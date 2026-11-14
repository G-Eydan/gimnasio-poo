package gympro.modelo.entrenamiento;

public class DetalleRutina
{
    private Ejercicio ejercicio;
    private int series;
    private int repeticiones;
    private double peso;

    public DetalleRutina(Ejercicio ejercicio, int series, int repeticiones, double peso)
    {
        this.ejercicio = ejercicio;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    public Ejercicio getEjercicio()
    {
        return ejercicio;
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
}