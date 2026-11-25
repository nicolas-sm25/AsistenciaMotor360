package modelo.estructuras.contratos;

/**
 * Criterio que define qué elemento es más prioritario que otro.
 *
 * @param <T> tipo de los elementos comparados
 */
@FunctionalInterface
public interface CriterioPrioridad<T> {

    /**
     * Compara la prioridad de dos elementos.
     *
     * @param primero primer elemento
     * @param segundo segundo elemento
     * @return positivo si el primero es más prioritario, negativo si lo es el segundo,
     *         cero si tienen la misma prioridad
     */
    int comparar(T primero, T segundo);
}