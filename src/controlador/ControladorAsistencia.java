package controlador;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import modelo.entidades.Cliente;
import modelo.entidades.Maletin;
import modelo.entidades.Repuesto;
import modelo.entidades.Solicitud;
import modelo.entidades.Tecnico;
import modelo.entidades.UnidadMovil;
import modelo.enumeraciones.Disponibilidad;
import modelo.enumeraciones.EstadoSolicitud;
import modelo.enumeraciones.EstadoTecnico;
import modelo.enumeraciones.EstatusUnidad;
import modelo.enumeraciones.Nivel;
import modelo.enumeraciones.TipoCliente;
import modelo.enumeraciones.TipoServicio;
import modelo.enumeraciones.TipoUnidad;
import modelo.estructuras.contratos.Condicion;
import modelo.estructuras.contratos.ICola;
import modelo.estructuras.contratos.IConsultable;
import modelo.estructuras.contratos.ILista;
import modelo.estructuras.contratos.IPila;
import modelo.estructuras.implementaciones.Cola;
import modelo.estructuras.implementaciones.ColaPrioridad;
import modelo.estructuras.implementaciones.ListaEnlazada;
import modelo.estructuras.implementaciones.Pila;
import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Fachada de la lógica del sistema Asistencia Motor 360. La vista solo habla con esta clase:
 * ella orquesta las entidades y las estructuras de datos, y lanza {@link ReglaNegocioException}
 * cuando una operación viola una regla de negocio.
 */
public class ControladorAsistencia {

    /**
     * Inventario de unidades móviles.
     */
    private final ILista<UnidadMovil> unidades = new ListaEnlazada<>();

    /**
     * Registro del personal técnico.
     */
    private final ILista<Tecnico> tecnicos = new ListaEnlazada<>();

    /**
     * Registro de clientes.
     */
    private final ILista<Cliente> clientes = new ListaEnlazada<>();

    /**
     * Cola FIFO de solicitudes ordinarias, atendidas estrictamente por orden de llegada.
     */
    private final ICola<Solicitud> colaOrdinaria = new Cola<>();

    /**
     * Cola de prioridad de solicitudes críticas, ordenada por puntaje.
     */
    private final ICola<Solicitud> colaPrioritaria = new ColaPrioridad<Solicitud>(
            (a, b) -> Integer.compare(a.calcularPuntaje(), b.calcularPuntaje()));

    /**
     * Solicitudes que tienen recursos asignados y se están atendiendo.
     */
    private final ILista<Solicitud> solicitudesActivas = new ListaEnlazada<>();

    /**
     * Solicitudes solucionadas, base del reporte de cierre.
     */
    private final ILista<Solicitud> solicitudesSolucionadas = new ListaEnlazada<>();

    /**
     * Estantería de maletines, de la que siempre sale el último apilado.
     */
    private final IPila<Maletin> estanteriaMaletines = new Pila<>();

    /**
     * Maletines que salieron de la estantería y están en servicio.
     */
    private final ILista<Maletin> maletinesEnServicio = new ListaEnlazada<>();

    /**
     * Estación de auditoría donde esperan los maletines que terminaron un servicio.
     */
    private final ICola<Maletin> estacionAuditoria = new Cola<>();

    /**
     * Pila de repuestos de alta rotación, de la que sale primero el último acomodado.
     */
    private final IPila<Repuesto> repuestos = new Pila<>();

    /**
     * Historial reciente de acciones, con la más nueva en la cima.
     */
    private final IPila<String> historial = new Pila<>();

    /**
     * Último número de solicitud asignado.
     */
    private int consecutivoSolicitud;

    /**
     * Registra una unidad móvil nueva con un UUID generado automáticamente.
     *
     * @param tipo categoría de la unidad
     * @param estatus estatus operativo inicial
     * @param zona zona de operación
     * @param disponibilidad disponibilidad inicial
     * @return unidad registrada
     * @throws ReglaNegocioException si falta algún dato o la combinación no es válida
     */
    public UnidadMovil registrarUnidad(TipoUnidad tipo, EstatusUnidad estatus, String zona,
            Disponibilidad disponibilidad) throws ReglaNegocioException {
        UnidadMovil unidad = new UnidadMovil(tipo, estatus, zona, disponibilidad);
        unidades.agregar(unidad);
        registrarHistorial("Unidad registrada: " + unidad);
        return unidad;
    }

