package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.util.Validador;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Persona del gimnasio (clase abstracta): base de {@link Miembro} y {@link Entrenador}.
 *
 * <p>Los setters validan los datos con {@link Validador}; el teléfono, el email y la
 * fecha de nacimiento son opcionales ({@code null}). Dos personas son iguales si
 * tienen el mismo DNI.</p>
 *
 * @author I3
 */
public abstract class Persona implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected String dni;
    protected String nombres;
    protected String apellidos;
    protected LocalDate fechaNacimiento;
    protected String telefono;
    protected String email;

    /**
     * Constructor corto: solo los datos obligatorios.
     *
     * @throws DatosInvalidosException si el DNI no tiene 8 dígitos o los nombres o apellidos están vacíos
     */
    public Persona(String dni, String nombres, String apellidos) throws DatosInvalidosException
    {
        this(dni, nombres, apellidos, null, null, null);
    }

    /**
     * Constructor completo. {@code fechaNacimiento}, {@code telefono} y {@code email} pueden ser {@code null}.
     *
     * @throws DatosInvalidosException si algún dato no pasa la validación
     */
    public Persona(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email) throws DatosInvalidosException
    {
        setDni(dni);
        setNombres(nombres);
        setApellidos(apellidos);
        this.fechaNacimiento = fechaNacimiento;
        if(telefono != null) setTelefono(telefono);
        if(email != null) setEmail(email);
    }

    /** @return nombres y apellidos separados por un espacio */
    public String getNombreCompleto()
    {
        return this.nombres + " " + this.apellidos;
    }

    /** @return edad en años cumplidos, o 0 si no se registró la fecha de nacimiento */
    public int getEdad()
    {
        if(fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /** @return rol de la persona en el gimnasio (lo define cada subclase) */
    public abstract String getRol();

    @Override
    public String toString()
    {
        return getRol() + ": " + getNombreCompleto() + " (DNI " + dni + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona)o;
        return Objects.equals(dni, persona.dni);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(dni);
    }

    public String getDni()
    {
        return dni;
    }

    public final void setDni(String dni) throws DatosInvalidosException
    {
        Validador.validarDni(dni);
        this.dni = dni;
    }

    public String getNombres()
    {
        return nombres;
    }

    public final void setNombres(String nombres) throws DatosInvalidosException
    {
        if(nombres == null || nombres.trim().isEmpty())
            throw new DatosInvalidosException("Los nombres no pueden estar vacíos.");
        this.nombres = nombres;
    }

    public String getApellidos()
    {
        return apellidos;
    }

    public final void setApellidos(String apellidos) throws DatosInvalidosException
    {
        if(apellidos == null || apellidos.trim().isEmpty())
            throw new DatosInvalidosException("Los apellidos no pueden estar vacíos.");
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento()
    {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento)
    {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono()
    {
        return telefono;
    }

    public final void setTelefono(String telefono) throws DatosInvalidosException
    {
        Validador.validarTelefono(telefono);
        this.telefono = telefono;
    }

    public String getEmail()
    {
        return email;
    }

    public final void setEmail(String email) throws DatosInvalidosException
    {
        Validador.validarEmail(email);
        this.email = email;
    }
}