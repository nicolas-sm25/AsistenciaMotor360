package modelo.enumeraciones;

/**
 * Categorías de las unidades móviles de la flota de rescate.
 */
public enum TipoUnidad {

    /** Grúa para remolque de vehículos. */
    GRUA("Grúa"),

    /** Motocicleta de respuesta rápida. */
    MOTO_RAPIDA("Moto de respuesta rápida"),

    /** Vehículo utilitario. */
    UTILITARIO("Utilitario"),

    /** Coche ligero. */
    LIVIANO("Coche ligero");

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Crea una constante con su texto legible.
     *
     * @param descripcion texto para mostrar
     */
    TipoUnidad(String descripcion) {
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