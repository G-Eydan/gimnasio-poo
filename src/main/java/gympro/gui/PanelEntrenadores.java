package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.EntrenadorSinCupoException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.enums.DiaSemana;
import gympro.modelo.enums.Especialidad;
import gympro.modelo.enums.Turno;
import gympro.modelo.persona.Entrenador;
import gympro.modelo.persona.Miembro;
import gympro.servicio.Gimnasio;
import gympro.util.Validador;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pestaña «Entrenadores» (HU-06 · T2): formulario para registrar entrenadores.
 *
 * <ul>
 *   <li>Datos personales y sueldo base en {@link JTextField}; turno en un {@link JComboBox}.</li>
 *   <li>Un {@link JCheckBox} por cada {@link Especialidad}, creados con un bucle.</li>
 *   <li>Grilla 7 × 3 de disponibilidad: un arreglo 2D de {@link JCheckBox} que se copia al
 *       arreglo 2D {@code boolean} del entrenador.</li>
 *   <li>Tabla de entrenadores con un {@link JComboBox} «Filtrar por especialidad»
 *       ({@code buscarEntrenadoresPorEspecialidad}). Al seleccionar uno, el formulario,
 *       los checkbox y la grilla muestran sus datos (modo consulta).</li>
 *   <li>HU-07: {@link JList} con los miembros asignados al entrenador seleccionado, contador
 *       «n / 15» y botón «Asignar miembro» ({@code gimnasio.asignarEntrenador}).</li>
 * </ul>
 *
 * @author I3
 */
public final class PanelEntrenadores extends JPanel implements Actualizable
{
    private static final long serialVersionUID = 1L;

    private final transient Gimnasio gimnasio;

    private final JTextField txtDni = new JTextField(10);
    private final JTextField txtNombres = new JTextField(12);
    private final JTextField txtApellidos = new JTextField(12);
    private final JTextField txtTelefono = new JTextField(10);
    private final JTextField txtEmail = new JTextField(12);
    private final JTextField txtSueldo = new JTextField(8);
    private final JComboBox<Turno> cboTurno = new JComboBox<>(Turno.values());
    private final JCheckBox[] chkEspecialidades = new JCheckBox[Especialidad.values().length];
    private final JCheckBox[][] celdas = new JCheckBox[DiaSemana.values().length][Turno.values().length];
    private final JButton btnRegistrar = new JButton("Registrar entrenador");

    private static final String TODAS = "Todas";
    private final EntrenadorTableModel modeloTabla = new EntrenadorTableModel();
    private final JTable tabla = new JTable(modeloTabla);
    private final JComboBox<Object> cboFiltro = new JComboBox<>();

    private final DefaultListModel<Miembro> modeloAsignados = new DefaultListModel<>();
    private final JList<Miembro> lstAsignados = new JList<>(modeloAsignados);
    private final JLabel lblCupo = new JLabel();
    private final JButton btnAsignar = new JButton("Asignar miembro");

    public PanelEntrenadores(Gimnasio gimnasio)
    {
        super(new BorderLayout(10, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(crearFormulario(), BorderLayout.WEST);
        add(crearListado(), BorderLayout.CENTER);
        actualizar();
    }

    /** Recarga la tabla con el filtro actual, conservando la selección si sigue visible. */
    @Override
    public void actualizar()
    {
        Entrenador antes = seleccionado();
        Object filtro = cboFiltro.getSelectedItem();
        modeloTabla.setFilas(filtro instanceof Especialidad
                ? gimnasio.buscarEntrenadoresPorEspecialidad((Especialidad) filtro)
                : gimnasio.listarEntrenadores());
        if (antes != null)
        {
            int fila = modeloTabla.indiceDe(antes);
            if (fila >= 0)
            {
                tabla.setRowSelectionInterval(fila, fila);
            }
        }
        mostrarAsignados();
    }

    // ------------------------------------------------------------------
    // Listado y modo consulta
    // ------------------------------------------------------------------

    private JPanel crearListado()
    {
        cboFiltro.addItem(TODAS);
        for (Especialidad e : Especialidad.values())
        {
            cboFiltro.addItem(e);
        }
        cboFiltro.addActionListener(e -> actualizar());

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.getSelectionModel().addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting())
            {
                Entrenador sel = seleccionado();
                if (sel != null)
                {
                    mostrar(sel);
                }
                mostrarAsignados();
            }
        });

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtro.add(new JLabel("Filtrar por especialidad:"));
        filtro.add(cboFiltro);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Entrenadores"));
        panel.add(filtro, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(crearAsignados(), BorderLayout.SOUTH);
        return panel;
    }

