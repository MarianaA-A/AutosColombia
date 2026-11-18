package autoscolombia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Parqueadero {

    private Map<String, TicketInfo> ticketsActivos;
    private Map<String, Double> tarifaPorHora;
    private Map<String, Double> tarifaMensual;
    private Map<String, LocalDate> mensualidadesActivas;
    private Map<String, String> usuarios;
    private Map<String, Boolean> celdas;
    private Map<String, Double> recaudoPorMetodo;
    private int consecutivoRecibo;

    private static final double RECARGO_NOCTURNO = 15000.0;
    private static final int HORAS_TICKET_PERDIDO = 17;

    public Parqueadero() {
        this.ticketsActivos = new HashMap<>();
        this.mensualidadesActivas = new HashMap<>();
        this.consecutivoRecibo = 450;

        this.tarifaPorHora = new HashMap<>();
        this.tarifaPorHora.put("Automóvil", 8000.0);
        this.tarifaPorHora.put("SUV", 12000.0);

        this.tarifaMensual = new HashMap<>();
        this.tarifaMensual.put("Automóvil", 180000.0);
        this.tarifaMensual.put("SUV", 240000.0);

        this.usuarios = new HashMap<>();
        this.usuarios.put("admin@autos.com", "Administrador");
        this.usuarios.put("operario@autos.com", "Operario");

        this.celdas = new HashMap<>();
        this.celdas.put("A1", true);
        this.celdas.put("A2", true);
        this.celdas.put("B1", true);

        this.recaudoPorMetodo = new HashMap<>();
        this.recaudoPorMetodo.put("Efectivo", 0.0);
        this.recaudoPorMetodo.put("Transferencia / QR", 0.0);
        this.recaudoPorMetodo.put("Tarjeta", 0.0);
    }

    private static class TicketInfo {
        String tipo;
        String celdaAsignada;
        LocalDateTime ingreso;

        TicketInfo(String tipo, String celdaAsignada, LocalDateTime ingreso) {
            this.tipo = tipo;
            this.celdaAsignada = celdaAsignada;
            this.ingreso = ingreso;
        }
    }

    public boolean iniciarSesion(String correo, String rolEsperado) {
        System.out.println("\n--- AUTENTICANDO USUARIO ---");
        if (usuarios.containsKey(correo) && usuarios.get(correo).equals(rolEsperado)) {
            System.out.println("EXITO: Sesion iniciada como " + rolEsperado + " (" + correo + ")");
            return true;
        }
        System.out.println("ERROR: Credenciales o rol incorrecto (Error 403).");
        return false;
    }

    public String registrarIngreso(String placa, String tipoVehiculo, String celda) {
        System.out.println("\n--- PROCESANDO INGRESO: " + placa + " ---");

        if (!tarifaPorHora.containsKey(tipoVehiculo)) {
            return "ERROR: El tipo de vehiculo '" + tipoVehiculo + "' no esta permitido (Solo Automovil o SUV).";
        }
        if (ticketsActivos.containsKey(placa)) {
            return "ERROR: El vehiculo " + placa + " ya se encuentra registrado adentro.";
        }
        if (!celdas.containsKey(celda) || !celdas.get(celda)) {
            return "ERROR: La celda " + celda + " no existe o esta ocupada.";
        }

        LocalDateTime horaIngreso = LocalDateTime.now();
        ticketsActivos.put(placa, new TicketInfo(tipoVehiculo, celda, horaIngreso));
        celdas.put(celda, false);

        boolean tieneMensualidad = verificarMensualidadVigente(placa);
        String modalidad = tieneMensualidad ? "MENSUALIDAD VIGENTE" : "CLIENTE OCASIONAL";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "EXITO: Ticket Generado [" + modalidad + "]. Ingreso de " + tipoVehiculo +
               " (" + placa + ") en Celda " + celda + " a las " + horaIngreso.format(formatter) + ".";
    }

    public String registrarMensualidad(String placa, String tipoVehiculo, String metodoPago) {
        System.out.println("\n--- REGISTRO / RENOVACION DE MENSUALIDAD: " + placa + " ---");

        if (!tarifaMensual.containsKey(tipoVehiculo)) {
            return "ERROR: Tipo de vehiculo no valido para mensualidad.";
        }
        if (!recaudoPorMetodo.containsKey(metodoPago)) {
            return "ERROR: Metodo de pago no soportado.";
        }

        double valorPlan = tarifaMensual.get(tipoVehiculo);
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaVencimiento = fechaInicio.plusDays(30);

        mensualidadesActivas.put(placa, fechaVencimiento);
        registrarRecaudo(metodoPago, valorPlan);
        consecutivoRecibo++;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "COMPROBANTE #" + consecutivoRecibo + " | Mensualidad Activada\n" +
               "Placa: " + placa + " (" + tipoVehiculo + ") | Metodo: " + metodoPago + "\n" +
               "Vigencia: " + fechaInicio.format(fmt) + " hasta " + fechaVencimiento.format(fmt) + "\n" +
               "Total Pagado: $" + String.format("%,.0f", valorPlan) + " COP.";
    }

    public boolean verificarMensualidadVigente(String placa) {
        if (!mensualidadesActivas.containsKey(placa)) {
            return false;
        }
        return !mensualidadesActivas.get(placa).isBefore(LocalDate.now());
    }

    public String registrarSalidaYCobrar(String placa, int horasSimuladas, boolean salidaNocturna, String metodoPago) {
        System.out.println("\n--- PROCESANDO SALIDA Y PAGO: " + placa + " ---");

        if (!ticketsActivos.containsKey(placa)) {
            return "ERROR: Vehiculo no encontrado. Procesar como Ticket Perdido.";
        }

        TicketInfo datos = ticketsActivos.get(placa);
        ticketsActivos.remove(placa);
        celdas.put(datos.celdaAsignada, true);

        if (verificarMensualidadVigente(placa)) {
            return "EXITO: Salida autorizada por MENSUALIDAD VIGENTE (Exento de cobro).\n" +
                   "Placa: " + placa + " | Celda " + datos.celdaAsignada + " liberada.\n" +
                   "Total a Pagar: $0 COP.\n" +
                   "[HARDWARE] Senal enviada: Talanquera de salida ABIERTA.";
        }

        double subtotal = horasSimuladas * tarifaPorHora.get(datos.tipo);
        double recargo = salidaNocturna ? RECARGO_NOCTURNO : 0.0;
        double totalPagar = subtotal + recargo;

        registrarRecaudo(metodoPago, totalPagar);
        consecutivoRecibo++;

        return "COMPROBANTE #" + consecutivoRecibo + " | Salida Registrada y Celda " + datos.celdaAsignada + " Liberada\n" +
               "Placa: " + placa + " (" + datos.tipo + ") | Tiempo: " + horasSimuladas + "h\n" +
               "Recargo Nocturno: $" + String.format("%,.0f", recargo) + " COP | Metodo: " + metodoPago + "\n" +
               "Total Pagado: $" + String.format("%,.0f", totalPagar) + " COP.\n" +
               "[HARDWARE] Senal enviada: Talanquera de salida ABIERTA.";
    }

    public String procesarTicketPerdido(String placa, String tipoVehiculo, String celda, String metodoPago) {
        System.out.println("\n--- PROCESANDO TICKET PERDIDO: " + placa + " ---");

        if (!tarifaPorHora.containsKey(tipoVehiculo)) {
            return "ERROR: Tipo de vehiculo no valido.";
        }

        double totalPenalidad = HORAS_TICKET_PERDIDO * tarifaPorHora.get(tipoVehiculo);
        ticketsActivos.remove(placa);
        if (celdas.containsKey(celda)) {
            celdas.put(celda, true);
        }

        registrarRecaudo(metodoPago, totalPenalidad);
        consecutivoRecibo++;

        return "COMPROBANTE #" + consecutivoRecibo + " | Liquidacion Ticket Perdido (17 horas)\n" +
               "Placa: " + placa + " (" + tipoVehiculo + ") | Metodo: " + metodoPago + "\n" +
               "Total Pagado: $" + String.format("%,.0f", totalPenalidad) + " COP.\n" +
               "[HARDWARE] Senal enviada: Talanquera de salida ABIERTA.";
    }

    private void registrarRecaudo(String metodoPago, double monto) {
        double acumulado = recaudoPorMetodo.getOrDefault(metodoPago, 0.0);
        recaudoPorMetodo.put(metodoPago, acumulado + monto);
    }

    public void generarCierreCaja() {
        System.out.println("\n=== REPORTE CONSOLIDADO DE CIERRE DE CAJA ===");
        double totalGeneral = 0.0;
        for (Map.Entry<String, Double> entry : recaudoPorMetodo.entrySet()) {
            System.out.println("- " + entry.getKey() + ": $" + String.format("%,.0f", entry.getValue()) + " COP");
            totalGeneral += entry.getValue();
        }
        System.out.println("TOTAL RECAUDADO DEL DIA: $" + String.format("%,.0f", totalGeneral) + " COP");
    }

    public static void main(String[] args) {
        Parqueadero sistema = new Parqueadero();

        sistema.iniciarSesion("operario@autos.com", "Operario");

        System.out.println(sistema.registrarMensualidad("KLY-104", "SUV", "Transferencia / QR"));
        System.out.println(sistema.registrarIngreso("KLY-104", "SUV", "B1"));
        System.out.println(sistema.registrarSalidaYCobrar("KLY-104", 5, false, "N/A"));

        System.out.println(sistema.registrarIngreso("ABC-123", "Automóvil", "A1"));
        System.out.println(sistema.registrarIngreso("XYZ-987", "Motocicleta", "A2"));
        System.out.println(sistema.registrarIngreso("DEF-456", "SUV", "A1"));

        System.out.println(sistema.registrarSalidaYCobrar("ABC-123", 4, true, "Efectivo"));

        System.out.println(sistema.procesarTicketPerdido("MTR-551", "Automóvil", "A2", "Tarjeta"));

        sistema.generarCierreCaja();
    }
}