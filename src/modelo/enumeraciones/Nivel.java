package modelo.enumeraciones;

/**
 * Nivel de intensidad usado para valorar el riesgo, la congestión vial y la gravedad
 * de un incidente al calcular la prioridad de una solicitud.
 */
public enum Nivel {

    /** Nivel bajo. */
    BAJO("Bajo", 1),

    /** Nivel medio. */
    MEDIO("Medio", 2),

    /** Nivel alto. */
    ALTO("Alto", 3);

    /**
     * Texto legible para la interfaz y los reportes.
     */
    private final String descripcion;

    /**
     * Puntos que aporta este nivel al puntaje de prioridad.
     */
    private final int puntos;

    /**
     * Crea una constante con su texto legible y sus puntos.
     *
     * @param descripcion texto para mostrar
     * @param puntos puntos que aporta al puntaje de prioridad
     */
    Nivel(String descripcion, int puntos) {
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
     * Obtiene los puntos que aporta este nivel al puntaje de prioridad.
     *
     * @return puntos del nivel
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