package gympro.excepciones;

/**
 * Error al guardar o cargar datos (archivos o base de datos).
 *
 * @author I3
 */
public class PersistenciaException extends GimnasioException
{
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje)
    {
        super(mensaje);
    }

    /** Guarda también la excepción original (por ejemplo una IOException o SQLException). */
    public PersistenciaException(String mensaje, Throwable causa)
    {
        super(mensaje, causa);
    }
}
