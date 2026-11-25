package modelo.enumeraciones;

/**
 * Estado del ciclo de vida de una solicitud de asistencia.
 */
public enum EstadoSolicitud {

    /** La solicitud espera en una cola a que se le asignen recursos. */
    PENDIENTE("Pendiente"),

    /** La solicitud tiene recursos asignados y se está atendiendo. */
    ATENDIDA("Atendida"),

    /** El servicio terminó y la solicitud sale del flujo de pendientes. */
    SOLUCIONADA("Solucionada");

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Crea una constante con su texto legible.
     *
     * @param descripcion texto para mostrar
     */
    EstadoSolicitud(String descripcion) {
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