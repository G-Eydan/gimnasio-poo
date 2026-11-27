package gympro.gui;

import gympro.modelo.persona.Usuario;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Ventana principal de GymPro (HG-01), con la estructura de la especificación
 * (sección 10.2) y solo los módulos que permite el rol del usuario.
 *
 * <ul>
 *   <li><b>NORTH</b>: {@link JMenuBar} con Archivo, Base de datos y Ayuda, con
 *       mnemónicos (Alt + letra) y Ctrl+Q para salir.</li>
 *   <li><b>CENTER</b>: {@link JTabbedPane} con una pestaña por módulo permitido: Miembros,
 *       Planes y Pagos, Entrenadores y Rutinas tienen su panel; Dashboard y Reportes llegan
 *       en el Sprint 4. Al cambiar de pestaña, el panel {@link Actualizable} recarga sus
 *       datos (un miembro registrado en «Miembros» aparece en «Rutinas» sin reiniciar).</li>
 *   <li><b>SOUTH</b>: barra de estado con el usuario y el rol, un área de mensajes
 *       ({@link #mostrarMensaje(String)}) y un reloj que actualiza un {@link Timer}
 *       cada segundo.</li>
 * </ul>
 *
 * <p>Las pestañas y menús que el rol no puede usar no se agregan
 * ({@link Usuario#puedeAcceder(String)}); por ejemplo, el entrenador no ve Reportes (CP-18).
 * Al cerrar la ventana (botón X o Archivo → Salir) se pide confirmación con un
 * {@link WindowAdapter} y se detienen los {@link Timer}.</p>
 *
 * @author I2
 */
public final class VentanaPrincipal extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_RELOJ = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");
    private static final String MENSAJE_INICIAL = "Listo";
    /** Tiempo que un mensaje queda en la barra de estado antes de volver a «Listo». */
    private static final int DURACION_MENSAJE_MS = 5000;

    /** Pestañas en el orden de la especificación: título y módulo que exige. */
    private static final String[][] PESTANAS = {
            {"Dashboard", Usuario.MODULO_DASHBOARD},
            {"Miembros", Usuario.MODULO_MIEMBROS},
            {"Planes y Pagos", Usuario.MODULO_PLANES_PAGOS},
            {"Entrenadores", Usuario.MODULO_ENTRENADORES},
            {"Rutinas", Usuario.MODULO_RUTINAS},
            {"Reportes", Usuario.MODULO_REPORTES},
    };

    private final transient Gimnasio gimnasio;
    private final transient Usuario usuario;
    /** Qué hacer al cerrar sesión (por ejemplo, volver al login); {@code null} oculta la opción. */
    private final transient Runnable alCerrarSesion;
    private final JTabbedPane pestanas = new JTabbedPane();
    private final JLabel lblMensaje = new JLabel(MENSAJE_INICIAL);
    private final JLabel lblReloj = new JLabel();
    private final Timer reloj = new Timer(1000, e -> actualizarReloj());
    private final Timer limpiarMensaje = new Timer(DURACION_MENSAJE_MS, e -> lblMensaje.setText(MENSAJE_INICIAL));

    /** Ventana sin la opción «Cerrar sesión». */
    public VentanaPrincipal(Gimnasio gimnasio, Usuario usuario)
    {
        this(gimnasio, usuario, null);
    }

    /**
     * @param alCerrarSesion se ejecuta después de cerrar la ventana con Archivo → Cerrar sesión
     *                       (por ejemplo, abrir de nuevo el login); {@code null} oculta la opción
     */
    public VentanaPrincipal(Gimnasio gimnasio, Usuario usuario, Runnable alCerrarSesion)
    {
        super(gimnasio.getNombre() + " - " + usuario.getUsername() + " (" + usuario.getRol() + ")");
        this.gimnasio = gimnasio;
        this.usuario = usuario;
        this.alCerrarSesion = alCerrarSesion;

        setJMenuBar(crearMenu());
        crearPestanas();
        add(pestanas, BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                confirmarSalida();
            }
        });
        setMinimumSize(new Dimension(900, 600));
        setSize(1180, 800);
        setLocationRelativeTo(null);
    }

    /** Títulos de las pestañas visibles (útil para pruebas). */
    public String[] getTitulosPestanas()
    {
        String[] titulos = new String[pestanas.getTabCount()];
        for (int i = 0; i < titulos.length; i++)
        {
            titulos[i] = pestanas.getTitleAt(i);
        }
        return titulos;
    }

    /**
     * Muestra un mensaje en la barra de estado (por ejemplo, «Miembro registrado»).
     * A los 5 segundos vuelve a «Listo». Los paneles lo usan para confirmar acciones
     * sin abrir un {@link JOptionPane}.
     */
    public void mostrarMensaje(String mensaje)
    {
        lblMensaje.setText(mensaje == null || mensaje.isBlank() ? MENSAJE_INICIAL : mensaje);
        limpiarMensaje.restart();
    }

    /** Detiene los {@link Timer} para que no sigan corriendo con la ventana cerrada. */
    @Override
    public void dispose()
    {
        reloj.stop();
        limpiarMensaje.stop();
        super.dispose();
    }

    // ------------------------------------------------------------------
    // Construcción
    // ------------------------------------------------------------------

    private JMenuBar crearMenu()
    {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        archivo.setMnemonic(KeyEvent.VK_A);
        if (usuario.puedeAcceder(Usuario.MODULO_ARCHIVO))
        {
            for (String opcion : new String[]{"Importar CSV", "Exportar CSV", "Respaldar (.dat)", "Restaurar",
                    "Estadísticas de archivos"})
            {
                archivo.add(opcionPendiente(opcion, "Sprint 4"));
            }
            archivo.addSeparator();
        }
        if (alCerrarSesion != null)
        {
            JMenuItem cerrarSesion = new JMenuItem("Cerrar sesión");
            cerrarSesion.setMnemonic(KeyEvent.VK_C);
            cerrarSesion.addActionListener(e ->
            {
                dispose();
                alCerrarSesion.run();
            });
            archivo.add(cerrarSesion);
        }
        JMenuItem salir = new JMenuItem("Salir");
        salir.setMnemonic(KeyEvent.VK_S);
        salir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        salir.addActionListener(e -> confirmarSalida());
        archivo.add(salir);
        barra.add(archivo);

        if (usuario.puedeAcceder(Usuario.MODULO_BASE_DATOS))
        {
            JMenu bd = new JMenu("Base de datos");
            bd.setMnemonic(KeyEvent.VK_B);
            for (String opcion : new String[]{"Conectar", "Sincronizar (guardar todo)", "Cargar"})
            {
                bd.add(opcionPendiente(opcion, "Sprint 5"));
            }
            barra.add(bd);
        }

        JMenu ayuda = new JMenu("Ayuda");
        ayuda.setMnemonic(KeyEvent.VK_Y);
        JMenuItem acercaDe = new JMenuItem("Acerca de");
        acercaDe.addActionListener(e -> JOptionPane.showMessageDialog(this,
                gimnasio.getNombre() + " - Sistema de Administración de un Gimnasio\n"
                        + "Equipo: Los terrores del ArrayList\nUNSA 2026-B",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        ayuda.add(acercaDe);
        barra.add(ayuda);
        return barra;
    }

    private JMenuItem opcionPendiente(String texto, String sprint)
    {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> mostrarMensaje(texto + " estará disponible en el " + sprint + "."));
        return item;
    }

    /** Agrega solo las pestañas que el rol puede usar y recarga el panel al seleccionarlo. */
    private void crearPestanas()
    {
        for (String[] pestana : PESTANAS)
        {
            if (usuario.puedeAcceder(pestana[1]))
            {
                pestanas.addTab(pestana[0], crearPanel(pestana[0], pestana[1]));
            }
        }
        pestanas.addChangeListener(e ->
        {
            Component actual = pestanas.getSelectedComponent();
            if (actual instanceof Actualizable)
            {
                ((Actualizable) actual).actualizar();
            }
        });
    }

    /** Panel real de cada módulo; los que aún no existen muestran un aviso. */
    private JComponent crearPanel(String titulo, String modulo)
    {
        switch (modulo)
        {
            case Usuario.MODULO_MIEMBROS:
                return new PanelMiembros(gimnasio);
            case Usuario.MODULO_PLANES_PAGOS:
                return new PanelPlanesPagos(gimnasio);
            case Usuario.MODULO_ENTRENADORES:
                return new PanelEntrenadores(gimnasio);
            case Usuario.MODULO_RUTINAS:
                return new PanelRutinas(gimnasio);
            default:
                return panelPendiente(titulo, "Sprint 4");
        }
    }

    private static JPanel panelPendiente(String titulo, String sprint)
    {
        JLabel aviso = new JLabel(titulo + ": disponible en el " + sprint, SwingConstants.CENTER);
        aviso.setForeground(Color.GRAY);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(aviso, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraEstado()
    {
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        barra.add(new JLabel("Usuario: " + usuario.getUsername() + "   |   Rol: " + usuario.getRol()), BorderLayout.WEST);
        barra.add(lblMensaje, BorderLayout.CENTER);
        barra.add(lblReloj, BorderLayout.EAST);

        limpiarMensaje.setRepeats(false);
        actualizarReloj();
        reloj.start();
        return barra;
    }

    private void actualizarReloj()
    {
        lblReloj.setText(LocalDateTime.now().format(FORMATO_RELOJ));
    }

    /** Pide confirmación; si el usuario acepta, cierra la ventana y termina el programa. */
    private void confirmarSalida()
    {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir de " + gimnasio.getNombre() + "?",
                "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION)
        {
            dispose();
            System.exit(0);
        }
    }
}
