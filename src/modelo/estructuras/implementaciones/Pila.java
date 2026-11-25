package modelo.estructuras.implementaciones;

import modelo.estructuras.contratos.IPila;
import modelo.estructuras.nodos.Nodo;

/**
 * Pila LIFO con nodos enlazados, sin usar Collections. Apilar y desapilar son O(1).
 *
 * @param <T> tipo de los elementos de la pila
 */
public class Pila<T> implements IPila<T> {

    /**
     * Nodo de la cima, el próximo en salir.
     */
    private Nodo<T> cima;

    /**
     * Cantidad de elementos de la pila.
     */
    private int tamano;

    /**
     * {@inheritDoc}
     */
    @Override
    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T desapilar() {
        if (cima == null) {
            throw new IllegalStateException("La pila está vacía");
        }
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        tamano--;
        return dato;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T verCima() {
        return cima == null ? null : cima.getDato();
    }

    /**
     * Obtiene un elemento contando desde la cima, donde la posición 0 es la cima.
     *
     * @param indice posición desde la cima
     * @return elemento de esa posición
     * @throws IndexOutOfBoundsException si el índice no es válido
     */
    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        Nodo<T> actual = cima;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int tamano() {
        return tamano;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean estaVacia() {
        return tamano == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vaciar() {
        cima = null;
        tamano = 0;
    }
}