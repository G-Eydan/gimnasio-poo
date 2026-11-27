package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.DiaSemana;
import gympro.modelo.enums.Especialidad;
import gympro.modelo.enums.Turno;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Entrenador del gimnasio. Tiene un código ("E001", "E002", …), un turno,
 * especialidades, un cupo de hasta {@link #MAX_MIEMBROS} miembros asignados
 * (agregación) y una matriz de disponibilidad {@code [día][turno]} (arreglo 2D de 7 × 3).
 *
 * <p>El orden natural ({@link Comparable}) es por cantidad de miembros asignados
 * (RF-27, ranking de entrenadores).</p>
 *
 * @author I3
 */
public class Entrenador extends Persona implements Comparable<Entrenador>
{
    private static final long serialVersionUID = 1L;
    public static final int MAX_MIEMBROS = 15;
    /** Monto que se suma al sueldo base por cada miembro asignado (RN-08). */
    public static final double BONO_POR_MIEMBRO = 20.0;
    private static int contador = 0;

    private final String codigo;
    private Turno turno;
    private final ArrayList<Especialidad> especialidades = new ArrayList<>();
    private double sueldoBase;
    private final ArrayList<Miembro> miembrosAsignados = new ArrayList<>();
    /** Fila = {@link DiaSemana} (0 = lunes), columna = {@link Turno}. */
    private final boolean[][] disponibilidad = new boolean[DiaSemana.values().length][Turno.values().length];

    /**
     * Constructor sin turno: queda en el turno {@link Turno#MANANA}.
     *
     * @throws DatosInvalidosException si algún dato personal no pasa la validación o el sueldo es negativo
     */
    public Entrenador(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email, double sueldoBase) throws DatosInvalidosException
    {
        this(dni, nombres, apellidos, fechaNacimiento, telefono, email, sueldoBase, Turno.MANANA);
    }

    /**
     * Constructor completo.
     *
     * @throws DatosInvalidosException si algún dato personal no pasa la validación, el sueldo es negativo o falta el turno
     */
    public Entrenador(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email,
                      double sueldoBase, Turno turno) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos, fechaNacimiento, telefono, email);
        if(sueldoBase < 0) throw new DatosInvalidosException("El sueldo base no puede ser negativo.");
        if(turno == null) throw new DatosInvalidosException("Debe elegir un turno.");
        this.sueldoBase = sueldoBase;
        this.turno = turno;
        this.codigo = String.format("E%03d", ++contador);
    }

    @Override
    public String getRol()
    {
        return "ENTRENADOR";
    }

    public String getCodigo()
    {
        return codigo;
    }

    public Turno getTurno()
    {
        return turno;
    }

    public void setTurno(Turno turno)
    {
        if(turno == null) throw new IllegalArgumentException("El turno no puede ser nulo");
        this.turno = turno;
    }

    // --- Especialidades ---

    /**
     * Agrega una especialidad si no la tenía.
     *
     * @return este entrenador, para encadenar llamadas
     */
    public Entrenador agregarEspecialidad(Especialidad e)
    {
        if(e != null && !especialidades.contains(e)) especialidades.add(e);
        return this;
    }

    public boolean tieneEspecialidad(Especialidad e)
    {
        return especialidades.contains(e);
    }

    /** @return copia de las especialidades */
    public ArrayList<Especialidad> getEspecialidades()
    {
        return new ArrayList<>(especialidades);
    }

    // --- Sueldo ---

    public double getSueldoBase()
    {
        return sueldoBase;
    }

    public void setSueldoBase(double sueldoBase)
    {
        if(sueldoBase < 0) throw new IllegalArgumentException("El sueldo no puede ser negativo");
        this.sueldoBase = sueldoBase;
    }

    /** @return sueldo base + S/ 20 por cada miembro asignado (RN-08) */
    public double calcularSueldo()
    {
        return sueldoBase + BONO_POR_MIEMBRO * miembrosAsignados.size();
    }

    // --- Miembros asignados ---

    /** @return true si tiene menos de {@link #MAX_MIEMBROS} miembros asignados */
    public boolean tieneCupo()
    {
        return miembrosAsignados.size() < MAX_MIEMBROS;
    }

    /** Igual que {@link #tieneCupo()} (nombre de la especificación). */
    public boolean puedeAceptarMiembro()
    {
        return tieneCupo();
    }

    /**
     * Asigna un miembro al entrenador.
     *
     * @return false si el miembro es nulo, ya estaba asignado o no hay cupo
     */
    public boolean asignarMiembro(Miembro m)
    {
        if(m == null || !tieneCupo() || miembrosAsignados.contains(m)) return false;
        return miembrosAsignados.add(m);
    }

    /** @return true si el miembro estaba asignado y se quitó */
    public boolean quitarMiembro(Miembro m)
    {
        return miembrosAsignados.remove(m);
    }

    /** Igual que {@link #quitarMiembro(Miembro)} (nombre de la especificación). */
    public boolean retirarMiembro(Miembro m)
    {
        return quitarMiembro(m);
    }

    /** @return copia de los miembros asignados */
    public ArrayList<Miembro> getMiembrosAsignados()
    {
        return new ArrayList<>(miembrosAsignados);
    }

    // --- Disponibilidad ---

    public void setDisponible(DiaSemana dia, Turno turno, boolean disponible)
    {
        disponibilidad[dia.ordinal()][turno.ordinal()] = disponible;
    }

    public boolean estaDisponible(DiaSemana dia, Turno turno)
    {
        return disponibilidad[dia.ordinal()][turno.ordinal()];
    }

    /** @return copia de la matriz de disponibilidad [día][turno] */
    public boolean[][] getDisponibilidad()
    {
        boolean[][] copia = new boolean[disponibilidad.length][];
        for(int i = 0; i < disponibilidad.length; i++) copia[i] = disponibilidad[i].clone();
        return copia;
    }

    /** Orden natural: por cantidad de miembros asignados; el código desempata. */
    @Override
    public int compareTo(Entrenador otro)
    {
        int r = Integer.compare(this.miembrosAsignados.size(), otro.miembrosAsignados.size());
        return (r != 0) ? r : this.codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString()
    {
        return codigo + " | " + getNombreCompleto() + " | " + turno + " | " + especialidades
                + " | " + miembrosAsignados.size() + "/" + MAX_MIEMBROS + " miembros";
    }
}
