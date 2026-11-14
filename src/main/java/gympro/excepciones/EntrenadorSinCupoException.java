package gympro.excepciones;

/**
 * El entrenador ya tiene el máximo de miembros asignados ({@code Entrenador.MAX_MIEMBROS}).
 *
 * @author I3
 */
public class EntrenadorSinCupoException extends GimnasioException
{
    private static final long serialVersionUID = 1L;

    public EntrenadorSinCupoException(String mensaje)
    {
        super(mensaje);
    }
}
