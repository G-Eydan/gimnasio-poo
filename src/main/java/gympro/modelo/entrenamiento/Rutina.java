package gympro.modelo.entrenamiento;

import gympro.excepciones.RutinaLlenaException;
import gympro.modelo.enums.Objetivo;
import java.time.DayOfWeek;

public class Rutina
{
    public static final int MAX_EJERCICIOS = 20;

    private String codigo;
    private Objetivo objetivo;
    private DetalleRutina[] detalles = new DetalleRutina[MAX_EJERCICIOS]; // arreglo parcial
    private int n; // elementos usados

    public Rutina(String codigo, Objetivo objetivo)
    {
        this.codigo = codigo;
        this.objetivo = objetivo;
        this.n = 0;
    }

    public void agregarDetalle(DetalleRutina detalle) throws RutinaLlenaException
    {
        if(n >= MAX_EJERCICIOS)
            throw new RutinaLlenaException("La rutina " + codigo + " ya tiene " + MAX_EJERCICIOS + " ejercicios");
        detalles[n++] = detalle;
    }

    // --- Métodos de la §7.3 (ajustar a la especificación) ---
    public boolean estaLlena()
    {
        return n == MAX_EJERCICIOS;
    }

    public boolean eliminarDetalle(int indice)
    {
        if(indice < 0 || indice >= n) return false;
        for(int i = indice; i < n - 1; i++) detalles[i] = detalles[i + 1];
        detalles[--n] = null;
        return true;
    }

    public DetalleRutina[] getDetallesPorDia(DayOfWeek dia)
    {
        int cuenta = 0;
        for(int i = 0; i < n; i++) if(detalles[i].getDia() == dia) cuenta++;
        DetalleRutina[] res = new DetalleRutina[cuenta];
        int k = 0;
        for(int i = 0; i < n; i++) if(detalles[i].getDia() == dia) res[k++] = detalles[i];
        return res;
    }

    public int getTotalSeries()
    {
        int total = 0;
        for(int i = 0; i < n; i++) total += detalles[i].getSeries();
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

    public DetalleRutina[] getDetalles()
    {
        return detalles;
    }

    public int getN()
    {
        return n;
    }
}