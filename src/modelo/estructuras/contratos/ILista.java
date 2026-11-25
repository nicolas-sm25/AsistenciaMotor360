package modelo.estructuras.contratos;

/**
 * Contrato CRUD de una lista de elementos.
 * Create: agregar e insertar. Read: obtener y buscar.
 * Update: actualizar. Delete: eliminar y eliminarDato.
 *
 * @param <T> tipo de los elementos de la lista
 */
public interface ILista<T> extends IConsultable<T> {

    /**
     * Create: agrega un elemento al final de la lista.
     *
     * @param dato elemento a agregar
     */
    void agregar(T dato);

    /**
     * Create: inserta un elemento en una posición.
     *
     * @param indice posición de inserción, entre 0 y tamano()
     * @param dato elemento a insertar
     * @throws IndexOutOfBoundsException si el índice no es válido
     */
    void insertar(int indice, T dato);

    /**
     * Read: busca el primer elemento que cumpla una condición.
     *
     * @param condicion criterio de búsqueda
     * @return primer elemento que la cumple o null si ninguno
     */
    T buscar(Condicion<T> condicion);

    /**
     * Update: reemplaza el elemento de una posición.
     *
     * @param indice posición a modificar
     * @param dato nuevo elemento
     * @return elemento que había antes en esa posición
     * @throws IndexOutOfBoundsException si el índice no es válido
     */
    T actualizar(int indice, T dato);

    /**
     * Delete: elimina el elemento de una posición.
     *
     * @param indice posición a eliminar
     * @return elemento eliminado
     * @throws IndexOutOfBoundsException si el índice no es válido
     */
    T eliminar(int indice);

    /**
     * Delete: elimina la primera aparición de un elemento, comparando con equals.
     *
     * @param dato elemento a eliminar
     * @return true si se eliminó, false si no estaba en la lista
     */
    boolean eliminarDato(T dato);
}