package autoscolombia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Parqueadero {

    private Map<String, TicketInfo> ticketsActivos;
    private Map<String, Double> tarifaPorHora;
    private Map<String, String> usuarios;
    private Map<String, Boolean> celdas;

    public Parqueadero() {
        this.ticketsActivos = new HashMap<>();
        
        this.tarifaPorHora = new HashMap<>();
        this.tarifaPorHora.put("Automóvil", 8000.0);
        this.tarifaPorHora.put("SUV", 12000.0);
        
        this.usuarios = new HashMap<>();
        this.usuarios.put("admin@autos.com", "Administrador");
        this.usuarios.put("operario@autos.com", "Operario");
        
        this.celdas = new HashMap<>();
        this.celdas.put("A1", true);
        this.celdas.put("A2", true);
        this.celdas.put("B1", true);
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
            System.out.println("EXITO: Sesion iniciada como " + rolEsperado);
            return true;
        }
        System.out.println("ERROR: Credenciales o rol incorrecto (Error 403).");
        return false;
    }

    public String registrarIngreso(String placa, String tipoVehiculo, String celda) {
        System.out.println("\n--- PROCESANDO INGRESO: " + placa + " ---");
        
        if (!tarifaPorHora.containsKey(tipoVehiculo)) {
            return "ERROR: El tipo de vehiculo '" + tipoVehiculo + "' no esta permitido.";
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
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "EXITO: Ticket Generado. Ingreso de " + tipoVehiculo + " (" + placa + ") en Celda " + celda + " a las " + horaIngreso.format(formatter) + ".";
    }

    public String registrarSalidaYCobrar(String placa, int horasSimuladas) {
        System.out.println("\n--- PROCESANDO SALIDA: " + placa + " ---");
        
        if (!ticketsActivos.containsKey(placa)) {
            return "ERROR: Vehiculo no encontrado. Procesar como Ticket Perdido.";
        }

        TicketInfo datos = ticketsActivos.get(placa);
        double totalPagar = horasSimuladas * tarifaPorHora.get(datos.tipo);
        
        ticketsActivos.remove(placa);
        celdas.put(datos.celdaAsignada, true); 
        
        return "EXITO: Salida registrada y Celda " + datos.celdaAsignada + " liberada.\n" +
               "Placa: " + placa + " (" + datos.tipo + ")\n" +
               "Tiempo cobrado: " + horasSimuladas + " horas.\n" +
               "Total a Pagar: $" + String.format("%,.0f", totalPagar) + " COP.";
    }

    public static void main(String[] args) {
        Parqueadero sistema = new Parqueadero();
        
        sistema.iniciarSesion("operario@autos.com", "Operario");
        
        System.out.println(sistema.registrarIngreso("ABC-123", "Automóvil", "A1"));
        System.out.println(sistema.registrarIngreso("XYZ-987", "Motocicleta", "A2")); 
        System.out.println(sistema.registrarIngreso("DEF-456", "SUV", "A1")); 
        
        System.out.println(sistema.registrarSalidaYCobrar("ABC-123", 3));
    }
}
