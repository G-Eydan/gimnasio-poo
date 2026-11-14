package gympro.modelo.membresia;

import gympro.modelo.enums.EstadoMembresia;
import gympro.modelo.persona.Miembro;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Membresía: une a un {@link Miembro} con un {@link Plan} durante un periodo.
 *
 * <p>Relaciones: el {@code Plan} es agregación (existe sin la membresía) y el
 * {@link Pago} es composición (pertenece a la membresía).</p>
 *
 * <p>La fecha de fin se calcula con el plan: inicio + meses − 1 día (RN-01).
 * Las fechas no cambian; para renovar se crea otra membresía.</p>
 *
 * @author I1
 */
public class Membresia implements Serializable, Comparable<Membresia> {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String codigo;
    private final Miembro miembro;
    private final Plan plan;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private EstadoMembresia estado;
    private Pago pago;

    /**
     * @param codigo       código único de la membresía (lo genera {@code Gimnasio})
     * @param miembro      dueño de la membresía
     * @param plan         plan contratado
     * @param fechaInicio  primer día de vigencia
     */
    public Membresia(String codigo, Miembro miembro, Plan plan, LocalDate fechaInicio) {
        this.codigo = Objects.requireNonNull(codigo, "El código no puede ser nulo.");
        this.miembro = Objects.requireNonNull(miembro, "El miembro no puede ser nulo.");
        this.plan = Objects.requireNonNull(plan, "El plan no puede ser nulo.");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula.");
        this.fechaFin = plan.calcularFechaFin(fechaInicio);
        this.estado = calcularEstado(LocalDate.now());
    }

    // ------------------------------------------------------------------
    // Consultas por fecha
    // ------------------------------------------------------------------

    /** Vigente = no cancelada y todavía no pasó su fecha de fin (incluye las pendientes). */
    public boolean estaVigente(LocalDate hoy) {
        return estado != EstadoMembresia.CANCELADA && !hoy.isAfter(fechaFin);
    }

    /** Vencida = no cancelada y ya pasó su fecha de fin. */
    public boolean estaVencida(LocalDate hoy) {
        return estado != EstadoMembresia.CANCELADA && hoy.isAfter(fechaFin);
    }

    /** True si ya empezó y vence dentro de los próximos {@code dias} días (contando hoy). */
    public boolean venceEnLosProximos(int dias, LocalDate hoy) {
        return estaVigente(hoy) && !hoy.isBefore(fechaInicio) && diasRestantes(hoy) <= dias;
    }

    /** Días que faltan para la fecha de fin (negativo si ya venció). */
    public long diasRestantes(LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, fechaFin);
    }

    // ------------------------------------------------------------------
    // Estado
    // ------------------------------------------------------------------

    /** Recalcula el estado según la fecha dada (una cancelada sigue cancelada). */
    public void actualizarEstado(LocalDate hoy) {
        this.estado = calcularEstado(hoy);
    }

    /** Anula la membresía (acción del administrador). */
    public void cancelar() {
        this.estado = EstadoMembresia.CANCELADA;
    }

    private EstadoMembresia calcularEstado(LocalDate hoy) {
        if (estado == EstadoMembresia.CANCELADA) return EstadoMembresia.CANCELADA;
        if (hoy.isAfter(fechaFin)) return EstadoMembresia.VENCIDA;
        if (hoy.isBefore(fechaInicio)) return EstadoMembresia.PENDIENTE;
        return EstadoMembresia.ACTIVA;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public String getCodigo() { return codigo; }
    public Miembro getMiembro() { return miembro; }
    public Plan getPlan() { return plan; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public EstadoMembresia getEstado() { return estado; }
    public Pago getPago() { return pago; }

    /** Asocia el pago a la membresía (composición 1 a 1: solo se puede hacer una vez). */
    public void setPago(Pago pago) {
        if (this.pago != null) {
            throw new IllegalStateException("La membresía " + codigo + " ya tiene un pago.");
        }
        this.pago = Objects.requireNonNull(pago, "El pago no puede ser nulo.");
    }

    /** Orden natural: por fecha de fin (sirve para ordenar vencimientos). */
    @Override
    public int compareTo(Membresia otra) {
        int c = fechaFin.compareTo(otra.fechaFin);
        return c != 0 ? c : codigo.compareTo(otra.codigo);
    }

    @Override
    public String toString() {
        return codigo + " | " + miembro.getNombreCompleto() + " | " + plan.getNombre()
                + " | " + FORMATO.format(fechaInicio) + " - " + FORMATO.format(fechaFin)
                + " | " + estado;
    }
}
