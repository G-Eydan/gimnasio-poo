package gympro.modelo.entrenamiento;

import gympro.modelo.enums.Objetivo;

public class Rutina
{
    private String codigo;
    private Objetivo objetivo;
    private DetalleRutina[] detalles; // Arreglo fijo de tamaño predefinido
    private int n; // Contador actual de elementos en el arreglo

    public Rutina(String codigo, Objetivo objetivo, int capacidadMaxima)
    {
        this.codigo = codigo;
        this.objetivo = objetivo;
        this.detalles = new DetalleRutina[capacidadMaxima];
        this.n = 0;
    }

    public boolean agregarDetalle(DetalleRutina detalle)
    {
        if(n < detalles.length)
        {
            detalles[n++] = detalle;
            return true;
        }
        return false; // Rutina llena
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public Objetivo getObjetivo()
    {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo)
    {
        this.objetivo = objetivo;
    }

    public DetalleRutina[] getDetalles()
    {
        return detalles;
    }

    public int getN()
    {
        return n;
    }
}