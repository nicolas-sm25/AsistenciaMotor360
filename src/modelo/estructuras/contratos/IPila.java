package modelo.estructuras.contratos;

/**
 * Contrato de una pila: el último elemento apilado es el primero en salir.
 *
 * @param <T> tipo de los elementos de la pila
 */
public interface IPila<T> extends IConsultable<T> {

    /**
     * Apila un elemento en la cima.
     *
     * @param dato elemento a apilar
     */
    void apilar(T dato);

    /**
     * Extrae y retorna el elemento de la cima.
     *
     * @return elemento de la cima
     * @throws IllegalStateException si la pila está vacía
     */
    T desapilar();

    /**
     * Consulta el elemento de la cima sin extraerlo.
     *
     * @return elemento de la cima o null si la pila está vacía
     */
    T verCima();
}