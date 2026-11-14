package gympro.modelo.membresia;

import gympro.excepciones.DatosInvalidosException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

/**
 * Plan de suscripción del gimnasio (clase abstracta).
 *
 * <p>No se crea directamente: se usan sus subclases {@link PlanMensual},
 * {@link PlanTrimestral} y {@link PlanAnual}. Cada subclase define su duración y
 * su descuento; el cálculo del importe está aquí y usa esos dos datos
 * (método plantilla + polimorfismo, RN-01).</p>
 *
 * <p>Fórmula: {@code importe = precioMensual × meses × (1 − descuentoPlan)}.</p>
 *
 * @author I1 (Product Owner / lógica de negocio)
 */
public abstract class Plan implements Serializable, Reportable {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private String nombre;
    private double precioMensual;
    private String descripcion;
    private boolean activo;

    /**
     * @param codigo         identificador único del plan (por ejemplo "PL-M")
     * @param nombre         nombre visible; no puede estar vacío
     * @param precioMensual  precio base de un mes; no puede ser negativo
     * @param descripcion    texto opcional
     * @throws DatosInvalidosException si el código o el nombre están vacíos o el precio es negativo
     */
    public Plan(String codigo, String nombre, double precioMensual, String descripcion)
            throws DatosInvalidosException {
        if (codigo == null || codigo.isBlank()) {
            throw new DatosInvalidosException("El código del plan no puede estar vacío.");
        }
        validarNombre(nombre);
        validarPrecio(precioMensual);
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.precioMensual = precioMensual;
        this.descripcion = (descripcion == null) ? "" : descripcion.trim();
        this.activo = true;
    }

    // ------------------------------------------------------------------
    // Lo que cada subclase debe definir
    // ------------------------------------------------------------------

    /** Cantidad de meses que dura el plan. */
    public abstract int getDuracionMeses();

    /** Descuento propio del plan como fracción (0.10 = 10 %). */
    public abstract double getDescuentoPlan();

    /** Tipo del plan: "MENSUAL", "TRIMESTRAL" o "ANUAL" (útil para reportes y base de datos). */
    public abstract String getTipo();

    // ------------------------------------------------------------------
    // Reglas de negocio
    // ------------------------------------------------------------------

    /**
     * Importe del plan con su descuento, redondeado a 2 decimales.
     * Con precio base 120: mensual 120.00, trimestral 324.00, anual 1152.00.
     */
    public double calcularImporte() {
        return redondear(precioMensual * getDuracionMeses() * (1 - getDescuentoPlan()));
    }

    /**
     * Último día de una membresía de este plan que empieza en {@code inicio}.
     * Ejemplo: trimestral desde 01/11/2026 termina el 31/01/2027.
     */
    public LocalDate calcularFechaFin(LocalDate inicio) {
        Objects.requireNonNull(inicio, "La fecha de inicio no puede ser nula.");
        return inicio.plusMonths(getDuracionMeses()).minusDays(1);
    }

    /** Redondea a 2 decimales (se usa también en {@link Pago}). */
    static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // ------------------------------------------------------------------
    // Validaciones (HU-04: no precio negativo ni nombre vacío)
    // ------------------------------------------------------------------

    private static void validarNombre(String nombre) throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("El nombre del plan no puede estar vacío.");
        }
    }

    private static void validarPrecio(double precio) throws DatosInvalidosException {
        if (Double.isNaN(precio) || precio < 0) {
            throw new DatosInvalidosException("El precio mensual no puede ser negativo.");
        }
    }

    // ------------------------------------------------------------------
    // Getters y setters
    // ------------------------------------------------------------------

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecioMensual() { return precioMensual; }
    public String getDescripcion() { return descripcion; }
    public boolean isActivo() { return activo; }

    public void setNombre(String nombre) throws DatosInvalidosException {
        validarNombre(nombre);
        this.nombre = nombre.trim();
    }

    public void setPrecioMensual(double precioMensual) throws DatosInvalidosException {
        validarPrecio(precioMensual);
        this.precioMensual = precioMensual;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = (descripcion == null) ? "" : descripcion.trim();
    }

    public void setActivo(boolean activo) { this.activo = activo; }

    // ------------------------------------------------------------------
    // Reportable y toString
    // ------------------------------------------------------------------

    @Override
    public String generarLineaReporte() {
        return String.format(Locale.US, "%s | %s | %d mes(es) | S/ %.2f",
                codigo, nombre, getDuracionMeses(), calcularImporte());
    }

    @Override
    public String toString() {
        int meses = getDuracionMeses();
        return String.format(Locale.US, "%s - %s (%d %s) - S/ %.2f",
                codigo, nombre, meses, meses == 1 ? "mes" : "meses", calcularImporte());
    }
}
