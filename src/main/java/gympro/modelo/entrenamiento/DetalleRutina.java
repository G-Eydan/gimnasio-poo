package gympro.modelo.entrenamiento;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.DiaSemana;
import gympro.util.Validador;

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

    /** Series y repeticiones permitidas (RN-09). */
    public static final int MIN_SERIES = 1;
    public static final int MAX_SERIES = 10;
    public static final int MIN_REPETICIONES = 1;
    public static final int MAX_REPETICIONES = 50;

    /**
     * Constructor corto: sin peso (ejercicio con el propio cuerpo) y sin descanso indicado.
     *
     * @throws DatosInvalidosException si algún dato no cumple RN-09
     */
    public DetalleRutina(Ejercicio ejercicio, DiaSemana dia, int series, int repeticiones) throws DatosInvalidosException
    {
        this(ejercicio, dia, series, repeticiones, 0.0, 0);
    }

    /**
     * Constructor completo. Las reglas de RN-09 se validan aquí, en un solo lugar.
     *
     * @throws DatosInvalidosException si falta el ejercicio o el día, las series no están entre 1 y 10,
     *                                 las repeticiones entre 1 y 50, o el peso o el descanso son negativos
     */
    public DetalleRutina(Ejercicio ejercicio, DiaSemana dia, int series, int repeticiones, double peso, int descansoSeg)
            throws DatosInvalidosException
    {
        if(ejercicio == null) throw new DatosInvalidosException("Debe elegir un ejercicio.");
        if(dia == null) throw new DatosInvalidosException("Debe elegir un día.");
        Validador.validarRango(series, MIN_SERIES, MAX_SERIES, "Las series");
        Validador.validarRango(repeticiones, MIN_REPETICIONES, MAX_REPETICIONES, "Las repeticiones");
        if(peso < 0) throw new DatosInvalidosException("El peso no puede ser negativo.");
        if(descansoSeg < 0) throw new DatosInvalidosException("El descanso no puede ser negativo.");
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
