package modelo.estructuras.implementaciones;

import modelo.estructuras.contratos.ICola;
import modelo.estructuras.nodos.Nodo;

/**
 * Cola FIFO con nodos enlazados, sin usar Collections. Encolar y desencolar son O(1).
 *
 * @param <T> tipo de los elementos de la cola
 */
public class Cola<T> implements ICola<T> {

    /**
     * Primer nodo de la cola, el próximo en salir.
     */
    private Nodo<T> frente;

    /**
     * Último nodo de la cola, donde se insertan los nuevos elementos.
     */
    private Nodo<T> fin;

    /**
     * Cantidad de elementos de la cola.
     */
    private int tamano;

    /**
     * {@inheritDoc}
     */
    @Override
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamano++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T desencolar() {
        if (frente == null) {
            throw new IllegalStateException("La cola está vacía");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return dato;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T verFrente() {
        return frente == null ? null : frente.getDato();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        Nodo<T> actual = frente;
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
        frente = null;
        fin = null;
        tamano = 0;
    }
}