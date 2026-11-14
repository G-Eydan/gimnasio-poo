package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.EstadoInscripcion;
import gympro.modelo.membresia.Membresia;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class Miembro extends Persona implements Descontable, Comparable<Miembro>
{
    private static final long serialVersionUID = 1L;
    private static int contador = 0;

    private final String codigo;
    private List<Membresia> historialMembresias = new ArrayList<>();
    // Sprint 2: peso, altura, entrenador e historial de rutinas

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

    public abstract String getTipo();

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

    /**
     * @return true si aún no tiene ninguna membresía registrada
     */
    public boolean esPrimeraMembresia()
    {
        return historialMembresias.isEmpty();
    }

    /**
     * IMC = peso (kg) / altura² (m). Peso y altura pasan a ser atributos en el Sprint 2.
     */
    public double calcularIMC(double pesoKg, double alturaM)
    {
        if(pesoKg <= 0 || alturaM <= 0) throw new IllegalArgumentException("Peso y altura deben ser positivos");
        return pesoKg / (alturaM * alturaM);
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

    /**
     * Orden natural: apellidos, luego nombres, y DNI como desempate.
     */
    @Override
    public int compareTo(Miembro otro)
    {
        int r = this.apellidos.compareToIgnoreCase(otro.apellidos);
        if(r != 0) return r;
        r = this.nombres.compareToIgnoreCase(otro.nombres);
        if(r != 0) return r;
        return this.dni.compareTo(otro.dni);
    }
}