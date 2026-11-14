package gympro.excepciones;

/**
 * El miembro ya tiene una membresía vigente o una renovación pendiente (RN-06).
 *
 * @author I3
 */
public class MembresiaActivaException extends GimnasioException
{
    private static final long serialVersionUID = 1L;

    public MembresiaActivaException(String mensaje)
    {
        super(mensaje);
    }
}
