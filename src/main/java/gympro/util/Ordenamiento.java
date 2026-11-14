package gympro.util;

import gympro.modelo.membresia.Membresia;
import gympro.modelo.persona.Miembro;

/**
 * Algoritmos de ordenamiento escritos a mano (métodos estáticos).
 *
 * <p>Trabajan sobre arreglos parcialmente llenos: solo se ordenan las
 * posiciones {@code 0} a {@code n - 1}; el resto del arreglo no se toca.</p>
 *
 * @author I2
 */
public final class Ordenamiento
{
    private Ordenamiento()
    {
        // Clase de utilidades: no se instancia
    }

    /**
     * Ordena miembros por apellidos, de la A a la Z, sin distinguir mayúsculas.
     * Algoritmo: <b>selección</b>.
     *
     * @param a arreglo de miembros
     * @param n cantidad de elementos usados en el arreglo
     */
    public static void ordenarPorApellido(Miembro[] a, int n)
    {
        validarTamanio(a, n);
        for (int i = 0; i < n - 1; i++)
        {
            int menor = i;
            for (int j = i + 1; j < n; j++)
            {
                if (a[j].getApellidos().compareToIgnoreCase(a[menor].getApellidos()) < 0)
                {
                    menor = j;
                }
            }
            if (menor != i)
            {
                Miembro temp = a[i];
                a[i] = a[menor];
                a[menor] = temp;
            }
        }
    }

    /**
     * Ordena membresías por fecha de fin, de la que vence primero a la última
     * (usa el {@code compareTo} de {@link Membresia}).
     * Algoritmo: <b>inserción</b>.
     *
     * @param a arreglo de membresías
     * @param n cantidad de elementos usados en el arreglo
     */
    public static void ordenarPorVencimiento(Membresia[] a, int n)
    {
        validarTamanio(a, n);
        for (int i = 1; i < n; i++)
        {
            Membresia clave = a[i];
            int j = i - 1;
            while (j >= 0 && a[j].compareTo(clave) > 0)
            {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = clave;
        }
    }

    /**
     * Ordena miembros por DNI de menor a mayor. Es el orden que necesita
     * {@link Busqueda#busquedaBinaria(Miembro[], int, String)}.
     * Algoritmo: <b>burbuja</b> (se detiene si una pasada no hace intercambios).
     *
     * @param a arreglo de miembros
     * @param n cantidad de elementos usados en el arreglo
     */
    public static void ordenarPorDni(Miembro[] a, int n)
    {
        validarTamanio(a, n);
        boolean huboCambio = true;
        for (int pasada = 0; pasada < n - 1 && huboCambio; pasada++)
        {
            huboCambio = false;
            for (int j = 0; j < n - 1 - pasada; j++)
            {
                if (a[j].getDni().compareTo(a[j + 1].getDni()) > 0)
                {
                    Miembro temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    huboCambio = true;
                }
            }
        }
    }

    /**
     * Ordena valores de mayor a menor y mueve cada etiqueta junto con su valor
     * (arreglos paralelos). Se usa en reportes, por ejemplo planes más solicitados.
     * Algoritmo: <b>selección</b>.
     *
     * @param valores   valores numéricos
     * @param etiquetas etiqueta de cada valor (misma longitud que {@code valores})
     * @throws IllegalArgumentException si algún arreglo es nulo o sus longitudes difieren
     */
    public static void ordenarDescendente(double[] valores, String[] etiquetas)
    {
        if (valores == null || etiquetas == null)
        {
            throw new IllegalArgumentException("Los arreglos no pueden ser nulos.");
        }
        if (valores.length != etiquetas.length)
        {
            throw new IllegalArgumentException("Los arreglos deben tener la misma longitud.");
        }
        for (int i = 0; i < valores.length - 1; i++)
        {
            int mayor = i;
            for (int j = i + 1; j < valores.length; j++)
            {
                if (valores[j] > valores[mayor])
                {
                    mayor = j;
                }
            }
            if (mayor != i)
            {
                double tempValor = valores[i];
                valores[i] = valores[mayor];
                valores[mayor] = tempValor;

                String tempEtiqueta = etiquetas[i];
                etiquetas[i] = etiquetas[mayor];
                etiquetas[mayor] = tempEtiqueta;
            }
        }
    }

    /** Verifica que el arreglo exista y que {@code n} esté entre 0 y su longitud. */
    static void validarTamanio(Object[] a, int n)
    {
        if (a == null)
        {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (n < 0 || n > a.length)
        {
            throw new IllegalArgumentException("n debe estar entre 0 y " + a.length + ": " + n);
        }
    }
}
