package modelo.estructuras.implementaciones;

import modelo.estructuras.contratos.Condicion;
import modelo.estructuras.contratos.ILista;
import modelo.estructuras.nodos.Nodo;

/**
 * Lista enlazada simple con operaciones CRUD, implementada sin usar Collections.
 *
 * @param <T> tipo de los elementos de la lista
 */
public class ListaEnlazada<T> implements ILista<T> {

    /**
     * Primer nodo de la lista.
     */
    private Nodo<T> primero;

    /**
     * Último nodo de la lista, lo que permite agregar en O(1).
     */
    private Nodo<T> ultimo;

    /**
     * Cantidad de elementos de la lista.
     */
    private int tamano;

    /**
     * {@inheritDoc}
     */
    @Override
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamano++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertar(int indice, T dato) {
        if (indice < 0 || indice > tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        if (indice == tamano) {
            agregar(dato);
            return;
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (indice == 0) {
            nuevo.setSiguiente(primero);
            primero = nuevo;
        } else {
            Nodo<T> anterior = nodoEn(indice - 1);
            nuevo.setSiguiente(anterior.getSiguiente());
            anterior.setSiguiente(nuevo);
        }
        tamano++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T obtener(int indice) {
        validarIndice(indice);
        return nodoEn(indice).getDato();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T buscar(Condicion<T> condicion) {
        Nodo<T> actual = primero;
        while (actual != null) {
            if (condicion.cumple(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T actualizar(int indice, T dato) {
        validarIndice(indice);
        Nodo<T> nodo = nodoEn(indice);
        T anterior = nodo.getDato();
        nodo.setDato(dato);
        return anterior;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T eliminar(int indice) {
        validarIndice(indice);
        Nodo<T> eliminado;
        if (indice == 0) {
            eliminado = primero;
            primero = primero.getSiguiente();
            if (primero == null) {
                ultimo = null;
            }
        } else {
            Nodo<T> anterior = nodoEn(indice - 1);
            eliminado = anterior.getSiguiente();
            anterior.setSiguiente(eliminado.getSiguiente());
            if (eliminado == ultimo) {
                ultimo = anterior;
            }
        }
        tamano--;
        return eliminado.getDato();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eliminarDato(T dato) {
        int indice = indiceDe(dato);
        if (indice < 0) {
            return false;
        }
        eliminar(indice);
        return true;
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
        primero = null;
        ultimo = null;
        tamano = 0;
    }

    /**
     * Verifica que un índice esté dentro del rango de la lista.
     *
     * @param indice posición a validar
     * @throws IndexOutOfBoundsException si el índice no es válido
     */
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
    }

    /**
     * Recorre la lista hasta el nodo de una posición ya validada.
     *
     * @param indice posición del nodo
     * @return nodo en esa posición
     */
    private Nodo<T> nodoEn(int indice) {
        Nodo<T> actual = primero;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual;
    }

    /**
     * Busca la posición de la primera aparición de un elemento, comparando con equals.
     *
     * @param dato elemento a buscar
     * @return posición del elemento o -1 si no está
     */
    private int indiceDe(T dato) {
        Nodo<T> actual = primero;
        int indice = 0;
        while (actual != null) {
            T valor = actual.getDato();
            if (dato == null ? valor == null : dato.equals(valor)) {
                return indice;
            }
            actual = actual.getSiguiente();
            indice++;
        }
        return -1;
    }
}