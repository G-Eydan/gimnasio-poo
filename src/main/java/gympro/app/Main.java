package gympro.app;

import gympro.excepciones.DatosInvalidosException;
import gympro.gui.VentanaLogin;
import gympro.gui.VentanaPrincipal;
import gympro.modelo.enums.Rol;
import gympro.modelo.persona.Usuario;
import gympro.servicio.Gimnasio;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de GymPro. Abre la ventana de login (HU-12) y, al ingresar, la
 * ventana principal con los módulos que permite el rol. Desde el Sprint 3 todo se usa
 * desde la GUI (decisión D3): el menú de consola ({@link MenuConsola}) ya no se abre en
 * paralelo, aunque la clase se conserva.
 *
 * @author I2
 */
public class Main
{
    public static void main(String[] args) throws DatosInvalidosException
    {
        Gimnasio gimnasio = new Gimnasio("GymPro");
        registrarUsuariosDePrueba(gimnasio);

        SwingUtilities.invokeLater(() -> abrirLogin(gimnasio));
    }

    /** Muestra el login; al ingresar abre la ventana principal, y al cerrar sesión vuelve aquí. */
    private static void abrirLogin(Gimnasio gimnasio)
    {
        new VentanaLogin(gimnasio, usuario ->
                new VentanaPrincipal(gimnasio, usuario, () -> abrirLogin(gimnasio)).setVisible(true))
                .setVisible(true);
    }

    /** Los 3 usuarios de prueba de HU-12, uno por rol. */
    private static void registrarUsuariosDePrueba(Gimnasio gimnasio) throws DatosInvalidosException
    {
        gimnasio.registrarUsuario(new Usuario("admin", "admin123", Rol.ADMINISTRADOR));
        gimnasio.registrarUsuario(new Usuario("recepcion", "recepcion123", Rol.RECEPCIONISTA));
        gimnasio.registrarUsuario(new Usuario("entrenador", "entrenador123", Rol.ENTRENADOR));
    }
}
