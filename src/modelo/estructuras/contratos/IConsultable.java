package modelo.estructuras.contratos;

/**
 * Contrato para estructuras cuyos elementos se pueden leer por posición,
 * lo que permite a la vista mostrarlos en tablas y listas.
 *
 * @param <T> tipo de los elementos almacenados
 */
public interface IConsultable<T> extends IContenedor<T> {

    /**
     * Obtiene el elemento de una posición sin extraerlo.
     *
     * @param indice posición del elemento, empezando en 0
     * @return elemento de esa posición
     * @throws IndexOutOfBoundsException si el índice no es válido
     */
    T obtener(int indice);
}