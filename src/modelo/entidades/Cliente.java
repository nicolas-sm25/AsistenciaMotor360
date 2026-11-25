package modelo.entidades;

import modelo.enumeraciones.TipoCliente;
import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Cliente que solicita asistencia vial. Toda solicitud debe estar vinculada a uno.
 */
public class Cliente {

    /**
     * Documento de identidad, que identifica al cliente y no cambia.
     */
    private final String documento;

    /**
     * Nombre completo del cliente.
     */
    private String nombre;

    /**
     * Teléfono de contacto.
     */
    private String telefono;

    /**
     * Perfil del cliente, que influye en la prioridad de sus solicitudes.
     */
    private TipoCliente tipo;

    /**
     * Crea un cliente validando que todos sus datos estén presentes.
     *
     * @param documento documento de identidad
     * @param nombre nombre completo
     * @param telefono teléfono de contacto
     * @param tipo perfil del cliente
     * @throws ReglaNegocioException si algún dato es nulo o está vacío
     */
    public Cliente(String documento, String nombre, String telefono, TipoCliente tipo)
            throws ReglaNegocioException {
        this.documento = Validador.textoObligatorio(documento, "documento");
        this.nombre = Validador.textoObligatorio(nombre, "nombre");
        this.telefono = Validador.textoObligatorio(telefono, "teléfono");
        this.tipo = Validador.objetoObligatorio(tipo, "tipo de cliente");
    }

    /**
     * Obtiene el documento de identidad.
     *
     * @return documento del cliente
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return nombre completo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Actualiza el nombre del cliente.
     *
     * @param nombre nuevo nombre
     * @throws ReglaNegocioException si el nombre es nulo o está vacío
     */
    public void setNombre(String nombre) throws ReglaNegocioException {
        this.nombre = Validador.textoObligatorio(nombre, "nombre");
    }

    /**
     * Obtiene el teléfono del cliente.
     *
     * @return teléfono de contacto
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Actualiza el teléfono del cliente.
     *
     * @param telefono nuevo teléfono
     * @throws ReglaNegocioException si el teléfono es nulo o está vacío
     */
    public void setTelefono(String telefono) throws ReglaNegocioException {
        this.telefono = Validador.textoObligatorio(telefono, "teléfono");
    }

    /**
     * Obtiene el perfil del cliente.
     *
     * @return tipo de cliente
     */
    public TipoCliente getTipo() {
        return tipo;
    }

    /**
     * Actualiza el perfil del cliente.
     *
     * @param tipo nuevo tipo de cliente
     * @throws ReglaNegocioException si el tipo es nulo
     */
    public void setTipo(TipoCliente tipo) throws ReglaNegocioException {
        this.tipo = Validador.objetoObligatorio(tipo, "tipo de cliente");
    }

    /**
     * Retorna una descripción corta del cliente para mostrar en la interfaz.
     *
     * @return nombre y documento
     */
    @Override
    public String toString() {
        return nombre + " (" + documento + ")";
    }
}