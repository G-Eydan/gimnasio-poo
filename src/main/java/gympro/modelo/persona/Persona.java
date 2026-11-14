package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.util.Validador;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Persona implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected String dni;
    protected String nombres;
    protected String apellidos;
    protected LocalDate fechaNacimiento;
    protected String telefono;
    protected String email;

    // Constructor corto
    public Persona(String dni, String nombres, String apellidos) throws DatosInvalidosException
    {
        this(dni, nombres, apellidos, null, null, null);
    }

    // Constructor largo (orden de la especificación: telefono antes que email)
    public Persona(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email) throws DatosInvalidosException
    {
        setDni(dni);
        setNombres(nombres);
        setApellidos(apellidos);
        this.fechaNacimiento = fechaNacimiento;
        if(telefono != null) setTelefono(telefono);
        if(email != null) setEmail(email);
    }

    public String getNombreCompleto()
    {
        return this.nombres + " " + this.apellidos;
    }

    public int getEdad()
    {
        if(fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public abstract String getRol();

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

    public void setDni(String dni) throws DatosInvalidosException
    {
        Validador.validarDni(dni);
        this.dni = dni;
    }

    public String getNombres()
    {
        return nombres;
    }

    public void setNombres(String nombres) throws DatosInvalidosException
    {
        if(nombres == null || nombres.trim().isEmpty())
            throw new DatosInvalidosException("Los nombres no pueden estar vacíos.");
        this.nombres = nombres;
    }

    public String getApellidos()
    {
        return apellidos;
    }

    public void setApellidos(String apellidos) throws DatosInvalidosException
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

    public void setTelefono(String telefono) throws DatosInvalidosException
    {
        Validador.validarTelefono(telefono);
        this.telefono = telefono;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email) throws DatosInvalidosException
    {
        Validador.validarEmail(email);
        this.email = email;
    }
}