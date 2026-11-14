package gympro.modelo.persona;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Persona
{
    protected String dni;
    protected String nombres;
    protected String apellidos;
    protected LocalDate fechaNacimiento;
    protected String email;
    protected String telefono;

    public Persona(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String email, String telefono)
    {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.telefono = telefono;
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

    public void setDni(String dni)
    {
        this.dni = dni;
    }

    public String getNombres()
    {
        return nombres;
    }

    public void setNombres(String nombres)
    {
        this.nombres = nombres;
    }

    public String getApellidos()
    {
        return apellidos;
    }

    public void setApellidos(String apellidos)
    {
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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getTelefono()
    {
        return telefono;
    }

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }
}