    /**
     * Busca una unidad por su UUID.
     *
     * @param id identificador de la unidad
     * @return unidad encontrada
     * @throws ReglaNegocioException si no existe una unidad con ese identificador
     */
    public UnidadMovil buscarUnidad(UUID id) throws ReglaNegocioException {
        UnidadMovil unidad = unidades.buscar(u -> u.getId().equals(id));
        if (unidad == null) {
            throw new ReglaNegocioException("No existe una unidad con el id " + id);
        }
        return unidad;
    }

    /**
     * Cambia la zona de operación de una unidad.
     *
     * @param id identificador de la unidad
     * @param zona nueva zona
     * @throws ReglaNegocioException si la unidad no existe o la zona es inválida
     */
    public void actualizarZonaUnidad(UUID id, String zona) throws ReglaNegocioException {
        UnidadMovil unidad = buscarUnidad(id);
        unidad.setZona(zona);
        registrarHistorial("Zona actualizada: " + unidad + " -> " + unidad.getZona());
    }

    /**
     * Envía una unidad a mantenimiento.
     *
     * @param id identificador de la unidad
     * @throws ReglaNegocioException si la unidad no existe, está asignada o ya está en mantenimiento
     */
    public void enviarUnidadAMantenimiento(UUID id) throws ReglaNegocioException {
        UnidadMovil unidad = buscarUnidad(id);
        unidad.enviarAMantenimiento();
        registrarHistorial("Unidad en mantenimiento: " + unidad);
    }

    /**
     * Saca una unidad de mantenimiento y la deja operando.
     *
     * @param id identificador de la unidad
     * @throws ReglaNegocioException si la unidad no existe o no estaba en mantenimiento
     */
    public void terminarMantenimientoUnidad(UUID id) throws ReglaNegocioException {
        UnidadMovil unidad = buscarUnidad(id);
        unidad.terminarMantenimiento();
        registrarHistorial("Unidad operando de nuevo: " + unidad);
    }

    /**
     * Elimina una unidad del inventario.
     *
     * @param id identificador de la unidad
     * @throws ReglaNegocioException si la unidad no existe o está asignada a un caso
     */
    public void eliminarUnidad(UUID id) throws ReglaNegocioException {
        UnidadMovil unidad = buscarUnidad(id);
        if (unidad.getDisponibilidad() == Disponibilidad.ASIGNADA) {
            throw new ReglaNegocioException("No se puede eliminar una unidad asignada a un caso");
        }
        unidades.eliminarDato(unidad);
        registrarHistorial("Unidad eliminada: " + unidad);
    }

    /**
     * Registra un técnico nuevo, libre al crearse.
     *
     * @param identificacion identificación única del técnico
     * @param nombre nombre completo
     * @param especialidad área de especialidad
     * @param sectorPatrullaje sector donde patrulla
     * @return técnico registrado
     * @throws ReglaNegocioException si falta algún dato o la identificación ya existe
     */
    public Tecnico registrarTecnico(String identificacion, String nombre, TipoServicio especialidad,
            String sectorPatrullaje) throws ReglaNegocioException {
        Tecnico tecnico = new Tecnico(identificacion, nombre, especialidad, sectorPatrullaje);
        String codigo = tecnico.getIdentificacion();
        if (tecnicos.buscar(t -> t.getIdentificacion().equals(codigo)) != null) {
            throw new ReglaNegocioException("Ya existe un técnico con la identificación " + codigo);
        }
        tecnicos.agregar(tecnico);
        registrarHistorial("Técnico registrado: " + tecnico);
        return tecnico;
    }

    /**
     * Busca un técnico por su identificación.
     *
     * @param identificacion identificación del técnico
     * @return técnico encontrado
     * @throws ReglaNegocioException si no existe un técnico con esa identificación
     */
    public Tecnico buscarTecnico(String identificacion) throws ReglaNegocioException {
        Tecnico tecnico = tecnicos.buscar(t -> t.getIdentificacion().equals(identificacion));
        if (tecnico == null) {
            throw new ReglaNegocioException("No existe un técnico con la identificación " + identificacion);
        }
        return tecnico;
    }

