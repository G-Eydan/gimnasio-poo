package gympro.util;

import gympro.modelo.membresia.Membresia;
import java.util.List;

public class Ordenamiento
{
    public static void ordenarMembresiasPorVencimiento(List<Membresia> lista)
    {
        int n = lista.size();
        for(int i = 1; i < n; i++)
        {
            Membresia clave = lista.get(i);
            int j = i - 1;
            while(j >= 0 && lista.get(j).getFechaFin().isAfter(clave.getFechaFin()))
            {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, clave);
        }
    }
}