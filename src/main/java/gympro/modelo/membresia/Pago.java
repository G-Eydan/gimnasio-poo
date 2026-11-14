package gympro.modelo.membresia;

import gympro.modelo.enums.MetodoPago;
import gympro.modelo.persona.Miembro;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Pago de una membresía, con el desglose completo (RN-02 a RN-05).
 *
 * <pre>
 * subtotal = importePlan − importePlan × descuentoMiembro (+ matrícula si es la primera vez)
 * total    = subtotal + subtotal × recargoMétodoPago
 * </pre>
 *
 * <p>Ejemplo: estudiante, trimestral, primera vez, tarjeta →
 * 324.00 − 48.60 + 50.00 = 325.40 → + 3 % = <b>S/ 335.16</b>.</p>
 *
 * <p>Todos los montos se redondean a 2 decimales.</p>
 *
 * @author I1
 */
public class Pago implements Serializable, Reportable {

    private static final long serialVersionUID = 1L;

    /** Matrícula que se cobra solo en la primera membresía (RN-03). */
    public static final double MATRICULA = 50.0;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String codigo;
    private final Membresia membresia;
    private final LocalDate fecha;
    private final MetodoPago metodo;
    private final double montoPlan;
    private final double porcentajeDescuento;
    private final double descuento;
    private final double matricula;
    private final double recargo;
    private final double total;

    /**
     * @param codigo      código único del pago (por ejemplo "P0001")
     * @param membresia   membresía que se paga (de ella salen el miembro y el plan)
     * @param metodo      método de pago
     * @param primeraVez  true si es la primera membresía del miembro (cobra matrícula)
     * @param fecha       fecha del pago
     */
    public Pago(String codigo, Membresia membresia, MetodoPago metodo, boolean primeraVez, LocalDate fecha) {
        this.codigo = Objects.requireNonNull(codigo, "El código no puede ser nulo.");
        this.membresia = Objects.requireNonNull(membresia, "La membresía no puede ser nula.");
        this.metodo = Objects.requireNonNull(metodo, "El método de pago no puede ser nulo.");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");

        this.montoPlan = membresia.getPlan().calcularImporte();
        this.porcentajeDescuento = membresia.getMiembro().getPorcentajeDescuento();
        this.descuento = calcularDescuento(montoPlan, porcentajeDescuento);
        this.matricula = primeraVez ? MATRICULA : 0.0;
        this.recargo = calcularRecargo(montoPlan - descuento + matricula, metodo);
        this.total = sumar(montoPlan, descuento, matricula, recargo);
    }

    /** Igual que el anterior, con la fecha de hoy. */
    public Pago(String codigo, Membresia membresia, MetodoPago metodo, boolean primeraVez) {
        this(codigo, membresia, metodo, primeraVez, LocalDate.now());
    }

    // ------------------------------------------------------------------
    // Cálculo
    // ------------------------------------------------------------------

    /** Total del pago: plan − descuento + matrícula + recargo. */
    public double calcularTotal() {
        return sumar(montoPlan, descuento, matricula, recargo);
    }

    /**
     * Vista previa del total sin registrar nada (la GUI la usa para recalcular
     * en vivo al cambiar de plan o de método de pago).
     */
    public static double previsualizarTotal(Miembro miembro, Plan plan, MetodoPago metodo, boolean primeraVez) {
        double montoPlan = plan.calcularImporte();
        double descuento = calcularDescuento(montoPlan, miembro.getPorcentajeDescuento());
        double matricula = primeraVez ? MATRICULA : 0.0;
        double recargo = calcularRecargo(montoPlan - descuento + matricula, metodo);
        return sumar(montoPlan, descuento, matricula, recargo);
    }

    private static double calcularDescuento(double montoPlan, double porcentaje) {
        return Plan.redondear(montoPlan * porcentaje);
    }

    private static double calcularRecargo(double subtotal, MetodoPago metodo) {
        return Plan.redondear(subtotal * metodo.getRecargo());
    }

    private static double sumar(double montoPlan, double descuento, double matricula, double recargo) {
        return Plan.redondear(montoPlan - descuento + matricula + recargo);
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public String getCodigo() { return codigo; }
    public Membresia getMembresia() { return membresia; }
    public LocalDate getFecha() { return fecha; }
    public MetodoPago getMetodo() { return metodo; }
    public double getMontoPlan() { return montoPlan; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public double getDescuento() { return descuento; }
    public double getMatricula() { return matricula; }
    public double getRecargo() { return recargo; }
    public double getTotal() { return total; }

    // ------------------------------------------------------------------
    // Comprobante y reporte
    // ------------------------------------------------------------------

    /** Texto del comprobante de pago (se guarda en un .txt, HU-15). */
    public String generarComprobante() {
        String linea = "===========================================";
        Miembro miembro = membresia.getMiembro();
        Plan plan = membresia.getPlan();
        StringBuilder sb = new StringBuilder();
        sb.append(linea).append('\n');
        sb.append("          GYMPRO - COMPROBANTE DE PAGO").append('\n');
        sb.append(linea).append('\n');
        sb.append(String.format(Locale.US, "Código     : %-15sFecha: %s",
                codigo, FORMATO.format(fecha))).append('\n');
        sb.append(String.format(Locale.US, "Miembro    : %s (%s)",
                miembro.getNombreCompleto(), miembro.getTipo())).append('\n');
        sb.append(String.format(Locale.US, "Plan       : %s  (%s - %s)",
                plan.getNombre(), FORMATO.format(membresia.getFechaInicio()),
                FORMATO.format(membresia.getFechaFin()))).append('\n');
        sb.append(String.format(Locale.US, "Importe    : S/ %7.2f", montoPlan)).append('\n');
        sb.append(String.format(Locale.US, "Descuento  : S/ %7.2f  (%.0f %%)",
                -descuento, porcentajeDescuento * 100)).append('\n');
        sb.append(String.format(Locale.US, "Matrícula  : S/ %7.2f", matricula)).append('\n');
        sb.append(String.format(Locale.US, "Recargo    : S/ %7.2f  (%s %.0f %%)",
                recargo, metodo.name(), metodo.getRecargo() * 100)).append('\n');
        sb.append(String.format(Locale.US, "TOTAL      : S/ %7.2f", total)).append('\n');
        sb.append(linea).append('\n');
        return sb.toString();
    }

    @Override
    public String generarLineaReporte() {
        return String.format(Locale.US, "%s | %s | %s | S/ %.2f | %s",
                codigo, FORMATO.format(fecha), membresia.getMiembro().getNombreCompleto(),
                total, metodo.name());
    }

    @Override
    public String toString() {
        return generarLineaReporte();
    }
}
