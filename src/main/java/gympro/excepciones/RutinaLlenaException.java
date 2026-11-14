package gympro.excepciones;

/**
 * La rutina ya tiene el máximo de ejercicios ({@code Rutina.MAX_EJERCICIOS}).
 *
 * @author I3
 */
public class RutinaLlenaException extends GimnasioException
{
    private static final long serialVersionUID = 1L;

    public RutinaLlenaException(String mensaje)
    {
        super(mensaje);
    }
}