    /**
     * Cambia el sector de patrullaje de un técnico.
     *
     * @param identificacion identificación del técnico
     * @param sectorPatrullaje nuevo sector
     * @throws ReglaNegocioException si el técnico no existe o el sector es inválido
     */
    public void actualizarSectorTecnico(String identificacion, String sectorPatrullaje)
            throws ReglaNegocioException {
        Tecnico tecnico = buscarTecnico(identificacion);
        tecnico.setSectorPatrullaje(sectorPatrullaje);
        registrarHistorial("Sector actualizado: " + tecnico + " -> " + tecnico.getSectorPatrullaje());
    }

    /**
     * Cambia la especialidad de un técnico.
     *
     * @param identificacion identificación del técnico
     * @param especialidad nueva especialidad
     * @throws ReglaNegocioException si el técnico no existe o la especialidad es nula
     */
    public void actualizarEspecialidadTecnico(String identificacion, TipoServicio especialidad)
            throws ReglaNegocioException {
        Tecnico tecnico = buscarTecnico(identificacion);
        tecnico.setEspecialidad(especialidad);
        registrarHistorial("Especialidad actualizada: " + tecnico);
    }

    /**
     * Elimina un técnico del registro.
     *
     * @param identificacion identificación del técnico
     * @throws ReglaNegocioException si el técnico no existe o tiene un servicio activo
     */
    public void eliminarTecnico(String identificacion) throws ReglaNegocioException {
        Tecnico tecnico = buscarTecnico(identificacion);
        if (tecnico.getEstado() == EstadoTecnico.EN_SERVICIO) {
            throw new ReglaNegocioException("No se puede eliminar un técnico con un servicio activo");
        }
        tecnicos.eliminarDato(tecnico);
        registrarHistorial("Técnico eliminado: " + tecnico);
    }

    /**
     * Registra un cliente nuevo.
     *
     * @param documento documento de identidad único
     * @param nombre nombre completo
     * @param telefono teléfono de contacto
     * @param tipo perfil del cliente
     * @return cliente registrado
     * @throws ReglaNegocioException si falta algún dato o el documento ya existe
     */
    public Cliente registrarCliente(String documento, String nombre, String telefono, TipoCliente tipo)
            throws ReglaNegocioException {
        Cliente cliente = new Cliente(documento, nombre, telefono, tipo);
        String codigo = cliente.getDocumento();
        if (clientes.buscar(c -> c.getDocumento().equals(codigo)) != null) {
            throw new ReglaNegocioException("Ya existe un cliente con el documento " + codigo);
        }
        clientes.agregar(cliente);
        registrarHistorial("Cliente registrado: " + cliente);
        return cliente;
    }

    /**
     * Busca un cliente por su documento.
     *
     * @param documento documento de identidad
     * @return cliente encontrado
     * @throws ReglaNegocioException si no existe un cliente con ese documento
     */
    public Cliente buscarCliente(String documento) throws ReglaNegocioException {
        Cliente cliente = clientes.buscar(c -> c.getDocumento().equals(documento));
        if (cliente == null) {
            throw new ReglaNegocioException("No existe un cliente con el documento " + documento);
        }
        return cliente;
    }

    /**
     * Cambia el teléfono de un cliente.
     *
     * @param documento documento del cliente
     * @param telefono nuevo teléfono
     * @throws ReglaNegocioException si el cliente no existe o el teléfono es inválido
     */
    public void actualizarTelefonoCliente(String documento, String telefono) throws ReglaNegocioException {
        Cliente cliente = buscarCliente(documento);
        cliente.setTelefono(telefono);
        registrarHistorial("Teléfono actualizado: " + cliente);
    }

