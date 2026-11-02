package com.gimnasio.modelo;

public class Entrenador extends Persona
{
    private String especialidad;
    private double sueldo;

    public Entrenador(int id, String nombre, String dni, String telefono, String especialidad, double sueldo)
    {
        super(id, nombre, dni, telefono);
        this.especialidad = especialidad;
        this.sueldo = sueldo;
    }

    public String getEspecialidad()
    {
        return especialidad;
    }

    public double getSueldo()
    {
        return sueldo;
    }

    public void setEspecialidad(String especialidad)
    {
        this.especialidad = especialidad;
    }

    public void setSueldo(double sueldo)
    {
        this.sueldo = sueldo;
    }

    @Override
    public String getRol()
    {
        return "ENTRENADOR";
    }

    @Override
    public String toString()
    {
        return super.toString() + " - " + especialidad;
    }
}