package gympro.app;

import gympro.excepciones.DatosInvalidosException;
import gympro.gui.VentanaLogin;
import gympro.modelo.enums.Rol;
import gympro.modelo.persona.Usuario;
import gympro.servicio.Gimnasio;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de GymPro. Desde el Sprint 2 abre primero la ventana de login (HU-12)
 * y, al ingresar, el menú de consola. La ventana principal según el rol llega con HU-12 T2.
 *
 * @author I2
 */
public class Main
{
    public static void main(String[] args) throws DatosInvalidosException
    {
        Gimnasio gimnasio = new Gimnasio("GymPro");
        registrarUsuariosDePrueba(gimnasio);

        SwingUtilities.invokeLater(() ->
                new VentanaLogin(gimnasio, usuario -> abrirMenuConsola(gimnasio, usuario)).setVisible(true));
    }

    /** Los 3 usuarios de prueba de HU-12, uno por rol. */
    private static void registrarUsuariosDePrueba(Gimnasio gimnasio) throws DatosInvalidosException
    {
        gimnasio.registrarUsuario(new Usuario("admin", "admin123", Rol.ADMINISTRADOR));
        gimnasio.registrarUsuario(new Usuario("recepcion", "recepcion123", Rol.RECEPCIONISTA));
        gimnasio.registrarUsuario(new Usuario("entrenador", "entrenador123", Rol.ENTRENADOR));
    }

    /** El menú de consola lee del teclado, así que corre en su propio hilo y no en el de Swing. */
    private static void abrirMenuConsola(Gimnasio gimnasio, Usuario usuario)
    {
        Thread hilo = new Thread(() ->
        {
            System.out.println("Sesión iniciada: " + usuario);
            new MenuConsola(gimnasio).iniciar();
            System.exit(0);
        }, "menu-consola");
        hilo.start();
    }
}
