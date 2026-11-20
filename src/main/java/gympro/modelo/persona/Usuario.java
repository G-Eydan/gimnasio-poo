package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.Rol;
import java.io.Serializable;

/**
 * Usuario del sistema (HU-12): ingresa con usuario y contraseña, y según su {@link Rol}
 * puede acceder a unos módulos u otros (sección 3 de la especificación).
 *
 * <p>Los nombres de los módulos son las constantes {@code MODULO_*}; coinciden con las
 * pestañas y menús de la ventana principal (Sprint 3).</p>
 *
 * @author I2
 */
public class Usuario implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String MODULO_DASHBOARD = "DASHBOARD";
    public static final String MODULO_MIEMBROS = "MIEMBROS";
    public static final String MODULO_PLANES_PAGOS = "PLANES_PAGOS";
    public static final String MODULO_ENTRENADORES = "ENTRENADORES";
    public static final String MODULO_RUTINAS = "RUTINAS";
    public static final String MODULO_REPORTES = "REPORTES";
    public static final String MODULO_ARCHIVO = "ARCHIVO";
    public static final String MODULO_BASE_DATOS = "BASE_DATOS";

    private final String username;
    private final String password;
    private final Rol rol;
    /** Persona asociada (por ejemplo, el entrenador); puede ser {@code null}. */
    private final Persona persona;

    /**
     * @throws DatosInvalidosException si el usuario o la contraseña están vacíos o falta el rol
     */
    public Usuario(String username, String password, Rol rol, Persona persona) throws DatosInvalidosException
    {
        if (username == null || username.isBlank())
        {
            throw new DatosInvalidosException("El nombre de usuario no puede estar vacío.");
        }
        if (password == null || password.isEmpty())
        {
            throw new DatosInvalidosException("La contraseña no puede estar vacía.");
        }
        if (rol == null)
        {
            throw new DatosInvalidosException("El usuario debe tener un rol.");
        }
        this.username = username.trim();
        this.password = password;
        this.rol = rol;
        this.persona = persona;
    }

    /** Usuario sin persona asociada. */
    public Usuario(String username, String password, Rol rol) throws DatosInvalidosException
    {
        this(username, password, rol, null);
    }

    /**
     * @return {@code true} si el usuario (sin distinguir mayúsculas) y la contraseña (exacta) coinciden
     */
    public boolean autenticar(String username, String password)
    {
        return username != null && password != null
                && this.username.equalsIgnoreCase(username.trim())
                && this.password.equals(password);
    }

    /**
     * Indica si el rol de este usuario puede usar un módulo.
     * <ul>
     *   <li>ADMINISTRADOR: todos los módulos.</li>
     *   <li>RECEPCIONISTA: dashboard, miembros, y planes y pagos.</li>
     *   <li>ENTRENADOR: miembros (consulta) y rutinas; no ve reportes de ingresos (CP-18).</li>
     * </ul>
     *
     * @param modulo una de las constantes {@code MODULO_*}
     */
    public boolean puedeAcceder(String modulo)
    {
        if (modulo == null)
        {
            return false;
        }
        switch (rol)
        {
            case ADMINISTRADOR:
                return true;
            case RECEPCIONISTA:
                return modulo.equals(MODULO_DASHBOARD) || modulo.equals(MODULO_MIEMBROS)
                        || modulo.equals(MODULO_PLANES_PAGOS);
            case ENTRENADOR:
                return modulo.equals(MODULO_MIEMBROS) || modulo.equals(MODULO_RUTINAS);
            default:
                return false;
        }
    }

    public String getUsername()
    {
        return username;
    }

    public Rol getRol()
    {
        return rol;
    }

    public Persona getPersona()
    {
        return persona;
    }

    /** No incluye la contraseña. */
    @Override
    public String toString()
    {
        return username + " (" + rol + ")";
    }
}
