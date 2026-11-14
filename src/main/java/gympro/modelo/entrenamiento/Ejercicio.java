package gympro.modelo.entrenamiento;

import gympro.modelo.enums.GrupoMuscular;

import java.io.Serializable;

/**
 * Ejercicio del catálogo del gimnasio. Las rutinas lo usan por agregación
 * (el ejercicio existe aunque no esté en ninguna rutina).
 *
 * <p>Dos ejercicios son iguales si tienen el mismo nombre, sin distinguir mayúsculas.</p>
 *
 * @author I3
 */
public class Ejercicio implements Serializable
{
    private static final long serialVersionUID = 1L;

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

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof Ejercicio)) return false;
        Ejercicio otro = (Ejercicio) o;
        return nombre != null && nombre.equalsIgnoreCase(otro.nombre);
    }

    @Override
    public int hashCode()
    {
        return nombre == null ? 0 : nombre.toLowerCase().hashCode();
    }

    @Override
    public String toString()
    {
        return nombre + " (" + grupoMuscular + ")";
    }
}
