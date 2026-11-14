package gympro.modelo.entrenamiento;

import gympro.modelo.enums.GrupoMuscular;

public class Ejercicio
{
    private String nombre;
    private GrupoMuscular grupoMuscular;

    public Ejercicio(String nombre, GrupoMuscular grupoMuscular)
    {
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public GrupoMuscular getGrupoMuscular()
    {
        return grupoMuscular;
    }

    public void setGrupoMuscular(GrupoMuscular grupoMuscular)
    {
        this.grupoMuscular = grupoMuscular;
    }
}