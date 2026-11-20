package gympro.gui;

import gympro.modelo.persona.Usuario;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Ventana principal (HU-12 · T2): por ahora "vacía", con la estructura de la
 * especificación (sección 10.2) y solo los módulos que permite el rol del usuario.
 *
 * <ul>
 *   <li><b>NORTH</b>: {@link JMenuBar} (Archivo, Base de datos, Ayuda).</li>
 *   <li><b>CENTER</b>: {@link JTabbedPane} con una pestaña por módulo permitido.</li>
 *   <li><b>SOUTH</b>: barra de estado con el usuario, el rol y un reloj ({@link Timer}).</li>
 * </ul>
 *
 * <p>Las pestañas y menús que el rol no puede usar no se agregan
 * ({@link Usuario#puedeAcceder(String)}); por ejemplo, el entrenador no ve Reportes (CP-18).
 * El contenido de cada pestaña llega en el Sprint 3 (HG-01).</p>
 *
 * @author I2
 */
public final class VentanaPrincipal extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_RELOJ = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");

    /** Pestañas en el orden de la especificación: título y módulo que exige. */
    private static final String[][] PESTANAS = {
            {"Dashboard", Usuario.MODULO_DASHBOARD},
            {"Miembros", Usuario.MODULO_MIEMBROS},
            {"Planes y Pagos", Usuario.MODULO_PLANES_PAGOS},
            {"Entrenadores", Usuario.MODULO_ENTRENADORES},
            {"Rutinas", Usuario.MODULO_RUTINAS},
            {"Reportes", Usuario.MODULO_REPORTES},
    };

    private final transient Usuario usuario;
    private final JTabbedPane pestanas = new JTabbedPane();
    private final JLabel lblReloj = new JLabel();

    public VentanaPrincipal(Gimnasio gimnasio, Usuario usuario)
    {
        super(gimnasio.getNombre() + " - " + usuario.getUsername() + " (" + usuario.getRol() + ")");
        this.usuario = usuario;

        setJMenuBar(crearMenu());
        crearPestanas();
        add(pestanas, BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 500));
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

    // ------------------------------------------------------------------
    // Construcción
    // ------------------------------------------------------------------

    private JMenuBar crearMenu()
    {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        if (usuario.puedeAcceder(Usuario.MODULO_ARCHIVO))
        {
            for (String opcion : new String[]{"Importar CSV", "Exportar CSV", "Respaldar (.dat)", "Restaurar"})
            {
                archivo.add(opcionPendiente(opcion, "Sprint 4"));
            }
            archivo.addSeparator();
        }
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        archivo.add(salir);
        barra.add(archivo);

        if (usuario.puedeAcceder(Usuario.MODULO_BASE_DATOS))
        {
            JMenu bd = new JMenu("Base de datos");
            for (String opcion : new String[]{"Conectar", "Sincronizar", "Cargar"})
            {
                bd.add(opcionPendiente(opcion, "Sprint 5"));
            }
            barra.add(bd);
        }

        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem acercaDe = new JMenuItem("Acerca de");
        acercaDe.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "GymPro - Sistema de Administración de un Gimnasio\nEquipo: Los terrores del ArrayList\nUNSA 2026-B",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        ayuda.add(acercaDe);
        barra.add(ayuda);
        return barra;
    }

    private JMenuItem opcionPendiente(String texto, String sprint)
    {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> JOptionPane.showMessageDialog(this,
                texto + " estará disponible en el " + sprint + ".", texto, JOptionPane.INFORMATION_MESSAGE));
        return item;
    }

    /** Agrega solo las pestañas que el rol puede usar; cada una muestra un aviso hasta el Sprint 3. */
    private void crearPestanas()
    {
        for (String[] pestana : PESTANAS)
        {
            if (usuario.puedeAcceder(pestana[1]))
            {
                JLabel aviso = new JLabel(pestana[0] + ": disponible en el Sprint 3", SwingConstants.CENTER);
                aviso.setForeground(Color.GRAY);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(aviso, BorderLayout.CENTER);
                pestanas.addTab(pestana[0], panel);
            }
        }
    }

    private JPanel crearBarraEstado()
    {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        barra.add(new JLabel("Usuario: " + usuario.getUsername() + "   |   Rol: " + usuario.getRol()), BorderLayout.WEST);
        barra.add(lblReloj, BorderLayout.EAST);

        actualizarReloj();
        Timer reloj = new Timer(1000, e -> actualizarReloj());
        reloj.start();
        return barra;
    }

    private void actualizarReloj()
    {
        lblReloj.setText(LocalDateTime.now().format(FORMATO_RELOJ));
    }
}
