package modelo.excepciones;

/**
 * Excepción que representa la violación de una regla de negocio del sistema.
 * El controlador la lanza y la vista muestra su mensaje al usuario.
 */
public class ReglaNegocioException extends Exception {

    /**
     * Identificador de versión para la serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con el mensaje de la regla violada.
     *
     * @param mensaje descripción de la regla que no se cumplió
     */
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}