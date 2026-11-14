package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.Especialidad;
import gympro.modelo.enums.Turno;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Entrenador extends Persona
{
    private static final long serialVersionUID = 1L;
    public static final int MAX_MIEMBROS = 15;

    private ArrayList<Especialidad> especialidades = new ArrayList<>();
    private double sueldoBase;
    private ArrayList<Miembro> miembrosAsignados = new ArrayList<>();
    private boolean[][] disponibilidad = new boolean[7][Turno.values().length]; // [día][turno]

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
    public void agregarEspecialidad(Especialidad e)
    {
        if(e != null && !especialidades.contains(e)) especialidades.add(e);
    }

    public ArrayList<Especialidad> getEspecialidades()
    {
        return especialidades;
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
    public boolean tieneCupo()
    {
        return miembrosAsignados.size() < MAX_MIEMBROS;
    }

    public boolean asignarMiembro(Miembro m)
    {
        if(m == null || !tieneCupo() || miembrosAsignados.contains(m)) return false;
        return miembrosAsignados.add(m);
    }

    public boolean quitarMiembro(Miembro m)
    {
        return miembrosAsignados.remove(m);
    }

    public ArrayList<Miembro> getMiembrosAsignados()
    {
        return miembrosAsignados;
    }

    // --- Disponibilidad ---
    private int indiceDia(DayOfWeek dia)
    {
        return dia.getValue() - 1; // lunes = 0 ... domingo = 6
    }

    public void setDisponible(DayOfWeek dia, Turno turno, boolean disponible)
    {
        disponibilidad[indiceDia(dia)][turno.ordinal()] = disponible;
    }

    public boolean estaDisponible(DayOfWeek dia, Turno turno)
    {
        return disponibilidad[indiceDia(dia)][turno.ordinal()];
    }

    public boolean[][] getDisponibilidad()
    {
        return disponibilidad;
    }
}