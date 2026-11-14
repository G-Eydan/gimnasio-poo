package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.DiaSemana;
import gympro.modelo.enums.Especialidad;
import gympro.modelo.enums.Turno;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Entrenador del gimnasio. Tiene especialidades, un cupo de hasta
 * {@link #MAX_MIEMBROS} miembros asignados (agregación) y una matriz de
 * disponibilidad {@code [día][turno]} (arreglo 2D de 7 × 3).
 *
 * @author I3
 */
public class Entrenador extends Persona
{
    private static final long serialVersionUID = 1L;
    public static final int MAX_MIEMBROS = 15;

    private final ArrayList<Especialidad> especialidades = new ArrayList<>();
    private double sueldoBase;
    private final ArrayList<Miembro> miembrosAsignados = new ArrayList<>();
    /** Fila = {@link DiaSemana} (0 = lunes), columna = {@link Turno}. */
    private final boolean[][] disponibilidad = new boolean[DiaSemana.values().length][Turno.values().length];

    /**
     * @throws DatosInvalidosException si algún dato personal no pasa la validación
     */
    public Entrenador(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email, double sueldoBase) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos, fechaNacimiento, telefono, email);
        this.sueldoBase = sueldoBase;
    }

    @Override
    public String getRol()
    {
        return "ENTRENADOR";
    }

    // --- Especialidades ---

    /** Agrega una especialidad si no la tenía. */
    public void agregarEspecialidad(Especialidad e)
    {
        if(e != null && !especialidades.contains(e)) especialidades.add(e);
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

    // --- Miembros asignados ---

    /** @return true si tiene menos de {@link #MAX_MIEMBROS} miembros asignados */
    public boolean tieneCupo()
    {
        return miembrosAsignados.size() < MAX_MIEMBROS;
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

    @Override
    public String toString()
    {
        return super.toString() + " | " + especialidades + " | " + miembrosAsignados.size() + "/" + MAX_MIEMBROS + " miembros";
    }
}
