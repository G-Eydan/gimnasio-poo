package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.persona.Usuario;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Ventana de inicio de sesión (HU-12): usuario, contraseña y botón Ingresar,
 * organizados con {@link GridLayout}.
 *
 * <p>El botón y la tecla Enter en la contraseña usan el mismo {@link ActionListener}
 * (la clase interna {@link ControladorIngresar}). Si las credenciales son correctas,
 * la ventana se cierra y entrega el {@link Usuario} a quien la abrió; si no, muestra
 * el error con {@link JOptionPane}.</p>
 *
 * @author I2
 */
public final class VentanaLogin extends JFrame
{
    private static final long serialVersionUID = 1L;

    private final transient Gimnasio gimnasio;
    private final transient Consumer<Usuario> alIngresar;

    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtClave = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar");

    /**
     * @param gimnasio   servicio donde están registrados los usuarios
     * @param alIngresar qué hacer con el usuario autenticado (por ejemplo, abrir la ventana principal)
     */
    public VentanaLogin(Gimnasio gimnasio, Consumer<Usuario> alIngresar)
    {
        super(gimnasio.getNombre() + " - Iniciar sesión");
        this.gimnasio = gimnasio;
        this.alIngresar = alIngresar;

        JLabel titulo = new JLabel(gimnasio.getNombre(), SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 16, 4, 16));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(12, 20, 20, 20));
        formulario.add(new JLabel("Usuario:"));
        formulario.add(txtUsuario);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtClave);
        formulario.add(new JLabel());
        formulario.add(btnIngresar);

        ControladorIngresar controlador = new ControladorIngresar();
        btnIngresar.addActionListener(controlador);
        txtClave.addActionListener(controlador);
        txtUsuario.addActionListener(e -> txtClave.requestFocusInWindow());

        add(titulo, BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btnIngresar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    /** Valida las credenciales al pulsar Ingresar o Enter. */
    private class ControladorIngresar implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String usuario = txtUsuario.getText().trim();
            char[] clave = txtClave.getPassword();
            try
            {
                if (usuario.isEmpty() || clave.length == 0)
                {
                    throw new DatosInvalidosException("Ingrese el usuario y la contraseña.");
                }
                Usuario autenticado = gimnasio.iniciarSesion(usuario, new String(clave));
                dispose();
                alIngresar.accept(autenticado);
            }
            catch (DatosInvalidosException ex)
            {
                JOptionPane.showMessageDialog(VentanaLogin.this, ex.getMessage(),
                        "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
                txtClave.setText("");
                (usuario.isEmpty() ? txtUsuario : txtClave).requestFocusInWindow();
            }
            finally
            {
                Arrays.fill(clave, '\0');
            }
        }
    }
}
