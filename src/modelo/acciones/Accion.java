package modelo.acciones;

import modelo.excepciones.ReglaNegocioException;

/**
 * Representa una operación reversible del sistema que puede ejecutarse y deshacerse
 * en orden inverso.
 */
public interface Accion {

    /**
     * Ejecuta la acción sobre el estado del sistema.
     *
     * @throws ReglaNegocioException si la operación viola una regla de negocio
     */
    void ejecutar() throws ReglaNegocioException;

    /**
     * Revierte los efectos de la acción ejecutada previamente.
     *
     * @throws ReglaNegocioException si no es posible revertir la acción
     */
    void deshacer() throws ReglaNegocioException;

    /**
     * Obtiene una descripción legible de la acción para el historial o el deshacer.
     *
     * @return descripción de la acción
     */
    String getDescripcion();
}