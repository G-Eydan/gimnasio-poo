package gympro.modelo.membresia;

import gympro.excepciones.DatosInvalidosException;

/**
 * Plan de 1 mes, sin descuento.
 *
 * @author I1
 */
public class PlanMensual extends Plan {

    private static final long serialVersionUID = 1L;

    public PlanMensual(String codigo, String nombre, double precioMensual, String descripcion)
            throws DatosInvalidosException {
        super(codigo, nombre, precioMensual, descripcion);
    }

    /** Constructor corto con nombre y descripción por defecto. */
    public PlanMensual(String codigo, double precioMensual) throws DatosInvalidosException {
        this(codigo, "Plan Mensual", precioMensual, "Acceso por 1 mes");
    }

    @Override
    public int getDuracionMeses() { return 1; }

    @Override
    public double getDescuentoPlan() { return 0.0; }

    @Override
    public String getTipo() { return "MENSUAL"; }
}
