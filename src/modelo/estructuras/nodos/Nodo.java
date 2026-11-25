package modelo.estructuras.nodos;

/**
 * Nodo enlazado simple que almacena un dato genérico y la referencia al siguiente nodo.
 *
 * @param <T> tipo del dato almacenado
 */
public class Nodo<T> {

    /**
     * Dato almacenado en el nodo.
     */
    private T dato;

    /**
     * Referencia al nodo siguiente de la estructura.
     */
    private Nodo<T> siguiente;

    /**
     * Crea un nodo con el dato indicado.
     *
     * @param dato dato a almacenar
     */
    public Nodo(T dato) {
        this.dato = dato;
    }

    /**
     * Obtiene el dato almacenado.
     *
     * @return dato del nodo
     */
    public T getDato() {
        return dato;
    }

    /**
     * Reemplaza el dato almacenado.
     *
     * @param dato nuevo dato
     */
    public void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Obtiene el nodo siguiente.
     *
     * @return nodo siguiente o null si es el último
     */
    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Define el nodo siguiente.
     *
     * @param siguiente nuevo nodo siguiente
     */
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}