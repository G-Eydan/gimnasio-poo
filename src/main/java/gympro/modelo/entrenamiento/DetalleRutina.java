package gympro.modelo.entrenamiento;

import gympro.modelo.enums.DiaSemana;

import java.io.Serializable;

/**
 * Una línea de una rutina: qué ejercicio se hace, qué día, con cuántas series,
 * repeticiones, peso y descanso.
 *
 * @author I3
 */
public class DetalleRutina implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Ejercicio ejercicio;
    private DiaSemana dia;
    private int series;
    private int repeticiones;
    private double peso;
    private int descansoSeg;

    /** Constructor corto: sin peso (ejercicio con el propio cuerpo) y sin descanso indicado. */
    public DetalleRutina(Ejercicio ejercicio, DiaSemana dia, int series, int repeticiones)
    {
        this(ejercicio, dia, series, repeticiones, 0.0, 0);
    }

    public DetalleRutina(Ejercicio ejercicio, DiaSemana dia, int series, int repeticiones, double peso, int descansoSeg)
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

    public DiaSemana getDia()
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

    /** @return series × repeticiones × peso (kg levantados en total) */
    public double calcularVolumen()
    {
        return series * repeticiones * peso;
    }

    /** Ejemplo: {@code "Lunes | Press banca | 4 x 10 | 40 kg | 90 s"}. */
    @Override
    public String toString()
    {
        String nombreDia = dia.name().charAt(0) + dia.name().substring(1).toLowerCase();
        String textoPeso = (peso == Math.floor(peso)) ? String.valueOf((long) peso) : String.valueOf(peso);
        return nombreDia + " | " + ejercicio.getNombre() + " | " + series + " x " + repeticiones
                + " | " + textoPeso + " kg | " + descansoSeg + " s";
    }
}
