package pruebas;

import controlador.ControladorAsistencia;
import modelo.entidades.Cliente;
import modelo.entidades.Maletin;
import modelo.entidades.Repuesto;
import modelo.entidades.Solicitud;
import modelo.entidades.Tecnico;
import modelo.entidades.UnidadMovil;
import modelo.enumeraciones.Disponibilidad;
import modelo.enumeraciones.EstatusUnidad;
import modelo.enumeraciones.Nivel;
import modelo.enumeraciones.TipoCliente;
import modelo.enumeraciones.TipoServicio;
import modelo.enumeraciones.TipoUnidad;
import modelo.excepciones.ReglaNegocioException;

/**
 * Prueba de consola del flujo completo de Asistencia Motor 360 sin interfaz gráfica:
 * registrar, solicitar, priorizar, asignar, solucionar, deshacer y exportar CSV.
 */
public class PruebaConsola {

    /**
     * Ejecuta el flujo de prueba en consola.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        ControladorAsistencia controlador = new ControladorAsistencia();
        try {
            UnidadMovil grua = controlador.registrarUnidad(
                    TipoUnidad.GRUA, EstatusUnidad.OPERANDO, "Norte", Disponibilidad.DISPONIBLE);
            UnidadMovil moto = controlador.registrarUnidad(
                    TipoUnidad.MOTO_RAPIDA, EstatusUnidad.OPERANDO, "Centro", Disponibilidad.DISPONIBLE);
            Tecnico tecnico1 = controlador.registrarTecnico(
                    "T-01", "Carlos Pérez", TipoServicio.REMOLQUE, "Norte");
            Tecnico tecnico2 = controlador.registrarTecnico(
                    "T-02", "Laura Gómez", TipoServicio.BATERIA, "Centro");
            Cliente particular = controlador.registrarCliente(
                    "1001", "Andrea Rojas", "3001234567", TipoCliente.PARTICULAR);
            Cliente flota = controlador.registrarCliente(
                    "9001", "Transportes Andina", "3109876543", TipoCliente.FLOTA);

            Solicitud solOrdinaria = controlador.crearSolicitud(
                    particular.getDocumento(), TipoServicio.BATERIA, "Calle 45 # 12-30",
                    Nivel.BAJO, Nivel.BAJO, Nivel.BAJO);
            Solicitud solEscalada = controlador.crearSolicitud(
                    particular.getDocumento(), TipoServicio.MECANICA, "Carrera 30 # 20-10",
                    Nivel.MEDIO, Nivel.BAJO, Nivel.MEDIO);
            Solicitud solPrioritaria = controlador.crearSolicitud(
                    flota.getDocumento(), TipoServicio.REMOLQUE, "Autopista Norte km 8",
                    Nivel.ALTO, Nivel.ALTO, Nivel.ALTO);

            controlador.escalarSolicitud(solEscalada.getNumero());

            System.out.println("Próxima solicitud en turno: " + controlador.verSiguienteSolicitud());
            Solicitud atendida1 = controlador.asignarRecursos(grua.getId(), tecnico1.getIdentificacion());
            System.out.println("Atendida: " + atendida1);

            Solicitud atendida2 = controlador.asignarRecursos(moto.getId(), tecnico2.getIdentificacion());
            System.out.println("Atendida: " + atendida2);

            controlador.marcarSolucionada(atendida1.getNumero());
            System.out.println("Solucionada: #" + atendida1.getNumero());

            Maletin maletin = controlador.registrarMaletin("M-10");
            controlador.retirarMaletinDeEstanteria();
            controlador.enviarMaletinAAuditoria(maletin.getCodigo());
            controlador.auditarSiguienteMaletin();

            Repuesto repuesto = controlador.registrarRepuesto("R-10", "Batería 12V");
            controlador.despacharRepuesto();
            System.out.println("Repuesto despachado: " + repuesto);

            String deshecha = controlador.deshacer();
            System.out.println("Deshecho: " + deshecha);

            controlador.exportarCsv("reporte_solucionadas.csv");
            System.out.println("CSV exportado correctamente. Pendiente ordinaria #"
                    + solOrdinaria.getNumero() + " y prioritaria #" + solPrioritaria.getNumero());
        } catch (ReglaNegocioException e) {
            System.err.println("Error de regla de negocio: " + e.getMessage());
        }
    }
}