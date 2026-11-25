package modelo.entidades;

import modelo.estructuras.contratos.ILista;
import modelo.estructuras.implementaciones.ListaEnlazada;
import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Maletín de respuesta rápida. Al terminar un servicio debe pasar por auditoría para verificar
 * su contenido y reemplazar piezas si es necesario.
 */
public class Maletin {

    /**
     * Código que identifica al maletín.
     */
    private final String codigo;

    /**
     * Piezas que contiene el maletín.
     */
    private final ILista<String> contenido;

    /**
     * Indica si el maletín ya fue auditado y está listo para usarse.
     */
    private boolean auditado;

    /**
     * Crea un maletín vacío y listo para usarse.
     *
     * @param codigo código del maletín
     * @throws ReglaNegocioException si el código es nulo o está vacío
     */
    public Maletin(String codigo) throws ReglaNegocioException {
        this.codigo = Validador.textoObligatorio(codigo, "código del maletín");
        this.contenido = new ListaEnlazada<>();
        this.auditado = true;
    }

    /**
     * Agrega una pieza al contenido.
     *
     * @param pieza nombre de la pieza
     * @throws ReglaNegocioException si el nombre es nulo o está vacío
     */
    public void agregarPieza(String pieza) throws ReglaNegocioException {
        contenido.agregar(Validador.textoObligatorio(pieza, "pieza"));
    }

    /**
     * Reemplaza una pieza durante la auditoría.
     *
     * @param indice posición de la pieza a reemplazar
     * @param piezaNueva nombre de la pieza nueva
     * @return nombre de la pieza reemplazada
     * @throws ReglaNegocioException si el nombre es nulo o está vacío
     */
    public String reemplazarPieza(int indice, String piezaNueva) throws ReglaNegocioException {
        return contenido.actualizar(indice, Validador.textoObligatorio(piezaNueva, "pieza"));
    }

    /**
     * Marca el maletín como pendiente de auditoría al terminar un servicio.
     */
    public void marcarParaAuditoria() {
        auditado = false;
    }

    /**
     * Marca el maletín como auditado y listo para volver a la estantería.
     */
    public void marcarAuditado() {
        auditado = true;
    }

    /**
     * Indica si el maletín ya fue auditado.
     *
     * @return true si está auditado
     */
    public boolean isAuditado() {
        return auditado;
    }

    /**
     * Obtiene el código del maletín.
     *
     * @return código del maletín
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Obtiene el contenido del maletín.
     *
     * @return lista de piezas
     */
    public ILista<String> getContenido() {
        return contenido;
    }

    /**
     * Retorna una descripción corta del maletín para mostrar en la interfaz.
     *
     * @return código del maletín
     */
    @Override
    public String toString() {
        return "Maletín " + codigo;
    }
}