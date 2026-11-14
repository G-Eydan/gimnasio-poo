package gympro.util;

import gympro.excepciones.DatosInvalidosException;

public class Validador
{
    public static void validarDni(String dni) throws DatosInvalidosException
    {
        if(dni == null || !dni.matches("\\d{8}"))
        {
            throw new DatosInvalidosException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
    }

    public static void validarEmail(String email) throws DatosInvalidosException
    {
        if(email == null || !email.contains("@"))
        {
            throw new DatosInvalidosException("El correo electrónico no es válido (falta '@').");
        }
    }

    public static void validarTelefono(String telefono) throws DatosInvalidosException
    {
        if(telefono == null || !telefono.matches("\\d{9}"))
        {
            throw new DatosInvalidosException("El teléfono debe tener exactamente 9 dígitos numéricos.");
        }
    }
}