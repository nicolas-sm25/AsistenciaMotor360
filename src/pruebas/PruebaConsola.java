package pruebas;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
 * Prueba integral en consola de las reglas de negocio, estructuras propias,
 * pilas de maletines y repuestos, estación de auditoría, deshacer y exportación CSV.
 */
public class PruebaConsola {

    /**
     * Ejecuta los casos de prueba sobre el controlador.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        ControladorAsistencia controlador = new ControladorAsistencia();

        try {
            System.out.println("=== 1. REGISTRO DE RECURSOS Y REGLAS 1, 2, 3 ===");
            UnidadMovil grua = controlador.registrarUnidad(
                    TipoUnidad.GRUA, EstatusUnidad.OPERANDO, "Norte", Disponibilidad.DISPONIBLE);
            UnidadMovil moto = controlador.registrarUnidad(
                    TipoUnidad.MOTO_RAPIDA, EstatusUnidad.OPERANDO, "Centro", Disponibilidad.DISPONIBLE);
            UnidadMovil utilitarioTaller = controlador.registrarUnidad(
                    TipoUnidad.UTILITARIO, EstatusUnidad.EN_MANTENIMIENTO, "Sur", Disponibilidad.DISPONIBLE);
            System.out.println("Unidades registradas: " + controlador.getUnidades().tamano()
                    + " | Listas: " + controlador.getUnidadesListas().tamano()
                    + " | En taller: " + controlador.getUnidadesEnMantenimiento().tamano());
            System.out.println("UUID generado Grúa: " + grua.getId());

            Tecnico tec1 = controlador.registrarTecnico(
                    "T-001", "Carlos Pérez", TipoServicio.REMOLQUE, "Norte");
            Tecnico tec2 = controlador.registrarTecnico(
                    "T-002", "Laura Gómez", TipoServicio.BATERIA, "Centro");
            System.out.println("Técnicos registrados: " + controlador.getTecnicos().tamano()
                    + " | Libres: " + controlador.getTecnicosLibres().tamano());

            Cliente cliParticular = controlador.registrarCliente(
                    "1001", "Andrea Rojas", "3001234567", TipoCliente.PARTICULAR);
            Cliente cliFlota = controlador.registrarCliente(
                    "9001", "Transportes Andina", "3109876543", TipoCliente.FLOTA);
            Cliente cliAseguradora = controlador.registrarCliente(
                    "8001", "Seguros Bolívar Vial", "3205554433", TipoCliente.ASEGURADORA);
            System.out.println("Clientes registrados: " + controlador.getClientes().tamano());

            System.out.println("\n=== 2. VALIDACIONES DE CONSTRUCTORES, DUPLICADOS Y REGLA 7 ===");
            probarExcepcion("Unidad en mantenimiento y asignada a la vez", () ->
                    controlador.registrarUnidad(TipoUnidad.LIVIANO, EstatusUnidad.EN_MANTENIMIENTO,
                            "Occidente", Disponibilidad.ASIGNADA));
            probarExcepcion("Técnico con identificación duplicada", () ->
                    controlador.registrarTecnico("T-001", "Otro Técnico", TipoServicio.LLANTAS, "Sur"));
            probarExcepcion("Solicitud con cliente inexistente", () ->
                    controlador.crearSolicitud("999999", TipoServicio.BATERIA, "Calle 10",
                            Nivel.BAJO, Nivel.BAJO, Nivel.BAJO));

            System.out.println("\n=== 3. CREACIÓN Y PRIORIZACIÓN DE SOLICITUDES (REGLAS 10 Y 11) ===");
            Solicitud s1 = controlador.crearSolicitud(
                    cliParticular.getDocumento(), TipoServicio.BATERIA, "Estacionamiento Calle 45",
                    Nivel.BAJO, Nivel.BAJO, Nivel.BAJO);
            Solicitud s2 = controlador.crearSolicitud(
                    cliParticular.getDocumento(), TipoServicio.LLANTAS, "Carrera 15 # 80-10",
                    Nivel.BAJO, Nivel.MEDIO, Nivel.BAJO);
            Solicitud s3 = controlador.crearSolicitud(
                    cliFlota.getDocumento(), TipoServicio.REMOLQUE, "Autopista Norte km 6",
                    Nivel.ALTO, Nivel.ALTO, Nivel.MEDIO);
            Solicitud s4 = controlador.crearSolicitud(
                    cliAseguradora.getDocumento(), TipoServicio.MECANICA, "Avenida Boyacá # 26",
                    Nivel.ALTO, Nivel.ALTO, Nivel.ALTO);

            System.out.println("Ordinarias en cola: " + controlador.getColaOrdinaria().tamano()
                    + " -> Frente: " + primerElemento(controlador.getColaOrdinaria()));
            System.out.println("Prioritarias en cola: " + controlador.getColaPrioritaria().tamano()
                    + " -> Frente: " + primerElemento(controlador.getColaPrioritaria()));

            System.out.println("\n=== 4. ESCALAMIENTO MANUAL (REGLA 11) ===");
            controlador.escalarSolicitud(s2.getNumero());
            System.out.println("Tras escalar #" + s2.getNumero()
                    + " -> Ordinarias: " + controlador.getColaOrdinaria().tamano()
                    + " | Prioritarias: " + controlador.getColaPrioritaria().tamano());

            System.out.println("\n=== 5. ASIGNACIÓN DE RECURSOS (REGLAS 4, 5, 6 Y 8) ===");
            probarExcepcion("Asignar unidad en mantenimiento", () ->
                    controlador.asignarRecursos(utilitarioTaller.getId(), tec1.getIdentificacion()));

            Solicitud atendida1 = controlador.asignarRecursos(grua.getId(), tec1.getIdentificacion());
            System.out.println("Despachada 1: " + atendida1 + " | Estado: " + atendida1.getEstado());

            probarExcepcion("Reasignar unidad ya asignada", () ->
                    controlador.asignarRecursos(grua.getId(), tec2.getIdentificacion()));
            probarExcepcion("Reasignar técnico ya en servicio", () ->
                    controlador.asignarRecursos(moto.getId(), tec1.getIdentificacion()));
            probarExcepcion("Enviar a mantenimiento unidad asignada", () ->
                    controlador.enviarUnidadAMantenimiento(grua.getId()));

            Solicitud atendida2 = controlador.asignarRecursos(moto.getId(), tec2.getIdentificacion());
            System.out.println("Despachada 2: " + atendida2 + " | Estado: " + atendida2.getEstado());
            System.out.println("Solicitudes activas: " + controlador.getSolicitudesActivas().tamano()
                    + " | Unidades listas: " + controlador.getUnidadesListas().tamano()
                    + " | Técnicos libres: " + controlador.getTecnicosLibres().tamano());

            System.out.println("\n=== 6. SOLUCIÓN DE SERVICIOS Y LIBERACIÓN (REGLA 9) ===");
            controlador.marcarSolucionada(atendida1.getNumero());
            System.out.println("Solucionada #" + atendida1.getNumero()
                    + " -> Activas: " + controlador.getSolicitudesActivas().tamano()
                    + " | Solucionadas: " + controlador.getSolicitudesSolucionadas().tamano()
                    + " | Grúa lista: " + grua.estaListaParaDespacho()
                    + " | Técnico T-001 libre: " + tec1.estaLibre());

            System.out.println("\n=== 7. MALETINES, AUDITORÍA Y REPUESTOS ===");
            Maletin m1 = controlador.registrarMaletin("M-01");
            Maletin m2 = controlador.registrarMaletin("M-02");
            m2.agregarPieza("Fusible 20A");
            m2.reemplazarPieza(0, "Fusible 30A");
            System.out.println("Cima estantería maletines: "
                    + primerElemento(controlador.getMaletinesEstanteria()));

            Maletin retirado = controlador.retirarMaletinDeEstanteria();
            System.out.println("Retirado de estantería: " + retirado
                    + " | En servicio: " + controlador.getMaletinesEnServicio().tamano());

            controlador.enviarMaletinAAuditoria(retirado.getCodigo());
            System.out.println("En estación de auditoría: " + controlador.getMaletinesEnAuditoria().tamano()
                    + " | Auditado antes de revisar: " + retirado.isAuditado());

            Maletin auditado = controlador.auditarSiguienteMaletin();
            System.out.println("Auditado y reincorporado: " + auditado
                    + " | Auditado: " + auditado.isAuditado()
                    + " | En estantería: " + controlador.getMaletinesEstanteria().tamano());

            Repuesto r1 = controlador.registrarRepuesto("R-01", "Batería 12V");
            Repuesto r2 = controlador.registrarRepuesto("R-02", "Llanta de repuesto");
            System.out.println("Cima pila repuestos: " + primerElemento(controlador.getRepuestos()));
            Repuesto despachado = controlador.despacharRepuesto();
            System.out.println("Repuesto despachado: " + despachado
                    + " | Nueva cima: " + primerElemento(controlador.getRepuestos()));

            System.out.println("\n=== 8. DESHACER TRANSACCIONES ===");
            System.out.println("Undo 1: " + controlador.deshacer());
            System.out.println("Cima repuestos restaurada: " + primerElemento(controlador.getRepuestos()));
            System.out.println("Undo 2: " + controlador.deshacer());
            System.out.println("Maletines en auditoría tras deshacer auditoría: "
                    + controlador.getMaletinesEnAuditoria().tamano());

            System.out.println("\n=== 9. EXPORTACIÓN CSV ===");
            String rutaCsv = "reporte_solucionadas.csv";
            controlador.exportarCsv(rutaCsv);
            imprimirCsv(rutaCsv);

            System.out.println("\n=== 10. ESTADO FINAL ===");
            System.out.println("Pendientes en cola prioritaria: " + controlador.getColaPrioritaria().tamano()
                    + " -> Frente: " + primerElemento(controlador.getColaPrioritaria()));
            System.out.println("Pendientes en cola ordinaria: " + controlador.getColaOrdinaria().tamano()
                    + " -> Frente: " + primerElemento(controlador.getColaOrdinaria()));
            System.out.println("Activas en atención: " + controlador.getSolicitudesActivas().tamano());
            System.out.println("Solucionadas hoy: " + controlador.getSolicitudesSolucionadas().tamano());
            System.out.println("Eventos en historial: " + controlador.getHistorial().tamano());

            UUID idVerificado = grua.getId();
            if (idVerificado != null && m1 != null && r1 != null && r2 != null
                    && s1 != null && s3 != null && s4 != null) {
                System.out.println("\nPrueba integral finalizada con éxito.");
            }
        } catch (ReglaNegocioException e) {
            System.err.println("Fallo inesperado en prueba integral: " + e.getMessage());
        }
    }

    /**
     * Obtiene el primer elemento de una estructura consultable sin extraerlo.
     *
     * @param <T> tipo de elemento
     * @param estructura estructura consultable
     * @return primer elemento o null si está vacía
     */
    private static <T> T primerElemento(modelo.estructuras.contratos.IConsultable<T> estructura) {
        if (estructura == null || estructura.estaVacia()) {
            return null;
        }
        return estructura.obtener(0);
    }

    /**
     * Ejecuta una operación que debe lanzar {@link ReglaNegocioException} y muestra el resultado.
     *
     * @param caso descripción del caso de prueba
     * @param accion operación a ejecutar
     */
    private static void probarExcepcion(String caso, EjecutablePrueba accion) {
        try {
            accion.ejecutar();
            System.err.println("[FALLO] Se esperaba excepción en: " + caso);
        } catch (ReglaNegocioException e) {
            System.out.println("[OK] " + caso + " -> " + e.getMessage());
        }
    }

    /**
     * Lee e imprime en consola el contenido del archivo CSV generado.
     *
     * @param ruta ruta del archivo CSV
     */
    private static void imprimirCsv(String ruta) {
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println("CSV > " + linea);
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer el CSV generado: " + e.getMessage());
        }
    }

    /**
     * Interfaz auxiliar para probar llamadas que lanzan {@link ReglaNegocioException}.
     */
    @FunctionalInterface
    private interface EjecutablePrueba {

        /**
         * Ejecuta la operación de prueba.
         *
         * @throws ReglaNegocioException si se viola una regla de negocio
         */
        void ejecutar() throws ReglaNegocioException;
    }
}