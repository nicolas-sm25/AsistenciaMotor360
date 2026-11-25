package modelo.enumeraciones;

/**
 * Perfil del cliente que solicita la asistencia, que influye en la prioridad de su solicitud.
 */
public enum TipoCliente {

    /** Conductor particular. */
    PARTICULAR("Particular", 0),

    /** Flota de transporte. */
    FLOTA("Flota de transporte", 1),

    /** Compañía de seguros. */
    ASEGURADORA("Compañía de seguros", 2);

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Puntos adicionales que aporta este tipo de cliente al puntaje de prioridad.
     */
    private final int puntos;

    /**
     * Crea una constante con su texto legible y sus puntos.
     *
     * @param descripcion texto para mostrar
     * @param puntos puntos adicionales al puntaje de prioridad
     */
    TipoCliente(String descripcion, int puntos) {
        this.descripcion = descripcion;
        this.puntos = puntos;
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
     * Obtiene los puntos adicionales que aporta este tipo de cliente.
     *
     * @return puntos del tipo de cliente
     */
    public int getPuntos() {
        return puntos;
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