package modelo.entidades;

import java.util.UUID;

import modelo.enumeraciones.Disponibilidad;
import modelo.enumeraciones.EstatusUnidad;
import modelo.enumeraciones.TipoUnidad;
import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Unidad móvil de la flota de rescate. Protege las reglas 1, 2, 4 y 6 en sus propios
 * métodos de cambio de estado.
 */
public class UnidadMovil {

    /**
     * Identificador universal único de la unidad (regla 1).
     */
    private final UUID id;

    /**
     * Categoría de la unidad.
     */
    private final TipoUnidad tipo;

    /**
     * Estatus operativo actual: operando o en mantenimiento.
     */
    private EstatusUnidad estatus;

    /**
     * Zona de operación de la unidad.
     */
    private String zona;

    /**
     * Disponibilidad actual: disponible o asignada.
     */
    private Disponibilidad disponibilidad;

    /**
     * Crea una unidad con un UUID nuevo, validando los cuatro datos obligatorios (regla 2).
     *
     * @param tipo categoría de la unidad
     * @param estatus estatus operativo inicial
     * @param zona zona de operación
     * @param disponibilidad disponibilidad inicial
     * @throws ReglaNegocioException si falta algún dato o si la unidad queda en mantenimiento
     *         y asignada a la vez
     */
    public UnidadMovil(TipoUnidad tipo, EstatusUnidad estatus, String zona, Disponibilidad disponibilidad)
            throws ReglaNegocioException {
        this.id = UUID.randomUUID();
        this.tipo = Validador.objetoObligatorio(tipo, "tipo de unidad");
        this.estatus = Validador.objetoObligatorio(estatus, "estatus");
        this.zona = Validador.textoObligatorio(zona, "zona de operación");
        this.disponibilidad = Validador.objetoObligatorio(disponibilidad, "disponibilidad");
        if (this.estatus == EstatusUnidad.EN_MANTENIMIENTO
                && this.disponibilidad == Disponibilidad.ASIGNADA) {
            throw new ReglaNegocioException("Una unidad en mantenimiento no puede estar asignada a un caso");
        }
    }

    /**
     * Asigna la unidad a un caso.
     *
     * @throws ReglaNegocioException si la unidad está en mantenimiento (regla 6) o ya está asignada
     *         (regla 4)
     */
    public void asignar() throws ReglaNegocioException {
        if (estatus == EstatusUnidad.EN_MANTENIMIENTO) {
            throw new ReglaNegocioException("La unidad está en mantenimiento y no puede asignarse a un caso");
        }
        if (disponibilidad == Disponibilidad.ASIGNADA) {
            throw new ReglaNegocioException("La unidad ya está asignada a otro caso");
        }
        disponibilidad = Disponibilidad.ASIGNADA;
    }

    /**
     * Libera la unidad para que quede disponible de nuevo.
     *
     * @throws ReglaNegocioException si la unidad no estaba asignada
     */
    public void liberar() throws ReglaNegocioException {
        if (disponibilidad != Disponibilidad.ASIGNADA) {
            throw new ReglaNegocioException("La unidad no está asignada a ningún caso");
        }
        disponibilidad = Disponibilidad.DISPONIBLE;
    }

    /**
     * Envía la unidad a mantenimiento.
     *
     * @throws ReglaNegocioException si la unidad está asignada a un caso o ya está en mantenimiento
     */
    public void enviarAMantenimiento() throws ReglaNegocioException {
        if (disponibilidad == Disponibilidad.ASIGNADA) {
            throw new ReglaNegocioException("No se puede enviar a mantenimiento una unidad asignada a un caso");
        }
        if (estatus == EstatusUnidad.EN_MANTENIMIENTO) {
            throw new ReglaNegocioException("La unidad ya está en mantenimiento");
        }
        estatus = EstatusUnidad.EN_MANTENIMIENTO;
    }

    /**
     * Saca la unidad de mantenimiento y la deja operando.
     *
     * @throws ReglaNegocioException si la unidad no estaba en mantenimiento
     */
    public void terminarMantenimiento() throws ReglaNegocioException {
        if (estatus != EstatusUnidad.EN_MANTENIMIENTO) {
            throw new ReglaNegocioException("La unidad no está en mantenimiento");
        }
        estatus = EstatusUnidad.OPERANDO;
    }

    /**
     * Indica si la unidad está lista para un nuevo despacho.
     *
     * @return true si está operando y disponible
     */
    public boolean estaListaParaDespacho() {
        return estatus == EstatusUnidad.OPERANDO && disponibilidad == Disponibilidad.DISPONIBLE;
    }

    /**
     * Obtiene el identificador único de la unidad.
     *
     * @return UUID de la unidad
     */
    public UUID getId() {
        return id;
    }

    /**
     * Obtiene la categoría de la unidad.
     *
     * @return tipo de unidad
     */
    public TipoUnidad getTipo() {
        return tipo;
    }

    /**
     * Obtiene el estatus operativo.
     *
     * @return estatus actual
     */
    public EstatusUnidad getEstatus() {
        return estatus;
    }

    /**
     * Obtiene la zona de operación.
     *
     * @return zona actual
     */
    public String getZona() {
        return zona;
    }

    /**
     * Cambia la zona de operación.
     *
     * @param zona nueva zona
     * @throws ReglaNegocioException si la zona es nula o está vacía
     */
    public void setZona(String zona) throws ReglaNegocioException {
        this.zona = Validador.textoObligatorio(zona, "zona de operación");
    }

    /**
     * Obtiene la disponibilidad.
     *
     * @return disponibilidad actual
     */
    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    /**
     * Retorna una descripción corta de la unidad para mostrar en la interfaz.
     *
     * @return tipo y primeros caracteres del UUID
     */
    @Override
    public String toString() {
        return tipo.getDescripcion() + " [" + id.toString().substring(0, 8) + "]";
    }
}