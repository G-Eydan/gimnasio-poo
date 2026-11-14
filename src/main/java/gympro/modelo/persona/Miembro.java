package gympro.modelo.persona;

import gympro.modelo.enums.EstadoInscripcion;
import gympro.modelo.membresia.Membresia;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Miembro extends Persona implements Comparable<Miembro>
{
    private String codigo;
    private List<Membresia> historialMembresias = new ArrayList<>();

    public Miembro(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String email, String telefono, String codigo)
    {
        super(dni, nombres, apellidos, fechaNacimiento, email, telefono);
        this.codigo = codigo;
    }

    @Override
    public String getRol()
    {
        return "MIEMBRO";
    }

    public void agregarMembresia(Membresia m)
    {
        this.historialMembresias.add(m);
    }

    public List<Membresia> getHistorialMembresias()
    {
        return historialMembresias;
    }

    public Membresia getMembresiaActual()
    {
        if(historialMembresias.isEmpty()) return null;
        return historialMembresias.get(historialMembresias.size() - 1);
    }

    public long getDiasRestantes()
    {
        Membresia actual = getMembresiaActual();
        if(actual == null || LocalDate.now().isAfter(actual.getFechaFin()))
        {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), actual.getFechaFin());
    }

    public EstadoInscripcion getEstadoInscripcion()
    {
        Membresia actual = getMembresiaActual();
        if(actual == null) return EstadoInscripcion.SIN_MEMBRESIA;

        long dias = getDiasRestantes();
        if(LocalDate.now().isAfter(actual.getFechaFin()))
        {
            return EstadoInscripcion.VENCIDO;
        }
        else if(dias <= 7)
        {
            return EstadoInscripcion.POR_VENCER;
        }
        return EstadoInscripcion.ACTIVO;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    @Override
    public int compareTo(Miembro otro)
    {
        return this.dni.compareTo(otro.getDni()); // Ordenamiento natural por DNI
    }
}