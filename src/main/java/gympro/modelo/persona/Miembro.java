package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.EstadoInscripcion;
import gympro.modelo.entrenamiento.Rutina;
import gympro.modelo.enums.EstadoMembresia;
import gympro.modelo.membresia.Membresia;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Miembro del gimnasio (clase abstracta). Sus subclases definen el tipo y el
 * descuento: {@link MiembroRegular}, {@link MiembroEstudiante} y {@link MiembroCorporativo}.
 *
 * <p>El código ("M0001", "M0002", …) se genera con un contador de clase.
 * El orden natural es por apellidos, luego nombres y luego DNI.</p>
 *
 * @author I3
 */
public abstract class Miembro extends Persona implements Descontable, Comparable<Miembro>
{
    private static final long serialVersionUID = 1L;
    private static final int DIAS_POR_VENCER = 7;
    private static int contador = 0;

    private final String codigo;
    private final ArrayList<Membresia> historialMembresias = new ArrayList<>();
    /** Baja lógica (decisión D6): un miembro dado de baja no se borra, solo queda inactivo. */
    private boolean activo = true;
    /** Entrenador asignado (asociación 0..1); lo cambia Gimnasio.asignarEntrenador (HU-07). */
    private Entrenador entrenador;
    /** Rutinas en orden de creación; solo la última puede estar activa (RN-10). */
    private final ArrayList<Rutina> historialRutinas = new ArrayList<>();
    // Pendiente: peso y altura como atributos

