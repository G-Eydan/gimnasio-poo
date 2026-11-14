package gympro.modelo.entrenamiento;

import gympro.excepciones.RutinaLlenaException;
import gympro.modelo.enums.DiaSemana;
import gympro.modelo.enums.Objetivo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Rutina de entrenamiento. Guarda sus ejercicios en un <b>arreglo estándar
 * parcialmente lleno</b> de hasta {@link #MAX_EJERCICIOS} elementos: solo las
 * posiciones {@code 0} a {@code numDetalles - 1} están en uso.
 *
 * @author I3
 */
public class Rutina implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int MAX_EJERCICIOS = 20;

    private String codigo;
    private Objetivo objetivo;
    private final DetalleRutina[] detalles = new DetalleRutina[MAX_EJERCICIOS];
    private int numDetalles;

    public Rutina(String codigo, Objetivo objetivo)
    {
        this.codigo = codigo;
        this.objetivo = objetivo;
        this.numDetalles = 0;
    }

    /**
     * Agrega un ejercicio al final de la rutina.
     *
     * @throws RutinaLlenaException si ya tiene {@link #MAX_EJERCICIOS} ejercicios
     */
    public void agregarDetalle(DetalleRutina detalle) throws RutinaLlenaException
    {
        if(estaLlena())
            throw new RutinaLlenaException("La rutina " + codigo + " ya tiene " + MAX_EJERCICIOS + " ejercicios.");
        detalles[numDetalles++] = detalle;
    }

    /** @return true si ya no se pueden agregar ejercicios */
    public boolean estaLlena()
    {
        return numDetalles == MAX_EJERCICIOS;
    }

    /**
     * Elimina el ejercicio de la posición indicada y desplaza los siguientes
     * una posición a la izquierda para no dejar huecos.
     *
     * @return false si el índice está fuera de rango
     */
    public boolean eliminarDetalle(int indice)
    {
        if(indice < 0 || indice >= numDetalles) return false;
        for(int i = indice; i < numDetalles - 1; i++) detalles[i] = detalles[i + 1];
        detalles[--numDetalles] = null;
        return true;
    }

    /** @return los ejercicios de ese día, en el orden en que se agregaron */
    public DetalleRutina[] getDetallesPorDia(DiaSemana dia)
    {
        int cuenta = 0;
        for(int i = 0; i < numDetalles; i++) if(detalles[i].getDia() == dia) cuenta++;
        DetalleRutina[] res = new DetalleRutina[cuenta];
        int k = 0;
        for(int i = 0; i < numDetalles; i++) if(detalles[i].getDia() == dia) res[k++] = detalles[i];
        return res;
    }

    /** @return suma de las series de todos los ejercicios */
    public int getTotalSeries()
    {
        int total = 0;
        for(int i = 0; i < numDetalles; i++) total += detalles[i].getSeries();
        return total;
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

    /** @return copia de los ejercicios en uso (sin las posiciones vacías) */
    public DetalleRutina[] getDetalles()
    {
        return Arrays.copyOf(detalles, numDetalles);
    }

    /** @return cantidad de ejercicios en la rutina */
    public int getNumDetalles()
    {
        return numDetalles;
    }

    @Override
    public String toString()
    {
        return codigo + " | " + objetivo + " | " + numDetalles + " ejercicio(s)";
    }
}
