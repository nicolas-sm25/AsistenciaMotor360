package modelo.estructuras.contratos;

/**
 * Contrato mínimo de toda estructura que almacena elementos.
 *
 * @param <T> tipo de los elementos almacenados
 */
public interface IContenedor<T> {

    /**
     * Retorna la cantidad de elementos almacenados.
     *
     * @return número de elementos
     */
    int tamano();

    /**
     * Indica si la estructura no tiene elementos.
     *
     * @return true si está vacía
     */
    boolean estaVacia();

    /**
     * Elimina todos los elementos de la estructura.
     */
    void vaciar();
}