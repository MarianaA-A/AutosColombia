import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Parqueadero {

    private Map<String, TicketInfo> ticketsActivos;
    private Map<String, Double> tarifaPorHora;

    public Parqueadero() {
        this.ticketsActivos = new HashMap<>();
        this.tarifaPorHora = new HashMap<>();
        this.tarifaPorHora.put("Automóvil", 8000.0);
        this.tarifaPorHora.put("SUV", 12000.0);
    }

    private static class TicketInfo {
        String tipo;
        LocalDateTime ingreso;

        TicketInfo(String tipo, LocalDateTime ingreso) {
            this.tipo = tipo;
            this.ingreso = ingreso;
        }
    }

    private boolean validarTipoVehiculo(String tipo) {
        return tarifaPorHora.containsKey(tipo);
    }

    public String registrarIngreso(String placa, String tipoVehiculo) {
        System.out.println("\n--- PROCESANDO INGRESO: " + placa + " ---");
        
        if (!validarTipoVehiculo(tipoVehiculo)) {
            return "ERROR: El tipo de vehículo '" + tipoVehiculo + "' no está permitido en este parqueadero.";
        }
        
        if (ticketsActivos.containsKey(placa)) {
            return "ERROR: El vehículo " + placa + " ya se encuentra registrado adentro.";
        }

        LocalDateTime horaIngreso = LocalDateTime.now();
        ticketsActivos.put(placa, new TicketInfo(tipoVehiculo, horaIngreso));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "EXITO: Ticket Generado. Ingreso de " + tipoVehiculo + " con placa " + placa + " a las " + horaIngreso.format(formatter) + ".";
    }

    public String registrarSalidaYCobrar(String placa, int horasSimuladas) {
        System.out.println("\n--- PROCESANDO SALIDA: " + placa + " ---");
        
        if (!ticketsActivos.containsKey(placa)) {
            return "ERROR: Vehículo no encontrado. Procesar como Ticket Perdido (tarifa plena).";
        }

        TicketInfo datosTicket = ticketsActivos.get(placa);
        double totalPagar = horasSimuladas * tarifaPorHora.get(datosTicket.tipo);
        
        ticketsActivos.remove(placa);
        
        return "EXITO: Salida registrada.\n" +
               "Placa: " + placa + " (" + datosTicket.tipo + ")\n" +
               "Tiempo cobrado: " + horasSimuladas + " horas.\n" +
               "Total a Pagar: $" + String.format("%,.0f", totalPagar) + " COP.";
    }

    public static void main(String[] args) {
        Parqueadero sistema = new Parqueadero();
        
        String res1 = sistema.registrarIngreso("ABC-123", "Automóvil");
        System.out.println(res1);
        
        String res2 = sistema.registrarIngreso("XYZ-987", "Motocicleta");
        System.out.println(res2);
        
        String res3 = sistema.registrarSalidaYCobrar("ABC-123", 3);
        System.out.println(res3);
    }
}
