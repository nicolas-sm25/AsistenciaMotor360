package modelo.estructuras.implementaciones;

import modelo.estructuras.contratos.CriterioPrioridad;
import modelo.estructuras.contratos.ICola;
import modelo.estructuras.nodos.Nodo;

/**
 * Cola de prioridad con lista enlazada ordenada, sin usar Collections.
 * Cada elemento se inserta en su posición según el criterio (O(n)), el más prioritario
 * queda al frente y se extrae en O(1). A igual prioridad se respeta el orden de llegada.
 *
 * @param <T> tipo de los elementos de la cola
 */
public class ColaPrioridad<T> implements ICola<T> {

    /**
     * Regla que decide qué elemento es más prioritario.
     */
    private final CriterioPrioridad<T> criterio;

    /**
     * Nodo de mayor prioridad, el próximo en salir.
     */
    private Nodo<T> frente;

    /**
     * Cantidad de elementos de la cola.
     */
    private int tamano;

    /**
     * Crea una cola de prioridad con el criterio indicado.
     *
     * @param criterio regla que decide qué elemento es más prioritario
     * @throws IllegalArgumentException si el criterio es nulo
     */
    public ColaPrioridad(CriterioPrioridad<T> criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException("El criterio de prioridad es obligatorio");
        }
        this.criterio = criterio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (frente == null || criterio.comparar(dato, frente.getDato()) > 0) {
            nuevo.setSiguiente(frente);
            frente = nuevo;
        } else {
            Nodo<T> actual = frente;
            while (actual.getSiguiente() != null
                    && criterio.comparar(dato, actual.getSiguiente().getDato()) <= 0) {
                actual = actual.getSiguiente();
            }
            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
        }
        tamano++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T desencolar() {
        if (frente == null) {
            throw new IllegalStateException("La cola de prioridad está vacía");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
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
        tamano = 0;
    }
}