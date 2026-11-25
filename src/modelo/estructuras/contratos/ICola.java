package modelo.estructuras.contratos;

/**
 * Contrato de una cola: los elementos se insertan por el final y se extraen por el frente.
 *
 * @param <T> tipo de los elementos de la cola
 */
public interface ICola<T> extends IConsultable<T> {

    /**
     * Inserta un elemento en la cola.
     *
     * @param dato elemento a encolar
     */
    void encolar(T dato);

    /**
     * Extrae y retorna el elemento del frente.
     *
     * @return elemento del frente
     * @throws IllegalStateException si la cola está vacía
     */
    T desencolar();

    /**
     * Consulta el elemento del frente sin extraerlo.
     *
     * @return elemento del frente o null si la cola está vacía
     */
    T verFrente();
}