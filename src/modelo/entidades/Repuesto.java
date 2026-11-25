package modelo.entidades;

import modelo.excepciones.ReglaNegocioException;
import modelo.utilidades.Validador;

/**
 * Repuesto de alta rotación que se prepara anticipadamente en los picos de trabajo.
 */
public class Repuesto {

    /**
     * Código que identifica al repuesto.
     */
    private final String codigo;

    /**
     * Nombre del repuesto.
     */
    private final String nombre;

    /**
     * Crea un repuesto validando sus datos.
     *
     * @param codigo código del repuesto
     * @param nombre nombre del repuesto
     * @throws ReglaNegocioException si algún dato es nulo o está vacío
     */
    public Repuesto(String codigo, String nombre) throws ReglaNegocioException {
        this.codigo = Validador.textoObligatorio(codigo, "código del repuesto");
        this.nombre = Validador.textoObligatorio(nombre, "nombre del repuesto");
    }

    /**
     * Obtiene el código del repuesto.
     *
     * @return código del repuesto
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Obtiene el nombre del repuesto.
     *
     * @return nombre del repuesto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna una descripción corta del repuesto para mostrar en la interfaz.
     *
     * @return nombre y código
     */
    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}