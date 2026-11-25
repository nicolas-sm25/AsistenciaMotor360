package modelo.enumeraciones;

/**
 * Estatus operativo de una unidad móvil.
 */
public enum EstatusUnidad {

    /** La unidad está operando normalmente. */
    OPERANDO("Operando"),

    /** La unidad está en mantenimiento y no puede asignarse a un caso. */
    EN_MANTENIMIENTO("En mantenimiento");

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Crea una constante con su texto legible.
     *
     * @param descripcion texto para mostrar
     */
    EstatusUnidad(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el texto legible de la constante.
     *
     * @return descripción para mostrar
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Retorna el texto legible, para que las listas desplegables muestren la descripción.
     *
     * @return descripción de la constante
     */
    @Override
    public String toString() {
        return descripcion;
    }
}