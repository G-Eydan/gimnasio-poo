package com.gimnasio.modelo;

public abstract class Persona
{
    private int id;
    private String nombre;
    private String dni;
    private String telefono;

    public Persona(int id, String nombre, String dni, String telefono)
    {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
    }

    public int getId()
    {
        return id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getDni()
    {
        return dni;
    }

    public String getTelefono()
    {
        return telefono;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    // Cada subclase define su propio rol (polimorfismo)
    public abstract String getRol();

    @Override
    public String toString()
    {
        return "[" + getRol() + "] #" + id + " - " + nombre + " (DNI: " + dni + ", Tel: " + telefono + ")";
    }
}