package gympro.modelo.membresia;

import gympro.excepciones.DatosInvalidosException;

/**
 * Plan de 12 meses, con 20 % de descuento.
 * Incluye una evaluación nutricional gratuita (RN-01).
 *
 * @author I1
 */
public class PlanAnual extends Plan {

    private static final long serialVersionUID = 1L;

    private boolean incluyeEvaluacionNutricional;

    public PlanAnual(String codigo, String nombre, double precioMensual, String descripcion)
            throws DatosInvalidosException {
        super(codigo, nombre, precioMensual, descripcion);
        this.incluyeEvaluacionNutricional = true;
    }

    /** Constructor corto con nombre y descripción por defecto. */
    public PlanAnual(String codigo, double precioMensual) throws DatosInvalidosException {
        this(codigo, "Plan Anual", precioMensual,
                "Acceso por 12 meses con 20 % de descuento y evaluación nutricional");
    }

    @Override
    public int getDuracionMeses() { return 12; }

    @Override
    public double getDescuentoPlan() { return 0.20; }

    @Override
    public String getTipo() { return "ANUAL"; }

    public boolean incluyeEvaluacionNutricional() { return incluyeEvaluacionNutricional; }

    public void setIncluyeEvaluacionNutricional(boolean valor) {
        this.incluyeEvaluacionNutricional = valor;
    }

    @Override
    public String toString() {
        return super.toString() + (incluyeEvaluacionNutricional ? " [incluye evaluación nutricional]" : "");
    }
}
