package gympro.modelo.entrenamiento;

import java.util.ArrayList;
import java.util.List;

public class Rutina
{
    private String nombre;
    private String objetivo;
    private List<DetalleRutina> detalles = new ArrayList<>();

    public Rutina(String nombre, String objetivo)
    {
        this.nombre = nombre;
        this.objetivo = objetivo;
    }

    public void agregarDetalle(DetalleRutina detalle)
    {
        this.detalles.add(detalle);
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getObjetivo()
    {
        return objetivo;
    }

    public List<DetalleRutina> getDetalles()
    {
        return detalles;
    }
}