    /** Constructor corto: solo DNI, nombres y apellidos. */
    public Miembro(String dni, String nombres, String apellidos) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos);
        this.codigo = String.format("M%04d", ++contador);
    }

    /** Constructor completo; fecha de nacimiento, teléfono y email pueden ser {@code null}. */
    public Miembro(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos, fechaNacimiento, telefono, email);
        this.codigo = String.format("M%04d", ++contador);
    }

    @Override
    public String getRol()
    {
        return "MIEMBRO";
    }

    /** @return "REGULAR", "ESTUDIANTE" o "CORPORATIVO" */
    public abstract String getTipo();

    /** Agrega una membresía al historial (la llama {@code Gimnasio} al contratar o renovar). */
    public void agregarMembresia(Membresia m)
    {
        this.historialMembresias.add(m);
    }

    /** @return copia del historial de membresías, en orden de registro */
    public ArrayList<Membresia> getHistorialMembresias()
    {
        return new ArrayList<>(historialMembresias);
    }

    /** @return copia del historial ordenada por fecha de inicio (la más antigua primero) */
    public ArrayList<Membresia> getHistorialOrdenado()
    {
        ArrayList<Membresia> copia = new ArrayList<>(historialMembresias);
        copia.sort(Comparator.comparing(Membresia::getFechaInicio));
        return copia;
    }

    /** @return la membresía vigente hoy, o {@code null} si no hay ninguna */
    public Membresia getMembresiaVigente()
    {
        return getMembresiaVigente(LocalDate.now());
    }

    /**
     * @param hoy fecha de referencia (permite probar con fechas distintas a la actual)
     * @return la membresía no cancelada cuyo periodo incluye {@code hoy}, o {@code null}
     */
    public Membresia getMembresiaVigente(LocalDate hoy)
    {
        for(Membresia m : historialMembresias)
        {
            if(m.getEstado() == EstadoMembresia.CANCELADA) continue;
            if(!hoy.isBefore(m.getFechaInicio()) && !hoy.isAfter(m.getFechaFin()))
            {
                return m;
            }
        }
        return null;
    }

    /**
     * @return la membresía vigente hoy; si no hay, la última no cancelada;
     *         {@code null} si no tiene ninguna
     */
    public Membresia getMembresiaActual()
    {
        return getMembresiaActual(LocalDate.now());
    }

    /** Igual que {@link #getMembresiaActual()} pero con fecha de referencia. */
    public Membresia getMembresiaActual(LocalDate hoy)
    {
        Membresia vigente = getMembresiaVigente(hoy);
        if(vigente != null) return vigente;

        Membresia ultima = null;
        for(Membresia m : historialMembresias)
        {
            if(m.getEstado() != EstadoMembresia.CANCELADA) ultima = m;
        }
        return ultima;
    }

    /** @return true si aún no tiene ninguna membresía registrada */
    public boolean esPrimeraMembresia()
    {
        return historialMembresias.isEmpty();
    }

    /**
     * IMC = peso (kg) / altura² (m). Peso y altura pasan a ser atributos en el Sprint 2.
     */
    public double calcularIMC(double pesoKg, double alturaM)
    {
        if(pesoKg <= 0 || alturaM <= 0)
            throw new IllegalArgumentException("Peso y altura deben ser positivos");
        return pesoKg / (alturaM * alturaM);
    }

    /** @return fecha de fin de la membresía actual, o {@code null} si no tiene ninguna */
    public LocalDate getFechaVencimiento()
    {
        Membresia m = getMembresiaActual();
        return (m == null) ? null : m.getFechaFin();
    }

    /** @return días que faltan para el fin de la membresía vigente (0 si no hay vigente) */
    public long getDiasRestantes()
    {
        return getDiasRestantes(LocalDate.now());
    }

    /** Igual que {@link #getDiasRestantes()} pero con fecha de referencia. */
    public long getDiasRestantes(LocalDate hoy)
    {
        Membresia vigente = getMembresiaVigente(hoy);
        if(vigente == null) return 0;
        return ChronoUnit.DAYS.between(hoy, vigente.getFechaFin());
    }

    /** @return estado de inscripción; POR_VENCER si quedan 7 días o menos */
    public EstadoInscripcion getEstadoInscripcion()
    {
        return getEstadoInscripcion(LocalDate.now());
    }

    /** Igual que {@link #getEstadoInscripcion()} pero con fecha de referencia. */
    public EstadoInscripcion getEstadoInscripcion(LocalDate hoy)
    {
        if(getMembresiaVigente(hoy) != null)
        {
            return (getDiasRestantes(hoy) <= DIAS_POR_VENCER)
                    ? EstadoInscripcion.POR_VENCER
                    : EstadoInscripcion.ACTIVO;
        }

        // Sin vigente: es VENCIDO si alguna membresía no cancelada ya terminó
        for(Membresia m : historialMembresias)
        {
            if(m.getEstado() != EstadoMembresia.CANCELADA && m.getFechaFin().isBefore(hoy))
            {
                return EstadoInscripcion.VENCIDO;
            }
        }
        return EstadoInscripcion.SIN_MEMBRESIA;
    }

    public String getCodigo()
    {
        return codigo;
    }

    /** @return false si el miembro fue dado de baja (baja lógica, HG-01) */
    public boolean isActivo()
    {
        return activo;
    }

    public void setActivo(boolean activo)
    {
        this.activo = activo;
    }

    /** @return el entrenador asignado, o {@code null} si no tiene */
    public Entrenador getEntrenador()
    {
        return entrenador;
    }

    /** Lo usa {@code Gimnasio.asignarEntrenador}, que mantiene la lista del entrenador sincronizada. */
    public void setEntrenador(Entrenador entrenador)
    {
        this.entrenador = entrenador;
    }

    /**
     * Asigna una rutina nueva: la actual (si hay) se archiva y queda en el historial (RN-10).
     *
     * @throws IllegalArgumentException si la rutina es nula
     */
    public void asignarRutina(Rutina r)
    {
        if(r == null) throw new IllegalArgumentException("La rutina no puede ser nula");
        Rutina actual = getRutinaActual();
        if(actual != null) actual.archivar();
        historialRutinas.add(r);
    }

    /** @return la última rutina activa, o {@code null} si no tiene */
    public Rutina getRutinaActual()
    {
        for(int i = historialRutinas.size() - 1; i >= 0; i--)
        {
            if(historialRutinas.get(i).isActiva()) return historialRutinas.get(i);
        }
        return null;
    }

    /** @return copia de todas las rutinas (activa y archivadas), en orden de creación */
    public ArrayList<Rutina> getHistorialRutinas()
    {
        return new ArrayList<>(historialRutinas);
    }

    /** Orden natural: apellidos, luego nombres, y DNI como desempate. */
    @Override
    public int compareTo(Miembro otro)
    {
        int r = this.apellidos.compareToIgnoreCase(otro.apellidos);
        if(r != 0) return r;
        r = this.nombres.compareToIgnoreCase(otro.nombres);
        if(r != 0) return r;
        return this.dni.compareTo(otro.dni);
    }

    @Override
    public String toString()
    {
        return codigo + " | " + getNombreCompleto() + " | DNI " + dni + " | " + getTipo();
    }
}