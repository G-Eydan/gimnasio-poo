package gympro.excepciones;

/**
 * Excepción base de GymPro: todas las excepciones propias del sistema heredan de ella.
 *
 * @author I3
 */
public class GimnasioException extends Exception
{
    private static final long serialVersionUID = 1L;

    public GimnasioException(String mensaje)
    {
        super(mensaje);
    }

    /** Guarda también la excepción original (por ejemplo una IOException o SQLException). */
    public GimnasioException(String mensaje, Throwable causa)
    {
        super(mensaje, causa);
    }
}
