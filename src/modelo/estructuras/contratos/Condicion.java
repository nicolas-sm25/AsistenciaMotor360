package modelo.estructuras.contratos;

/**
 * Condición que un elemento puede o no cumplir, usada para buscar sin depender
 * de las clases concretas.
 *
 * @param <T> tipo del elemento evaluado
 */
@FunctionalInterface
public interface Condicion<T> {

    /**
     * Evalúa la condición sobre un elemento.
     *
     * @param dato elemento a evaluar
     * @return true si el elemento cumple la condición
     */
    boolean cumple(T dato);
}