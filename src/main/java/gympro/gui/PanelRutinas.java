package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.entrenamiento.Ejercicio;
import gympro.modelo.entrenamiento.Rutina;
import gympro.modelo.enums.GrupoMuscular;
import gympro.modelo.enums.Objetivo;
import gympro.modelo.persona.Miembro;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Pestaña «Rutinas» (HU-08 · T4).
 *
 * <ul>
 *   <li><b>NORTH</b>: {@link JComboBox} solo con miembros con membresía activa (con su
 *       entrenador y su rutina actual al lado), nombre, {@code JComboBox<Objetivo>} y
 *       botón «Nueva rutina» ({@code gimnasio.crearRutina}).</li>
 *   <li><b>WEST</b>: catálogo en un {@link JList} filtrado por grupo muscular.</li>
 * </ul>
 *
 * @author I3
 */
public final class PanelRutinas extends JPanel implements Actualizable
{
    private static final long serialVersionUID = 1L;
    private static final String TODOS = "Todos";

    private final transient Gimnasio gimnasio;

    private final JComboBox<Miembro> cboMiembro = new JComboBox<>();
    private final JLabel lblMiembro = new JLabel(" ");
    private final JTextField txtNombre = new JTextField(14);
    private final JComboBox<Objetivo> cboObjetivo = new JComboBox<>(Objetivo.values());

    private final JComboBox<Object> cboGrupo = new JComboBox<>();
    private final DefaultListModel<Ejercicio> modeloCatalogo = new DefaultListModel<>();
    private final JList<Ejercicio> lstCatalogo = new JList<>(modeloCatalogo);

    public PanelRutinas(Gimnasio gimnasio)
    {
        super(new BorderLayout(10, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(crearSeleccionMiembro(), BorderLayout.NORTH);
        add(crearCatalogo(), BorderLayout.WEST);

        actualizar();
    }

    /** Recarga los miembros activos (conservando el elegido) y el catálogo. */
    @Override
    public void actualizar()
    {
        Miembro antes = (Miembro) cboMiembro.getSelectedItem();
        cboMiembro.removeAllItems();
        for (Miembro m : gimnasio.listarMiembrosActivos())
        {
            cboMiembro.addItem(m);
        }
        if (antes != null)
        {
            cboMiembro.setSelectedItem(antes);
        }
        filtrarCatalogo();
        mostrarMiembro();
    }

    /** @return el miembro elegido en el combo, o {@code null} */
    private Miembro miembroElegido()
    {
        return (Miembro) cboMiembro.getSelectedItem();
    }

    /** @return la rutina actual del miembro elegido, o {@code null} */
    private Rutina rutinaActual()
    {
        Miembro m = miembroElegido();
        return (m == null) ? null : m.getRutinaActual();
    }

    // ------------------------------------------------------------------
    // Miembro y nueva rutina
    // ------------------------------------------------------------------

    private JPanel crearSeleccionMiembro()
    {
        cboMiembro.addActionListener(e -> mostrarMiembro());
        JButton btnNueva = new JButton("Nueva rutina");
        btnNueva.addActionListener(e -> nuevaRutina());

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila1.add(new JLabel("Miembro (membresía activa):"));
        fila1.add(cboMiembro);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila2.add(new JLabel("Nombre de la rutina:"));
        fila2.add(txtNombre);
        fila2.add(new JLabel("Objetivo:"));
        fila2.add(cboObjetivo);
        fila2.add(btnNueva);

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Miembro y rutina"));
        panel.add(fila1, BorderLayout.NORTH);
        panel.add(lblMiembro, BorderLayout.CENTER);
        panel.add(fila2, BorderLayout.SOUTH);
        return panel;
    }

    /** Muestra el entrenador y la rutina actual del miembro elegido. */
    private void mostrarMiembro()
    {
        Miembro m = miembroElegido();
        if (m == null)
        {
            lblMiembro.setText("  No hay miembros con membresía activa.");
            return;
        }
        Rutina r = m.getRutinaActual();
        lblMiembro.setText(String.format("  Entrenador: %s   ·   Rutina actual: %s   ·   Historial: %d rutina(s)",
                m.getEntrenador() == null ? "sin asignar" : m.getEntrenador().getNombreCompleto(),
                r == null ? "ninguna" : r.getCodigo() + " " + r.getNombre() + " (" + r.getObjetivo() + ")",
                m.getHistorialRutinas().size()));
    }

    /** Crea una rutina nueva; la anterior del miembro pasa a su historial (RN-10). */
    private void nuevaRutina()
    {
        Miembro m = miembroElegido();
        if (m == null)
        {
            return;
        }
        if (rutinaActual() != null)
        {
            int opcion = JOptionPane.showConfirmDialog(this, "La rutina actual de " + m.getNombreCompleto()
                    + " pasará al historial. ¿Continuar?", "Nueva rutina", JOptionPane.YES_NO_OPTION);
            if (opcion != JOptionPane.YES_OPTION)
            {
                return;
            }
        }
        try
        {
            gimnasio.crearRutina(m.getDni(), txtNombre.getText(), (Objetivo) cboObjetivo.getSelectedItem());
            txtNombre.setText("");
            mostrarMiembro();
        }
        catch (MiembroNoEncontradoException | DatosInvalidosException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Nueva rutina", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Catálogo
    // ------------------------------------------------------------------

    private JPanel crearCatalogo()
    {
        cboGrupo.addItem(TODOS);
        for (GrupoMuscular g : GrupoMuscular.values())
        {
            cboGrupo.addItem(g);
        }
        cboGrupo.addActionListener(e -> filtrarCatalogo());
        lstCatalogo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtro.add(new JLabel("Grupo:"));
        filtro.add(cboGrupo);

        JScrollPane scroll = new JScrollPane(lstCatalogo);
        scroll.setPreferredSize(new Dimension(240, 300));

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Catálogo de ejercicios"));
        panel.add(filtro, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void filtrarCatalogo()
    {
        Object grupo = cboGrupo.getSelectedItem();
        List<Ejercicio> lista = gimnasio.getCatalogoEjercicios(grupo instanceof GrupoMuscular ? (GrupoMuscular) grupo : null);
        modeloCatalogo.clear();
        for (Ejercicio e : lista)
        {
            modeloCatalogo.addElement(e);
        }
    }
}
