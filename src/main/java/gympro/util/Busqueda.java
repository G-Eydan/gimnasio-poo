package gympro.util;

import gympro.modelo.persona.Miembro;

/**
 * Algoritmos de búsqueda escritos a mano (métodos estáticos).
 *
 * <p>Trabajan sobre arreglos parcialmente llenos: solo se revisan las
 * posiciones {@code 0} a {@code n - 1}.</p>
 *
 * @author I2
 */
public final class Busqueda
{
    private Busqueda()
    {
        // Clase de utilidades: no se instancia
    }

    /**
     * Busca el primer miembro con esos apellidos (sin distinguir mayúsculas
     * ni espacios al inicio o al final). No necesita el arreglo ordenado.
     * Algoritmo: <b>búsqueda lineal</b>.
     *
     * @param a        arreglo de miembros
     * @param n        cantidad de elementos usados en el arreglo
     * @param apellido apellidos a buscar
     * @return índice del miembro, o -1 si no está
     */
    public static int busquedaLineal(Miembro[] a, int n, String apellido)
    {
        Ordenamiento.validarTamanio(a, n);
        if (apellido == null)
        {
            return -1;
        }
        String buscado = apellido.trim();
        for (int i = 0; i < n; i++)
        {
            if (a[i].getApellidos().trim().equalsIgnoreCase(buscado))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Busca un miembro por DNI.
     * Algoritmo: <b>búsqueda binaria</b>.
     *
     * <p><b>Requisito:</b> el arreglo debe estar ordenado por DNI con
     * {@link Ordenamiento#ordenarPorDni(Miembro[], int)}; si no, el resultado
     * no es confiable.</p>
     *
     * @param a   arreglo de miembros ordenado por DNI
     * @param n   cantidad de elementos usados en el arreglo
     * @param dni DNI a buscar
     * @return índice del miembro, o -1 si no está
     */
    public static int busquedaBinaria(Miembro[] a, int n, String dni)
    {
        Ordenamiento.validarTamanio(a, n);
        if (dni == null)
        {
            return -1;
        }
        int inicio = 0;
        int fin = n - 1;
        while (inicio <= fin)
        {
            int medio = inicio + (fin - inicio) / 2;
            int comparacion = a[medio].getDni().compareTo(dni);
            if (comparacion == 0)
            {
                return medio;
            }
            if (comparacion < 0)
            {
                inicio = medio + 1;
            }
            else
            {
                fin = medio - 1;
            }
        }
        return -1;
    }
}
