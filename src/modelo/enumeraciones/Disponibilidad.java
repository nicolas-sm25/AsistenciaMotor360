package modelo.enumeraciones;

/**
 * Nivel de disponibilidad de una unidad móvil para un nuevo despacho.
 */
public enum Disponibilidad {

    /** La unidad está lista para ser asignada. */
    DISPONIBLE("Disponible"),

    /** La unidad ya está asignada a un servicio. */
    ASIGNADA("Asignada");

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Crea una constante con su texto legible.
     *
     * @param descripcion texto para mostrar
     */
    Disponibilidad(String descripcion) {
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