package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.excepciones.RutinaLlenaException;
import gympro.modelo.entrenamiento.DetalleRutina;
import gympro.modelo.entrenamiento.Ejercicio;
import gympro.modelo.entrenamiento.Rutina;
import gympro.modelo.enums.DiaSemana;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;

/**
 * Pestaña «Rutinas» (HU-08 · T4).
 *
 * <ul>
 *   <li><b>NORTH</b>: {@link JComboBox} solo con miembros con membresía activa (con su
 *       entrenador y su rutina actual al lado), nombre, {@code JComboBox<Objetivo>} y
 *       botón «Nueva rutina» ({@code gimnasio.crearRutina}).</li>
 *   <li><b>WEST</b>: catálogo en un {@link JList} filtrado por grupo muscular.</li>
 *   <li><b>CENTER</b>: día y cuatro {@link JSpinner} con los límites de RN-09 (el usuario no
 *       puede escribir valores fuera de rango), botones Agregar, Quitar y Ordenar por día,
 *       {@link JTable} con la rutina y {@link JProgressBar} de 0 a {@link Rutina#MAX_EJERCICIOS}.</li>
 * </ul>
 *
 * <p>«Exportar .txt» (HU-15) y el historial detallado (HU-09) quedan para el
 * Sprint 4.</p>
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

    private final JComboBox<DiaSemana> cboDia = new JComboBox<>(DiaSemana.values());
    private final JSpinner spSeries = new JSpinner(new SpinnerNumberModel(3,
            DetalleRutina.MIN_SERIES, DetalleRutina.MAX_SERIES, 1));
    private final JSpinner spReps = new JSpinner(new SpinnerNumberModel(10,
            DetalleRutina.MIN_REPETICIONES, DetalleRutina.MAX_REPETICIONES, 1));
    private final JSpinner spPeso = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 500.0, 2.5));
    private final JSpinner spDescanso = new JSpinner(new SpinnerNumberModel(60, 0, 600, 15));
    private final RutinaTableModel modeloRutina = new RutinaTableModel();
    private final JTable tablaRutina = new JTable(modeloRutina);
    private final JProgressBar barra = new JProgressBar(0, Rutina.MAX_EJERCICIOS);
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnQuitar = new JButton("Quitar");
    private final JButton btnOrdenar = new JButton("Ordenar por día");

    public PanelRutinas(Gimnasio gimnasio)
    {
        super(new BorderLayout(10, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(crearSeleccionMiembro(), BorderLayout.NORTH);
        add(crearCatalogo(), BorderLayout.WEST);
        add(crearRutina(), BorderLayout.CENTER);

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
            mostrarRutina();
            return;
        }
        Rutina r = m.getRutinaActual();
        mostrarRutina();
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
    // ------------------------------------------------------------------
    // Rutina: agregar, quitar, ordenar
    // ------------------------------------------------------------------

    private JPanel crearRutina()
    {
        btnAgregar.addActionListener(e -> agregarEjercicio());
        btnQuitar.addActionListener(e -> quitarEjercicio());
        btnOrdenar.addActionListener(e -> ordenarPorDia());
        JButton btnExportar = new JButton("Exportar .txt");
        btnExportar.setEnabled(false);
        btnExportar.setToolTipText("Disponible en el Sprint 4 (HU-15)");

        JPanel datos = new JPanel(new GridLayout(0, 2, 6, 4));
        datos.add(new JLabel("Día:"));
        datos.add(cboDia);
        datos.add(new JLabel("Series (1-10):"));
        datos.add(spSeries);
        datos.add(new JLabel("Repeticiones (1-50):"));
        datos.add(spReps);
        datos.add(new JLabel("Peso (kg):"));
        datos.add(spPeso);
        datos.add(new JLabel("Descanso (s):"));
        datos.add(spDescanso);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnAgregar);
        botones.add(btnQuitar);
        botones.add(btnOrdenar);
        botones.add(btnExportar);

        JPanel controles = new JPanel(new BorderLayout(0, 6));
        controles.add(datos, BorderLayout.NORTH);
        controles.add(botones, BorderLayout.SOUTH);

        tablaRutina.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRutina.setFillsViewportHeight(true);
        tablaRutina.getSelectionModel().addListSelectionListener(e -> actualizarBotones());
        barra.setStringPainted(true);

        JPanel derecha = new JPanel(new BorderLayout(0, 4));
        derecha.add(new JScrollPane(tablaRutina), BorderLayout.CENTER);
        derecha.add(barra, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Ejercicios de la rutina actual"));
        panel.add(controles, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.CENTER);
        return panel;
    }

    /** Repinta la tabla y la barra con la rutina actual del miembro elegido. */
    private void mostrarRutina()
    {
        Rutina r = rutinaActual();
        modeloRutina.setRutina(r);
        int n = (r == null) ? 0 : r.getNumDetalles();
        barra.setValue(n);
        barra.setString(n + " / " + Rutina.MAX_EJERCICIOS);
        actualizarBotones();
    }

    private void actualizarBotones()
    {
        Rutina r = rutinaActual();
        btnAgregar.setEnabled(r != null && !r.estaLlena());
        btnQuitar.setEnabled(r != null && tablaRutina.getSelectedRow() >= 0);
        btnOrdenar.setEnabled(r != null && r.getNumDetalles() > 1);
    }

    /** Agrega el ejercicio del catálogo con los valores de los spinners. */
    private void agregarEjercicio()
    {
        Rutina r = rutinaActual();
        Ejercicio ejercicio = lstCatalogo.getSelectedValue();
        if (r == null)
        {
            return;
        }
        if (ejercicio == null)
        {
            JOptionPane.showMessageDialog(this, "Elija un ejercicio del catálogo.", "Agregar",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try
        {
            r.agregarEjercicio(ejercicio, (DiaSemana) cboDia.getSelectedItem(), (Integer) spSeries.getValue(),
                    (Integer) spReps.getValue(), (Double) spPeso.getValue(), (Integer) spDescanso.getValue());
        }
        catch (RutinaLlenaException | DatosInvalidosException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Agregar", JOptionPane.ERROR_MESSAGE);
        }
        mostrarRutina();
    }

    private void quitarEjercicio()
    {
        Rutina r = rutinaActual();
        int fila = tablaRutina.getSelectedRow();
        if (r != null && fila >= 0)
        {
            r.eliminarDetalle(fila);
            mostrarRutina();
        }
    }

    private void ordenarPorDia()
    {
        Rutina r = rutinaActual();
        if (r != null)
        {
            r.ordenarPorDia();
            mostrarRutina();
        }
    }

    /** Tabla de la rutina: día, ejercicio, series × repeticiones, peso y descanso. */
    private static final class RutinaTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;
        private static final String[] COLUMNAS = {"Día", "Ejercicio", "Series x reps", "Peso", "Descanso"};
        private transient DetalleRutina[] detalles = new DetalleRutina[0];

        void setRutina(Rutina r)
        {
            detalles = (r == null) ? new DetalleRutina[0] : r.getDetalles();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount()
        {
            return detalles.length;
        }

        @Override
        public int getColumnCount()
        {
            return COLUMNAS.length;
        }

        @Override
        public String getColumnName(int c)
        {
            return COLUMNAS[c];
        }

        @Override
        public Object getValueAt(int fila, int col)
        {
            DetalleRutina d = detalles[fila];
            switch (col)
            {
                case 0:
                    return PanelEntrenadores.nombreLegible(d.getDia().name());
                case 1:
                    return d.getEjercicio().getNombre();
                case 2:
                    return d.getSeries() + " x " + d.getRepeticiones();
                case 3:
                    return String.format(Locale.US, "%.1f kg", d.getPeso());
                default:
                    return d.getDescansoSeg() + " s";
            }
        }
    }
}
