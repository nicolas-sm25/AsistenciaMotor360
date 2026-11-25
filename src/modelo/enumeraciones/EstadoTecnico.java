package modelo.enumeraciones;

/**
 * Estado actual de un técnico respecto a los servicios.
 */
public enum EstadoTecnico {

    /** El técnico no tiene ningún servicio activo. */
    LIBRE("Libre"),

    /** El técnico está atendiendo un servicio. */
    EN_SERVICIO("En servicio");

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Crea una constante con su texto legible.
     *
     * @param descripcion texto para mostrar
     */
    EstadoTecnico(String descripcion) {
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