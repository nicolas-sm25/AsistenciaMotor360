package modelo.utilidades;

import modelo.excepciones.ReglaNegocioException;

/**
 * Utilidades de validación reutilizadas por las entidades para exigir datos obligatorios.
 */
public final class Validador {

    /**
     * Evita que la clase utilitaria sea instanciada.
     */
    private Validador() {
    }

    /**
     * Exige que un texto no sea nulo ni esté vacío.
     *
     * @param valor texto a validar
     * @param campo nombre del campo, usado en el mensaje de error
     * @return el texto sin espacios al inicio ni al final
     * @throws ReglaNegocioException si el texto es nulo o está vacío
     */
    public static String textoObligatorio(String valor, String campo) throws ReglaNegocioException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    /**
     * Exige que un objeto no sea nulo.
     *
     * @param <T> tipo del objeto validado
     * @param valor objeto a validar
     * @param campo nombre del campo, usado en el mensaje de error
     * @return el mismo objeto recibido
     * @throws ReglaNegocioException si el objeto es nulo
     */
    public static <T> T objetoObligatorio(T valor, String campo) throws ReglaNegocioException {
        if (valor == null) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }
}