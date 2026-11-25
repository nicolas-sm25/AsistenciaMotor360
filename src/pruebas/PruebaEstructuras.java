package pruebas;

import modelo.estructuras.implementaciones.Cola;
import modelo.estructuras.implementaciones.ColaPrioridad;
import modelo.estructuras.implementaciones.ListaEnlazada;
import modelo.estructuras.implementaciones.Pila;

/**
 * Prueba rápida por consola de las estructuras de datos propias.
 */
public class PruebaEstructuras {

    /**
     * Ejecuta una operación de cada estructura e imprime el resultado.
     *
     * @param args argumentos de consola, no se usan
     */
    public static void main(String[] args) {
        Cola<String> cola = new Cola<>();
        cola.encolar("A");
        cola.encolar("B");
        cola.encolar("C");
        System.out.println("Cola FIFO: " + cola.desencolar() + cola.desencolar() + cola.desencolar());

        Pila<String> pila = new Pila<>();
        pila.apilar("A");
        pila.apilar("B");
        pila.apilar("C");
        System.out.println("Pila LIFO: " + pila.desapilar() + pila.desapilar() + pila.desapilar());

        ColaPrioridad<Integer> prioridad = new ColaPrioridad<>((a, b) -> Integer.compare(a, b));
        prioridad.encolar(2);
        prioridad.encolar(9);
        prioridad.encolar(5);
        System.out.println("Prioridad: " + prioridad.desencolar() + prioridad.desencolar() + prioridad.desencolar());

        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.agregar("uno");
        lista.agregar("dos");
        lista.insertar(1, "medio");
        lista.actualizar(0, "UNO");
        lista.eliminarDato("dos");
        System.out.println("Lista: " + lista.obtener(0) + ", " + lista.obtener(1) + ", tamaño " + lista.tamano());
    }
}