    /**
     * Elimina un cliente del registro.
     *
     * @param documento documento del cliente
     * @throws ReglaNegocioException si el cliente no existe o tiene solicitudes en curso
     */
    public void eliminarCliente(String documento) throws ReglaNegocioException {
        Cliente cliente = buscarCliente(documento);
        if (tieneSolicitudes(colaOrdinaria, cliente) || tieneSolicitudes(colaPrioritaria, cliente)
                || tieneSolicitudes(solicitudesActivas, cliente)) {
            throw new ReglaNegocioException("No se puede eliminar un cliente con solicitudes en curso");
        }
        clientes.eliminarDato(cliente);
        registrarHistorial("Cliente eliminado: " + cliente);
    }

    /**
     * Crea una solicitud de asistencia y la encola: en la cola prioritaria si su puntaje alcanza
     * el umbral, o en la ordinaria si no.
     *
     * @param documentoCliente documento del cliente que la solicita
     * @param tipoServicio tipo de servicio requerido
     * @param ubicacion lugar del incidente
     * @param riesgo nivel de peligro
     * @param congestion nivel de afectación vial
     * @param gravedad nivel de severidad
     * @return solicitud creada
     * @throws ReglaNegocioException si el cliente no existe o falta algún dato
     */
    public Solicitud crearSolicitud(String documentoCliente, TipoServicio tipoServicio, String ubicacion,
            Nivel riesgo, Nivel congestion, Nivel gravedad) throws ReglaNegocioException {
        Cliente cliente = buscarCliente(documentoCliente);
        Solicitud solicitud = new Solicitud(consecutivoSolicitud + 1, cliente, tipoServicio, ubicacion,
                riesgo, congestion, gravedad);
        consecutivoSolicitud++;
        if (solicitud.esPrioritaria()) {
            colaPrioritaria.encolar(solicitud);
        } else {
            colaOrdinaria.encolar(solicitud);
        }
        registrarHistorial("Solicitud creada: " + solicitud + " (puntaje " + solicitud.calcularPuntaje() + ")");
        return solicitud;
    }

    /**
     * Promueve una solicitud ordinaria a la cola prioritaria, para casos que el operador
     * considera críticos aunque su puntaje no alcance el umbral.
     *
     * @param numero número de la solicitud
     * @return solicitud escalada
     * @throws ReglaNegocioException si la solicitud no está en la cola ordinaria
     */
    public Solicitud escalarSolicitud(int numero) throws ReglaNegocioException {
        Solicitud solicitud = extraerDeCola(colaOrdinaria, numero);
        if (solicitud == null) {
            throw new ReglaNegocioException("La solicitud #" + numero + " no está en la cola ordinaria");
        }
        colaPrioritaria.encolar(solicitud);
        registrarHistorial("Solicitud escalada a prioritaria: " + solicitud);
        return solicitud;
    }

    /**
     * Consulta la próxima solicitud a atender sin sacarla de su cola: primero la prioritaria
     * y, si no hay, la ordinaria más antigua.
     *
     * @return próxima solicitud o null si no hay ninguna pendiente
     */
    public Solicitud verSiguienteSolicitud() {
        if (!colaPrioritaria.estaVacia()) {
            return colaPrioritaria.verFrente();
        }
        return colaOrdinaria.verFrente();
    }

    /**
     * Asigna una unidad y un técnico a la próxima solicitud en turno, validando las reglas 4 a 8,
     * y la marca como atendida.
     *
     * @param idUnidad identificador de la unidad a asignar
     * @param identificacionTecnico identificación del técnico a asignar
     * @return solicitud atendida
     * @throws ReglaNegocioException si no hay solicitudes pendientes, la solicitud no tiene cliente,
     *         la unidad o el técnico no existen, o se incumple alguna regla de asignación
     */
    public Solicitud asignarRecursos(UUID idUnidad, String identificacionTecnico)
            throws ReglaNegocioException {
        Solicitud solicitud = verSiguienteSolicitud();
        if (solicitud == null) {
            throw new ReglaNegocioException("No hay solicitudes pendientes por atender");
        }
        if (solicitud.getCliente() == null) {
            throw new ReglaNegocioException("Toda solicitud de asistencia debe estar vinculada a un cliente");
        }
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden asignar recursos a una solicitud pendiente");
        }
        UnidadMovil unidad = buscarUnidad(idUnidad);
        Tecnico tecnico = buscarTecnico(identificacionTecnico);
        validarDisponibilidadRecursos(unidad, tecnico);

