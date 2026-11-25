package modelo.enumeraciones;

/**
 * Tipos de servicio de asistencia vial que ofrece la compañía.
 * Se usa como tipo de incidente de una solicitud y como especialidad de un técnico.
 */
public enum TipoServicio {

    /** Avería mecánica. */
    MECANICA("Avería mecánica"),

    /** Pinchazo de llanta. */
    LLANTAS("Pinchazo"),

    /** Recarga de batería. */
    BATERIA("Recarga de batería"),

    /** Remolque con grúa. */
    REMOLQUE("Remolque con grúa"),

    /** Cerrajería vehicular. */
    CERRAJERIA("Cerrajería vehicular"),

    /** Entrega de combustible. */
    COMBUSTIBLE("Entrega de combustible"),

    /** Diagnóstico preventivo en el lugar. */
    DIAGNOSTICO("Diagnóstico preventivo");

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Crea una constante con su texto legible.
     *
     * @param descripcion texto para mostrar
     */
    TipoServicio(String descripcion) {
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