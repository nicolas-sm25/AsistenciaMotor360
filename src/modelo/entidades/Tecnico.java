package modelo.entidades;

import modelo.enumeraciones.EstadoTecnico;
import modelo.enumeraciones.TipoServicio;
import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Técnico de campo. Protege las reglas 3 y 5 en sus propios métodos de cambio de estado.
 */
public class Tecnico {

    /**
     * Identificación del técnico, que no cambia.
     */
    private final String identificacion;

    /**
     * Nombre completo del técnico.
     */
    private String nombre;

    /**
     * Área de especialidad asignada.
     */
    private TipoServicio especialidad;

    /**
     * Sector donde patrulla el técnico.
     */
    private String sectorPatrullaje;

    /**
     * Estado vigente: libre o en servicio.
     */
    private EstadoTecnico estado;

    /**
     * Crea un técnico libre, validando sus datos obligatorios (regla 3).
     *
     * @param identificacion identificación del técnico
     * @param nombre nombre completo
     * @param especialidad área de especialidad
     * @param sectorPatrullaje sector de patrullaje
     * @throws ReglaNegocioException si algún dato es nulo o está vacío
     */
    public Tecnico(String identificacion, String nombre, TipoServicio especialidad, String sectorPatrullaje)
            throws ReglaNegocioException {
        this.identificacion = Validador.textoObligatorio(identificacion, "identificación");
        this.nombre = Validador.textoObligatorio(nombre, "nombre");
        this.especialidad = Validador.objetoObligatorio(especialidad, "especialidad");
        this.sectorPatrullaje = Validador.textoObligatorio(sectorPatrullaje, "sector de patrullaje");
        this.estado = EstadoTecnico.LIBRE;
    }

    /**
     * Marca al técnico como en servicio.
     *
     * @throws ReglaNegocioException si ya tiene un servicio activo (regla 5)
     */
    public void ocupar() throws ReglaNegocioException {
        if (estado == EstadoTecnico.EN_SERVICIO) {
            throw new ReglaNegocioException("El técnico " + nombre + " ya tiene un servicio activo");
        }
        estado = EstadoTecnico.EN_SERVICIO;
    }

    /**
     * Marca al técnico como libre.
     *
     * @throws ReglaNegocioException si el técnico no tenía un servicio activo
     */
    public void liberar() throws ReglaNegocioException {
        if (estado == EstadoTecnico.LIBRE) {
            throw new ReglaNegocioException("El técnico " + nombre + " no tiene un servicio activo");
        }
        estado = EstadoTecnico.LIBRE;
    }

    /**
     * Indica si el técnico puede recibir un servicio.
     *
     * @return true si está libre
     */
    public boolean estaLibre() {
        return estado == EstadoTecnico.LIBRE;
    }

    /**
     * Obtiene la identificación del técnico.
     *
     * @return identificación
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * Obtiene el nombre del técnico.
     *
     * @return nombre completo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Actualiza el nombre del técnico.
     *
     * @param nombre nuevo nombre
     * @throws ReglaNegocioException si el nombre es nulo o está vacío
     */
    public void setNombre(String nombre) throws ReglaNegocioException {
        this.nombre = Validador.textoObligatorio(nombre, "nombre");
    }

    /**
     * Obtiene la especialidad del técnico.
     *
     * @return especialidad asignada
     */
    public TipoServicio getEspecialidad() {
        return especialidad;
    }

    /**
     * Actualiza la especialidad del técnico.
     *
     * @param especialidad nueva especialidad
     * @throws ReglaNegocioException si la especialidad es nula
     */
    public void setEspecialidad(TipoServicio especialidad) throws ReglaNegocioException {
        this.especialidad = Validador.objetoObligatorio(especialidad, "especialidad");
    }

    /**
     * Obtiene el sector de patrullaje.
     *
     * @return sector actual
     */
    public String getSectorPatrullaje() {
        return sectorPatrullaje;
    }

    /**
     * Actualiza el sector de patrullaje.
     *
     * @param sectorPatrullaje nuevo sector
     * @throws ReglaNegocioException si el sector es nulo o está vacío
     */
    public void setSectorPatrullaje(String sectorPatrullaje) throws ReglaNegocioException {
        this.sectorPatrullaje = Validador.textoObligatorio(sectorPatrullaje, "sector de patrullaje");
    }

    /**
     * Obtiene el estado vigente del técnico.
     *
     * @return estado actual
     */
    public EstadoTecnico getEstado() {
        return estado;
    }

    /**
     * Retorna una descripción corta del técnico para mostrar en la interfaz.
     *
     * @return nombre y especialidad
     */
    @Override
    public String toString() {
        return nombre + " (" + especialidad.getDescripcion() + ")";
    }
}