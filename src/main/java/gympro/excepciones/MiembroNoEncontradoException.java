package gympro.excepciones;

/**
 * No existe un miembro con el DNI o nombre buscado.
 *
 * @author I3
 */
public class MiembroNoEncontradoException extends GimnasioException
{
    private static final long serialVersionUID = 1L;

    public MiembroNoEncontradoException(String mensaje)
    {
        super(mensaje);
    }
}
