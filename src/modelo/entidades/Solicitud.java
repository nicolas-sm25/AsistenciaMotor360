package modelo.entidades;

import java.time.LocalDateTime;

import modelo.enumeraciones.EstadoSolicitud;
import modelo.enumeraciones.Nivel;
import modelo.enumeraciones.TipoServicio;
import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Solicitud de asistencia vial. Controla su ciclo de vida (pendiente, atendida, solucionada)
 * y protege las reglas 7, 8 y 9. Sus métodos reversibles permiten implementar el deshacer.
 */
public class Solicitud {

    /**
     * Puntaje mínimo desde el cual una solicitud se considera prioritaria (regla 11).
     */
    public static final int UMBRAL_PRIORITARIA = 8;

    /**
     * Número consecutivo de la solicitud.
     */
    private final int numero;

    /**
     * Cliente vinculado a la solicitud (regla 7).
     */
    private final Cliente cliente;

    /**
     * Tipo de servicio requerido.
     */
    private final TipoServicio tipoServicio;

    /**
     * Lugar donde ocurrió el incidente.
     */
    private final String ubicacion;

    /**
     * Nivel de peligro del incidente.
     */
    private final Nivel riesgo;

    /**
     * Nivel de afectación vial causada por el incidente.
     */
    private final Nivel congestion;

    /**
     * Nivel de severidad de la avería o el evento.
     */
    private final Nivel gravedad;

    /**
     * Fecha y hora en que se registró la solicitud.
     */
    private final LocalDateTime fechaRegistro;

    /**
     * Estado actual dentro del ciclo de vida.
     */
    private EstadoSolicitud estado;

    /**
     * Unidad asignada, o null si aún no tiene.
     */
    private UnidadMovil unidadAsignada;

    /**
     * Técnico asignado, o null si aún no tiene.
     */
    private Tecnico tecnicoAsignado;

    /**
     * Fecha y hora en que se solucionó, o null mientras no esté solucionada.
     */
    private LocalDateTime fechaSolucion;

    /**
     * Crea una solicitud pendiente validando sus datos obligatorios.
     *
     * @param numero número consecutivo, mayor que cero
     * @param cliente cliente vinculado
     * @param tipoServicio tipo de servicio requerido
     * @param ubicacion lugar del incidente
     * @param riesgo nivel de peligro
     * @param congestion nivel de afectación vial
     * @param gravedad nivel de severidad
     * @throws ReglaNegocioException si falta algún dato, el número no es positivo o no hay cliente
     */
    public Solicitud(int numero, Cliente cliente, TipoServicio tipoServicio, String ubicacion,
            Nivel riesgo, Nivel congestion, Nivel gravedad) throws ReglaNegocioException {
        if (numero <= 0) {
            throw new ReglaNegocioException("El número de la solicitud debe ser positivo");
        }
        if (cliente == null) {
            throw new ReglaNegocioException("Toda solicitud de asistencia debe estar vinculada a un cliente");
        }
        this.numero = numero;
        this.cliente = cliente;
        this.tipoServicio = Validador.objetoObligatorio(tipoServicio, "tipo de servicio");
        this.ubicacion = Validador.textoObligatorio(ubicacion, "ubicación");
        this.riesgo = Validador.objetoObligatorio(riesgo, "riesgo");
        this.congestion = Validador.objetoObligatorio(congestion, "congestión vial");
        this.gravedad = Validador.objetoObligatorio(gravedad, "gravedad");
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Calcula el puntaje de prioridad sumando riesgo, congestión, gravedad y tipo de cliente.
     *
     * @return puntaje entre 3 y 11
     */
    public int calcularPuntaje() {
        return riesgo.getPuntos() + congestion.getPuntos() + gravedad.getPuntos()
                + cliente.getTipo().getPuntos();
    }

    /**
     * Indica si la solicitud debe ir a la cola prioritaria (regla 11).
     *
     * @return true si el puntaje alcanza el umbral de prioridad
     */
    public boolean esPrioritaria() {
        return calcularPuntaje() >= UMBRAL_PRIORITARIA;
    }

    /**
     * Asigna una unidad y un técnico a la solicitud, ocupando ambos recursos.
     *
     * @param unidad unidad a asignar
     * @param tecnico técnico a asignar
     * @throws ReglaNegocioException si la solicitud no está pendiente, ya tiene recursos, falta un
     *         recurso, la unidad no puede asignarse (reglas 4 y 6) o el técnico ya tiene un servicio
     *         activo (regla 5)
     */
    public void asignarRecursos(UnidadMovil unidad, Tecnico tecnico) throws ReglaNegocioException {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden asignar recursos a una solicitud pendiente");
        }
        if (unidadAsignada != null) {
            throw new ReglaNegocioException("La solicitud ya tiene recursos asignados");
        }
        Validador.objetoObligatorio(unidad, "unidad");
        Validador.objetoObligatorio(tecnico, "técnico");
        ocuparRecursos(unidad, tecnico);
        this.unidadAsignada = unidad;
        this.tecnicoAsignado = tecnico;
    }