    // ------------------------------------------------------------------
    // Miembros asignados (HU-07)
    // ------------------------------------------------------------------

    private JPanel crearAsignados()
    {
        btnAsignar.addActionListener(e -> asignarMiembro());
        lstAsignados.setVisibleRowCount(6);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(lblCupo, BorderLayout.WEST);
        arriba.add(btnAsignar, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Miembros asignados"));
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(lstAsignados), BorderLayout.CENTER);
        return panel;
    }

    /** Muestra los miembros del entrenador seleccionado y el contador «n / 15». */
    private void mostrarAsignados()
    {
        modeloAsignados.clear();
        Entrenador e = seleccionado();
        btnAsignar.setEnabled(e != null);
        if (e == null)
        {
            lblCupo.setText("Seleccione un entrenador.");
            return;
        }
        for (Miembro m : e.getMiembrosAsignados())
        {
            modeloAsignados.addElement(m);
        }
        lblCupo.setText(e.getCodigo() + " · " + e.getMiembrosAsignados().size() + " / " + Entrenador.MAX_MIEMBROS);
    }

    /**
     * Pide un miembro con membresía activa (en un {@link JComboBox} dentro de un
     * {@link JOptionPane}) y lo asigna al entrenador seleccionado. Si no hay cupo,
     * muestra el error de {@link EntrenadorSinCupoException}.
     */
    private void asignarMiembro()
    {
        Entrenador e = seleccionado();
        if (e == null)
        {
            return;
        }
        List<Miembro> activos = gimnasio.listarMiembrosActivos();
        if (activos.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No hay miembros con membresía activa.", "Asignar miembro",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JComboBox<Miembro> cboMiembros = new JComboBox<>(activos.toArray(new Miembro[0]));
        int opcion = JOptionPane.showConfirmDialog(this, cboMiembros, "Asignar miembro a " + e.getNombreCompleto(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION || cboMiembros.getSelectedItem() == null)
        {
            return;
        }
        Miembro m = (Miembro) cboMiembros.getSelectedItem();
        try
        {
            gimnasio.asignarEntrenador(m.getDni(), e.getCodigo());
        }
        catch (EntrenadorSinCupoException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Sin cupo", JOptionPane.ERROR_MESSAGE);
        }
        catch (MiembroNoEncontradoException | DatosInvalidosException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Asignar miembro", JOptionPane.ERROR_MESSAGE);
        }
        actualizar();
    }

    private Entrenador seleccionado()
    {
        int fila = tabla.getSelectedRow();
        return (fila < 0) ? null : modeloTabla.getEntrenador(fila);
    }

    /** Modo consulta: muestra los datos del entrenador y bloquea el formulario. */
    private void mostrar(Entrenador e)
    {
        txtDni.setText(e.getDni());
        txtNombres.setText(e.getNombres());
        txtApellidos.setText(e.getApellidos());
        txtTelefono.setText(e.getTelefono() == null ? "" : e.getTelefono());
        txtEmail.setText(e.getEmail() == null ? "" : e.getEmail());
        txtSueldo.setText(String.format(Locale.US, "%.2f", e.getSueldoBase()));
        cboTurno.setSelectedItem(e.getTurno());
        for (Especialidad esp : Especialidad.values())
        {
            chkEspecialidades[esp.ordinal()].setSelected(e.tieneEspecialidad(esp));
        }
        boolean[][] disponibilidad = e.getDisponibilidad();
        for (DiaSemana d : DiaSemana.values())
        {
            for (Turno t : Turno.values())
            {
                celdas[d.ordinal()][t.ordinal()].setSelected(disponibilidad[d.ordinal()][t.ordinal()]);
            }
        }
        setEditable(false);
    }

    /** En modo consulta los campos no se pueden cambiar; «Limpiar» vuelve al modo registro. */
    private void setEditable(boolean editable)
    {
        for (JTextField campo : new JTextField[]{txtDni, txtNombres, txtApellidos, txtTelefono, txtEmail, txtSueldo})
        {
            campo.setEditable(editable);
        }
        cboTurno.setEnabled(editable);
        for (JCheckBox chk : chkEspecialidades)
        {
            chk.setEnabled(editable);
        }
        for (JCheckBox[] fila : celdas)
        {
            for (JCheckBox celda : fila)
            {
                celda.setEnabled(editable);
            }
        }
        btnRegistrar.setEnabled(editable);
    }

    // ------------------------------------------------------------------
    // Formulario
    // ------------------------------------------------------------------

    private JPanel crearFormulario()
    {
        JPanel datos = new JPanel(new GridLayout(0, 2, 6, 4));
        agregarCampo(datos, "DNI:", txtDni);
        agregarCampo(datos, "Nombres:", txtNombres);
        agregarCampo(datos, "Apellidos:", txtApellidos);
        agregarCampo(datos, "Teléfono (opcional):", txtTelefono);
        agregarCampo(datos, "Email (opcional):", txtEmail);
        agregarCampo(datos, "Sueldo base S/:", txtSueldo);
        agregarCampo(datos, "Turno:", cboTurno);

        // Un checkbox por especialidad, con un bucle (no 8 a mano)
        JPanel especialidades = new JPanel(new GridLayout(0, 4, 4, 2));
        especialidades.setBorder(BorderFactory.createTitledBorder("Especialidades"));
        for (Especialidad e : Especialidad.values())
        {
            chkEspecialidades[e.ordinal()] = new JCheckBox(nombreLegible(e.name()));
            especialidades.add(chkEspecialidades[e.ordinal()]);
        }

        btnRegistrar.addActionListener(e -> registrar());
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnRegistrar);
        botones.add(btnLimpiar);

        JPanel arriba = new JPanel(new BorderLayout(0, 6));
        arriba.add(datos, BorderLayout.NORTH);
        arriba.add(especialidades, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del entrenador"));
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(crearGrillaDisponibilidad(), BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    /** Grilla 7 × 3: una fila por día y una columna por turno, más los encabezados. */
    private JPanel crearGrillaDisponibilidad()
    {
        int filas = DiaSemana.values().length + 1;
        int columnas = Turno.values().length + 1;
        JPanel grilla = new JPanel(new GridLayout(filas, columnas, 4, 2));
        grilla.setBorder(BorderFactory.createTitledBorder("Disponibilidad semanal"));
        grilla.add(new JLabel(""));
        for (Turno t : Turno.values())
        {
            grilla.add(new JLabel(nombreLegible(t.name()), SwingConstants.CENTER));
        }
        for (DiaSemana d : DiaSemana.values())
        {
            grilla.add(new JLabel(nombreLegible(d.name())));
            for (Turno t : Turno.values())
            {
                JCheckBox celda = new JCheckBox();
                celda.setHorizontalAlignment(SwingConstants.CENTER);
                celdas[d.ordinal()][t.ordinal()] = celda;
                grilla.add(celda);
            }
        }
        return grilla;
    }

    private static void agregarCampo(JPanel panel, String etiqueta, java.awt.Component campo)
    {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    /** «MIERCOLES» → «Miercoles», «MANANA» → «Manana». */
    static String nombreLegible(String constante)
    {
        String texto = constante.replace('_', ' ').toLowerCase();
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    /** Valida los datos, crea el entrenador, copia especialidades y disponibilidad, y lo registra. */
    private void registrar()
    {
        try
        {
            String dni = txtDni.getText().trim();
            Validador.validarDni(dni);
            // Se revisa antes de crear el objeto para no gastar un código E00n con un DNI repetido.
            for (Entrenador otro : gimnasio.listarEntrenadores())
            {
                if (otro.getDni().equals(dni))
                {
                    throw new DatosInvalidosException("Ya existe un entrenador con DNI " + dni + ".");
                }
            }
            String telefono = opcional(txtTelefono);
            String email = opcional(txtEmail);
            double sueldo;
            try
            {
                sueldo = Double.parseDouble(txtSueldo.getText().trim().replace(',', '.'));
            }
            catch (NumberFormatException e)
            {
                throw new DatosInvalidosException("El sueldo base debe ser un número.");
            }
            List<Especialidad> elegidas = new ArrayList<>();
            for (Especialidad e : Especialidad.values())
            {
                if (chkEspecialidades[e.ordinal()].isSelected())
                {
                    elegidas.add(e);
                }
            }
            if (elegidas.isEmpty())
            {
                throw new DatosInvalidosException("Marque al menos una especialidad.");
            }

            Entrenador entrenador = new Entrenador(dni, txtNombres.getText().trim(), txtApellidos.getText().trim(),
                    null, telefono, email, sueldo, (Turno) cboTurno.getSelectedItem());
            for (Especialidad e : elegidas)
            {
                entrenador.agregarEspecialidad(e);
            }
            // Copiar la grilla al arreglo 2D del entrenador
            for (DiaSemana d : DiaSemana.values())
            {
                for (Turno t : Turno.values())
                {
                    entrenador.setDisponible(d, t, celdas[d.ordinal()][t.ordinal()].isSelected());
                }
            }
            gimnasio.registrarEntrenador(entrenador);
            JOptionPane.showMessageDialog(this, "Entrenador " + entrenador.getCodigo() + " registrado.",
                    "Entrenadores", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            actualizar();
        }
        catch (DatosInvalidosException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar()
    {
        tabla.clearSelection();
        setEditable(true);
        for (JTextField campo : new JTextField[]{txtDni, txtNombres, txtApellidos, txtTelefono, txtEmail, txtSueldo})
        {
            campo.setText("");
        }
        cboTurno.setSelectedIndex(0);
        for (JCheckBox chk : chkEspecialidades)
        {
            chk.setSelected(false);
        }
        for (JCheckBox[] fila : celdas)
        {
            for (JCheckBox celda : fila)
            {
                celda.setSelected(false);
            }
        }
    }

    /** @return el texto sin espacios, o {@code null} si está vacío (el campo es opcional) */
    private static String opcional(JTextField campo)
    {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }
    /** Tabla de entrenadores: código, nombre, especialidades, turno, asignados «n/15» y sueldo. */
    private static final class EntrenadorTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;
        private static final String[] COLUMNAS = {"Código", "Nombre", "Especialidades", "Turno", "Asignados", "Sueldo"};
        private transient List<Entrenador> filas = new ArrayList<>();

        void setFilas(List<Entrenador> lista)
        {
            filas = new ArrayList<>(lista);
            fireTableDataChanged();
        }

        Entrenador getEntrenador(int fila)
        {
            return filas.get(fila);
        }

        int indiceDe(Entrenador e)
        {
            return filas.indexOf(e);
        }

        @Override
        public int getRowCount()
        {
            return filas.size();
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
            Entrenador e = filas.get(fila);
            switch (col)
            {
                case 0:
                    return e.getCodigo();
                case 1:
                    return e.getNombreCompleto();
                case 2:
                    StringBuilder sb = new StringBuilder();
                    for (Especialidad esp : e.getEspecialidades())
                    {
                        sb.append(sb.length() == 0 ? "" : ", ").append(nombreLegible(esp.name()));
                    }
                    return sb.toString();
                case 3:
                    return nombreLegible(e.getTurno().name());
                case 4:
                    return e.getMiembrosAsignados().size() + "/" + Entrenador.MAX_MIEMBROS;
                default:
                    return String.format(Locale.US, "S/ %.2f", e.calcularSueldo());
            }
        }
    }
}
