package gympro.modelo.entrenamiento;

import java.time.DayOfWeek;

public class DetalleRutina
{
    private Ejercicio ejercicio;
    private DayOfWeek dia;
    private int series;
    private int repeticiones;
    private double peso;
    private int descansoSeg;

    public DetalleRutina(Ejercicio ejercicio, DayOfWeek dia, int series, int repeticiones, double peso, int descansoSeg)
    {
        this.ejercicio = ejercicio;
        this.dia = dia;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.descansoSeg = descansoSeg;
    }

    public Ejercicio getEjercicio()
    {
        return ejercicio;
    }

    public DayOfWeek getDia()
    {
        return dia;
    }

    public int getSeries()
    {
        return series;
    }

    public int getRepeticiones()
    {
        return repeticiones;
    }

    public double getPeso()
    {
        return peso;
    }

    public int getDescansoSeg()
    {
        return descansoSeg;
    }
}