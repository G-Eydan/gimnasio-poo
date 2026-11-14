package gympro.util;

import gympro.excepciones.DatosInvalidosException;

/**
 * Validaciones de datos de entrada (métodos estáticos). Cada método lanza
 * {@link DatosInvalidosException} con un mensaje claro si el dato no es válido.
 *
 * @author I3
 */
public final class Validador
{
    private Validador()
    {
        // Clase de utilidades: no se instancia
    }

    /** El DNI debe tener exactamente 8 dígitos. */
    public static void validarDni(String dni) throws DatosInvalidosException
    {
        if(dni == null || !dni.matches("\\d{8}"))
        {
            throw new DatosInvalidosException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
    }

    /** El email debe contener "@". */
    public static void validarEmail(String email) throws DatosInvalidosException
    {
        if(email == null || !email.contains("@"))
        {
            throw new DatosInvalidosException("El correo electrónico no es válido (falta '@').");
        }
    }

    /** El teléfono debe tener exactamente 9 dígitos. */
    public static void validarTelefono(String telefono) throws DatosInvalidosException
    {
        if(telefono == null || !telefono.matches("\\d{9}"))
        {
            throw new DatosInvalidosException("El teléfono debe tener exactamente 9 dígitos numéricos.");
        }
    }

    /**
     * El valor debe estar entre {@code min} y {@code max} (incluidos).
     *
     * @param campo nombre del campo, para el mensaje de error
     */
    public static void validarRango(int valor, int min, int max, String campo) throws DatosInvalidosException
    {
        if(valor < min || valor > max)
        {
            throw new DatosInvalidosException(campo + " debe estar entre " + min + " y " + max + ".");
        }
    }
}
