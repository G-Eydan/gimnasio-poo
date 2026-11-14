package gympro.modelo.membresia;

import gympro.excepciones.DatosInvalidosException;

/**
 * Plan de 3 meses, con 10 % de descuento.
 *
 * @author I1
 */
public class PlanTrimestral extends Plan {

    private static final long serialVersionUID = 1L;

    public PlanTrimestral(String codigo, String nombre, double precioMensual, String descripcion)
            throws DatosInvalidosException {
        super(codigo, nombre, precioMensual, descripcion);
    }

    /** Constructor corto con nombre y descripción por defecto. */
    public PlanTrimestral(String codigo, double precioMensual) throws DatosInvalidosException {
        this(codigo, "Plan Trimestral", precioMensual, "Acceso por 3 meses con 10 % de descuento");
    }

    @Override
    public int getDuracionMeses() { return 3; }

    @Override
    public double getDescuentoPlan() { return 0.10; }

    @Override
    public String getTipo() { return "TRIMESTRAL"; }
}
