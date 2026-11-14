package gympro.excepciones;

/**
 * Un dato ingresado no cumple las validaciones (DNI, email, teléfono, precio, nombre vacío, etc.).
 *
 * @author I3
 */
public class DatosInvalidosException extends GimnasioException
{
    private static final long serialVersionUID = 1L;

    public DatosInvalidosException(String mensaje)
    {
        super(mensaje);
    }
}