        solicitud.asignarRecursos(unidad, tecnico);
        solicitud.marcarAtendida();

        if (!colaPrioritaria.estaVacia()) {
            colaPrioritaria.desencolar();
        } else {
            colaOrdinaria.desencolar();
        }
        solicitudesActivas.agregar(solicitud);
        registrarHistorial("Recursos asignados: " + solicitud + " con " + unidad + " y " + tecnico);
        return solicitud;
    }

    /**
     * Verifica las reglas 4, 5 y 6 sobre la unidad y el técnico antes de asignar recursos.
     *
     * @param unidad unidad a validar
     * @param tecnico técnico a validar
     * @throws ReglaNegocioException si la unidad está en mantenimiento, no está disponible o el
     *         técnico no está libre
     */
    private void validarDisponibilidadRecursos(UnidadMovil unidad, Tecnico tecnico)
            throws ReglaNegocioException {
        if (unidad.getEstatus() == EstatusUnidad.EN_MANTENIMIENTO) {
            throw new ReglaNegocioException("La unidad está en mantenimiento y no puede asignarse a un caso");
        }
        if (unidad.getDisponibilidad() != Disponibilidad.DISPONIBLE) {
            throw new ReglaNegocioException("La unidad no está disponible para asignación");
        }
        if (!tecnico.estaLibre() || tecnico.getEstado() == EstadoTecnico.EN_SERVICIO) {
            throw new ReglaNegocioException("El técnico " + tecnico.getNombre() + " ya tiene un servicio activo");
        }
    }

    /**
     * Marca una solicitud en atención como solucionada, libera sus recursos y la retira del flujo
     * de pendientes.
     *
     * @param numero número de la solicitud
     * @return solicitud solucionada
     * @throws ReglaNegocioException si la solicitud no está en atención
     */
    public Solicitud marcarSolucionada(int numero) throws ReglaNegocioException {
        Solicitud solicitud = solicitudesActivas.buscar(s -> s.getNumero() == numero);
        if (solicitud == null) {
            throw new ReglaNegocioException("La solicitud #" + numero + " no está en atención");
        }
        solicitud.marcarSolucionada();
        solicitudesActivas.eliminarDato(solicitud);
        solicitudesSolucionadas.agregar(solicitud);
        registrarHistorial("Solicitud solucionada: " + solicitud);
        return solicitud;
    }

    /**
     * Deshace la última transacción ejecutada, retrocediendo en orden inverso.
     * La implementación se completa en el Paso 7.
     *
     * @return descripción de la acción deshecha
     * @throws ReglaNegocioException si no hay acciones por deshacer
     */
    public String deshacer() throws ReglaNegocioException {
        throw new ReglaNegocioException("La función deshacer se implementa en el Paso 7");
    }

    /**
     * Registra un maletín nuevo y lo apila en la estantería.
     *
     * @param codigo código del maletín
     * @return maletín registrado
     * @throws ReglaNegocioException si el código es inválido
     */
    public Maletin registrarMaletin(String codigo) throws ReglaNegocioException {
        Maletin maletin = new Maletin(codigo);
        estanteriaMaletines.apilar(maletin);
        registrarHistorial("Maletín apilado en la estantería: " + maletin);
        return maletin;
    }

    /**
     * Retira de la estantería el último maletín apilado y lo pone en servicio.
     *
     * @return maletín retirado
     * @throws ReglaNegocioException si no hay maletines en la estantería
     */
    public Maletin retirarMaletinDeEstanteria() throws ReglaNegocioException {
        if (estanteriaMaletines.estaVacia()) {
            throw new ReglaNegocioException("No hay maletines en la estantería");
        }
        Maletin maletin = estanteriaMaletines.desapilar();
        maletinesEnServicio.agregar(maletin);
        registrarHistorial("Maletín en servicio: " + maletin);
        return maletin;
    }

    /**
     * Envía un maletín en servicio a la estación de auditoría.
     *
     * @param codigo código del maletín
     * @return maletín enviado
     * @throws ReglaNegocioException si no hay un maletín en servicio con ese código
     */
    public Maletin enviarMaletinAAuditoria(String codigo) throws ReglaNegocioException {
        Maletin maletin = maletinesEnServicio.buscar(m -> m.getCodigo().equals(codigo));
        if (maletin == null) {
            throw new ReglaNegocioException("No hay un maletín en servicio con el código " + codigo);
        }
        maletinesEnServicio.eliminarDato(maletin);
        maletin.marcarParaAuditoria();
        estacionAuditoria.encolar(maletin);
        registrarHistorial("Maletín enviado a auditoría: " + maletin);
        return maletin;
    }

    /**
     * Audita el maletín que lleva más tiempo en la estación y lo devuelve a la estantería.
     *
     * @return maletín auditado
     * @throws ReglaNegocioException si no hay maletines en auditoría
     */
    public Maletin auditarSiguienteMaletin() throws ReglaNegocioException {
        if (estacionAuditoria.estaVacia()) {
            throw new ReglaNegocioException("No hay maletines en la estación de auditoría");
        }
        Maletin maletin = estacionAuditoria.desencolar();
        maletin.marcarAuditado();
        estanteriaMaletines.apilar(maletin);
        registrarHistorial("Maletín auditado y apilado: " + maletin);
        return maletin;
    }

    /**
     * Registra un repuesto de alta rotación y lo apila.
     *
     * @param codigo código del repuesto
     * @param nombre nombre del repuesto
     * @return repuesto registrado
     * @throws ReglaNegocioException si algún dato es inválido
     */
    public Repuesto registrarRepuesto(String codigo, String nombre) throws ReglaNegocioException {
        Repuesto repuesto = new Repuesto(codigo, nombre);
        repuestos.apilar(repuesto);
        registrarHistorial("Repuesto apilado: " + repuesto);
        return repuesto;
    }

    /**
     * Despacha el último repuesto acomodado.
     *
     * @return repuesto despachado
     * @throws ReglaNegocioException si no hay repuestos
     */
    public Repuesto despacharRepuesto() throws ReglaNegocioException {
        if (repuestos.estaVacia()) {
            throw new ReglaNegocioException("No hay repuestos disponibles");
        }
        Repuesto repuesto = repuestos.desapilar();
        registrarHistorial("Repuesto despachado: " + repuesto);
        return repuesto;
    }

    /**
     * Exporta a un archivo CSV las incidencias solucionadas el día de hoy.
     *
     * @param ruta ruta completa del archivo a crear
     * @throws ReglaNegocioException si la ruta es inválida o no se puede escribir el archivo
     */
    public void exportarCsv(String ruta) throws ReglaNegocioException {
        Validador.textoObligatorio(ruta, "ruta del archivo");
        LocalDate hoy = LocalDate.now();
        try (PrintWriter escritor = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(ruta), StandardCharsets.UTF_8))) {
            escritor.write('\uFEFF');
            escritor.println("numero,cliente,documento,servicio,ubicacion,riesgo,congestion,gravedad,"
                    + "puntaje,unidad,tecnico,registro,solucion");
            for (int i = 0; i < solicitudesSolucionadas.tamano(); i++) {
                Solicitud solicitud = solicitudesSolucionadas.obtener(i);
                if (solicitud.getFechaSolucion().toLocalDate().equals(hoy)) {
                    escritor.println(solicitud.getNumero() + ","
                            + campoCsv(solicitud.getCliente().getNombre()) + ","
                            + campoCsv(solicitud.getCliente().getDocumento()) + ","
                            + campoCsv(solicitud.getTipoServicio().getDescripcion()) + ","
                            + campoCsv(solicitud.getUbicacion()) + ","
                            + campoCsv(solicitud.getRiesgo().getDescripcion()) + ","
                            + campoCsv(solicitud.getCongestion().getDescripcion()) + ","
                            + campoCsv(solicitud.getGravedad().getDescripcion()) + ","
                            + solicitud.calcularPuntaje() + ","
                            + solicitud.getUnidadAsignada().getId() + ","
                            + campoCsv(solicitud.getTecnicoAsignado().getNombre()) + ","
                            + solicitud.getFechaRegistro() + ","
                            + solicitud.getFechaSolucion());
                }
            }
            if (escritor.checkError()) {
                throw new ReglaNegocioException("No se pudo escribir el archivo CSV");
            }
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo exportar el CSV: " + e.getMessage());
        }
        registrarHistorial("Reporte CSV exportado: " + ruta);
    }

    /**
     * Carga datos de ejemplo para probar la interfaz y grabar el video.
     *
     * @throws ReglaNegocioException si algún dato de ejemplo viola una regla de negocio
     */
    public void cargarDatosDemo() throws ReglaNegocioException {
        registrarUnidad(TipoUnidad.GRUA, EstatusUnidad.OPERANDO, "Norte", Disponibilidad.DISPONIBLE);
        registrarUnidad(TipoUnidad.MOTO_RAPIDA, EstatusUnidad.OPERANDO, "Centro", Disponibilidad.DISPONIBLE);
        registrarUnidad(TipoUnidad.UTILITARIO, EstatusUnidad.EN_MANTENIMIENTO, "Sur", Disponibilidad.DISPONIBLE);
        registrarTecnico("T-001", "Carlos Pérez", TipoServicio.MECANICA, "Norte");
        registrarTecnico("T-002", "Laura Gómez", TipoServicio.BATERIA, "Centro");
        registrarCliente("1001", "Andrea Rojas", "3001234567", TipoCliente.PARTICULAR);
        registrarCliente("900123", "Transportes Andina", "3109876543", TipoCliente.FLOTA);
        crearSolicitud("1001", TipoServicio.BATERIA, "Carrera 7 con calle 45", Nivel.BAJO, Nivel.BAJO,
                Nivel.BAJO);
        crearSolicitud("900123", TipoServicio.REMOLQUE, "Autopista Norte km 5", Nivel.ALTO, Nivel.ALTO,
                Nivel.MEDIO);
        registrarMaletin("M-01");
        registrarMaletin("M-02");
        registrarRepuesto("R-01", "Batería 12V");
        registrarRepuesto("R-02", "Llanta de repuesto");
    }

    /**
     * Consulta el inventario de unidades.
     *
     * @return unidades registradas, de solo lectura
     */
    public IConsultable<UnidadMovil> getUnidades() {
        return unidades;
    }

    /**
     * Consulta las unidades operando y listas para un nuevo despacho.
     *
     * @return unidades listas
     */
    public IConsultable<UnidadMovil> getUnidadesListas() {
        return filtrarUnidades(UnidadMovil::estaListaParaDespacho);
    }

    /**
     * Consulta las unidades asignadas a un servicio.
     *
     * @return unidades asignadas
     */
    public IConsultable<UnidadMovil> getUnidadesAsignadas() {
        return filtrarUnidades(u -> u.getDisponibilidad() == Disponibilidad.ASIGNADA);
    }

    /**
     * Consulta las unidades que están en el taller.
     *
     * @return unidades en mantenimiento
     */
    public IConsultable<UnidadMovil> getUnidadesEnMantenimiento() {
        return filtrarUnidades(u -> u.getEstatus() == EstatusUnidad.EN_MANTENIMIENTO);
    }

    /**
     * Consulta el personal técnico.
     *
     * @return técnicos registrados, de solo lectura
     */
    public IConsultable<Tecnico> getTecnicos() {
        return tecnicos;
    }

    /**
     * Consulta los técnicos que no tienen un servicio activo.
     *
     * @return técnicos libres
     */
    public IConsultable<Tecnico> getTecnicosLibres() {
        ILista<Tecnico> libres = new ListaEnlazada<>();
        for (int i = 0; i < tecnicos.tamano(); i++) {
            Tecnico tecnico = tecnicos.obtener(i);
            if (tecnico.estaLibre()) {
                libres.agregar(tecnico);
            }
        }
        return libres;
    }

    /**
     * Consulta los clientes registrados.
     *
     * @return clientes, de solo lectura
     */
    public IConsultable<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Consulta la cola de solicitudes ordinarias, con la más antigua en la posición 0.
     *
     * @return cola ordinaria, de solo lectura
     */
    public IConsultable<Solicitud> getColaOrdinaria() {
        return colaOrdinaria;
    }

    /**
     * Consulta la cola de solicitudes prioritarias, con la más urgente en la posición 0.
     *
     * @return cola prioritaria, de solo lectura
     */
    public IConsultable<Solicitud> getColaPrioritaria() {
        return colaPrioritaria;
    }

    /**
     * Consulta los rescates activos.
     *
     * @return solicitudes en atención
     */
    public IConsultable<Solicitud> getSolicitudesActivas() {
        return solicitudesActivas;
    }

    /**
     * Consulta las solicitudes solucionadas.
     *
     * @return solicitudes solucionadas
     */
    public IConsultable<Solicitud> getSolicitudesSolucionadas() {
        return solicitudesSolucionadas;
    }

    /**
     * Consulta los maletines de la estantería, con el próximo en salir en la posición 0.
     *
     * @return maletines en la estantería
     */
    public IConsultable<Maletin> getMaletinesEstanteria() {
        return estanteriaMaletines;
    }

    /**
     * Consulta los maletines que están en servicio.
     *
     * @return maletines en servicio
     */
    public IConsultable<Maletin> getMaletinesEnServicio() {
        return maletinesEnServicio;
    }

    /**
     * Consulta los maletines que esperan auditoría, con el más antiguo en la posición 0.
     *
     * @return maletines en la estación de auditoría
     */
    public IConsultable<Maletin> getMaletinesEnAuditoria() {
        return estacionAuditoria;
    }

    /**
     * Consulta los repuestos apilados, con el próximo en salir en la posición 0.
     *
     * @return repuestos disponibles
     */
    public IConsultable<Repuesto> getRepuestos() {
        return repuestos;
    }

    /**
     * Consulta el historial reciente de acciones, con la más nueva en la posición 0.
     *
     * @return historial de acciones
     */
    public IConsultable<String> getHistorial() {
        return historial;
    }

    /**
     * Agrega una línea con la hora actual al historial de acciones.
     *
     * @param descripcion texto de la acción realizada
     */
    private void registrarHistorial(String descripcion) {
        historial.apilar(LocalTime.now().withNano(0) + " - " + descripcion);
    }

    /**
     * Saca una solicitud de una cola sin alterar el orden de las demás.
     *
     * @param cola cola de la que se extrae
     * @param numero número de la solicitud buscada
     * @return solicitud extraída o null si no estaba en la cola
     */
    private Solicitud extraerDeCola(ICola<Solicitud> cola, int numero) {
        Solicitud encontrada = null;
        int total = cola.tamano();
        for (int i = 0; i < total; i++) {
            Solicitud actual = cola.desencolar();
            if (encontrada == null && actual.getNumero() == numero) {
                encontrada = actual;
            } else {
                cola.encolar(actual);
            }
        }
        return encontrada;
    }

    /**
     * Indica si una estructura de solicitudes contiene alguna de un cliente.
     *
     * @param origen estructura a revisar
     * @param cliente cliente buscado
     * @return true si hay al menos una solicitud del cliente
     */
    private boolean tieneSolicitudes(IConsultable<Solicitud> origen, Cliente cliente) {
        for (int i = 0; i < origen.tamano(); i++) {
            if (origen.obtener(i).getCliente() == cliente) {
                return true;
            }
        }
        return false;
    }

    /**
     * Construye una lista con las unidades que cumplen una condición.
     *
     * @param condicion criterio de filtrado
     * @return unidades que cumplen la condición
     */
    private IConsultable<UnidadMovil> filtrarUnidades(Condicion<UnidadMovil> condicion) {
        ILista<UnidadMovil> resultado = new ListaEnlazada<>();
        for (int i = 0; i < unidades.tamano(); i++) {
            UnidadMovil unidad = unidades.obtener(i);
            if (condicion.cumple(unidad)) {
                resultado.agregar(unidad);
            }
        }
        return resultado;
    }

    /**
     * Encierra un texto entre comillas y escapa las comillas internas, para el formato CSV.
     *
     * @param valor texto original
     * @return texto listo para una celda CSV
     */
    private String campoCsv(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}