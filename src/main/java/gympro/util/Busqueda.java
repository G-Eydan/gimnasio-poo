package gympro.util;

import gympro.modelo.persona.Miembro;
import java.util.List;

public class Busqueda
{
    public static int busquedaBinariaPorDni(List<Miembro> lista, String dniBuscado)
    {
        int inicio = 0;
        int fin = lista.size() - 1;

        while(inicio <= fin)
        {
            int medio = inicio + (fin - inicio) / 2;
            int comparacion = lista.get(medio).getDni().compareTo(dniBuscado);

            if(comparacion == 0) return medio;
            if(comparacion < 0) inicio = medio + 1;
            else fin = medio - 1;
        }
        return -1;
    }
}