    /**
     * Retira los recursos asignados y los libera. Revierte {@link #asignarRecursos}.
     *
     * @throws ReglaNegocioException si la solicitud no está pendiente o no tiene recursos asignados
     */
    public void desasignarRecursos() throws ReglaNegocioException {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden retirar recursos de una solicitud pendiente");
        }
        if (unidadAsignada == null) {
            throw new ReglaNegocioException("La solicitud no tiene recursos asignados");
        }
        unidadAsignada.liberar();
        tecnicoAsignado.liberar();
        unidadAsignada = null;
        tecnicoAsignado = null;
    }

    /**
     * Cambia la solicitud a atendida.
     *
     * @throws ReglaNegocioException si no está pendiente o no tiene recursos asignados (regla 8)
     */
    public void marcarAtendida() throws ReglaNegocioException {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("Solo una solicitud pendiente puede pasar a atendida");
        }
        if (unidadAsignada == null || tecnicoAsignado == null) {
            throw new ReglaNegocioException(
                    "No se puede atender una solicitud sin recursos asignados");
        }
        estado = EstadoSolicitud.ATENDIDA;
    }

    /**
     * Devuelve la solicitud de atendida a pendiente conservando sus recursos.
     * Revierte {@link #marcarAtendida}.
     *
     * @throws ReglaNegocioException si la solicitud no está atendida
     */
    public void revertirAtendida() throws ReglaNegocioException {
        if (estado != EstadoSolicitud.ATENDIDA) {
            throw new ReglaNegocioException("La solicitud no está atendida");
        }
        estado = EstadoSolicitud.PENDIENTE;
    }

    /**
     * Marca el servicio como solucionado y libera la unidad y el técnico (regla 9).
     *
     * @throws ReglaNegocioException si la solicitud no está atendida
     */
    public void marcarSolucionada() throws ReglaNegocioException {
        if (estado != EstadoSolicitud.ATENDIDA) {
            throw new ReglaNegocioException("Solo una solicitud atendida puede marcarse como solucionada");
        }
        unidadAsignada.liberar();
        tecnicoAsignado.liberar();
        estado = EstadoSolicitud.SOLUCIONADA;
        fechaSolucion = LocalDateTime.now();
    }

    /**
     * Reabre una solicitud solucionada, volviendo a ocupar sus recursos.
     * Revierte {@link #marcarSolucionada}.
     *
     * @throws ReglaNegocioException si no está solucionada o si alguno de sus recursos ya no está libre
     */
    public void reabrir() throws ReglaNegocioException {
        if (estado != EstadoSolicitud.SOLUCIONADA) {
            throw new ReglaNegocioException("La solicitud no está solucionada");
        }
        ocuparRecursos(unidadAsignada, tecnicoAsignado);
        estado = EstadoSolicitud.ATENDIDA;
        fechaSolucion = null;
    }

    /**
     * Ocupa la unidad y el técnico, y deshace la unidad si el técnico no puede ocuparse.
     *
     * @param unidad unidad a ocupar
     * @param tecnico técnico a ocupar
     * @throws ReglaNegocioException si alguno de los dos recursos no está libre
     */
    private void ocuparRecursos(UnidadMovil unidad, Tecnico tecnico) throws ReglaNegocioException {
        unidad.asignar();
        try {
            tecnico.ocupar();
        } catch (ReglaNegocioException e) {
            unidad.liberar();
            throw e;
        }
    }

    /**
     * Obtiene el número de la solicitud.
     *
     * @return número consecutivo
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Obtiene el cliente vinculado.
     *
     * @return cliente de la solicitud
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Obtiene el tipo de servicio requerido.
     *
     * @return tipo de servicio
     */
    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    /**
     * Obtiene la ubicación del incidente.
     *
     * @return lugar del incidente
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Obtiene el nivel de riesgo.
     *
     * @return nivel de peligro
     */
    public Nivel getRiesgo() {
        return riesgo;
    }

    /**
     * Obtiene el nivel de congestión vial.
     *
     * @return nivel de afectación vial
     */
    public Nivel getCongestion() {
        return congestion;
    }

    /**
     * Obtiene el nivel de gravedad.
     *
     * @return nivel de severidad
     */
    public Nivel getGravedad() {
        return gravedad;
    }

    /**
     * Obtiene la fecha de registro.
     *
     * @return fecha y hora de creación
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Obtiene el estado actual.
     *
     * @return estado de la solicitud
     */
    public EstadoSolicitud getEstado() {
        return estado;
    }

    /**
     * Obtiene la unidad asignada.
     *
     * @return unidad asignada o null si no tiene
     */
    public UnidadMovil getUnidadAsignada() {
        return unidadAsignada;
    }

    /**
     * Obtiene el técnico asignado.
     *
     * @return técnico asignado o null si no tiene
     */
    public Tecnico getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    /**
     * Obtiene la fecha de solución.
     *
     * @return fecha y hora de solución o null si aún no está solucionada
     */
    public LocalDateTime getFechaSolucion() {
        return fechaSolucion;
    }

    /**
     * Retorna una descripción corta de la solicitud para mostrar en la interfaz.
     *
     * @return número, tipo de servicio y cliente
     */
    @Override
    public String toString() {
        return "#" + numero + " " + tipoServicio.getDescripcion() + " - " + cliente.getNombre();
    }
}