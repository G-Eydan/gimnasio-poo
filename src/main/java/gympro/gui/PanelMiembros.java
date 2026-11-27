package gympro.gui;

import gympro.modelo.enums.EstadoInscripcion;
import gympro.modelo.membresia.Membresia;
import gympro.modelo.persona.Miembro;
import gympro.modelo.persona.MiembroCorporativo;
import gympro.modelo.persona.MiembroEstudiante;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pestaña «Miembros» (HG-01 · T2).
 *
 * <ul>
 *   <li><b>NORTH</b>: búsqueda en vivo por DNI o nombre ({@link DocumentListener}) y
 *       filtro por estado ({@link JComboBox}); los dos se combinan con
 *       {@link RowFilter#andFilter}.</li>
 *   <li><b>CENTER</b>: {@link JTable} con {@link MiembroTableModel} y un
 *       {@link TableRowSorter} (clic en el encabezado para ordenar).</li>
 *   <li><b>SOUTH</b>: Nuevo, Editar, Dar de baja y Ver ficha; doble clic en una fila
 *       también abre la ficha ({@link MouseAdapter}).</li>
 * </ul>
 *
 * <p>Las filas seleccionadas se convierten siempre con
 * {@code convertRowIndexToModel}: con la tabla filtrada u ordenada, la fila de la vista
 * no es la del modelo.</p>
 *
 * @author I2
 */
public final class PanelMiembros extends JPanel implements Actualizable
{
    private static final long serialVersionUID = 1L;
    private static final String TODOS = "Todos";
    private static final DateTimeFormatter FORMATO_FECHA = MiembroTableModel.FORMATO_FECHA;

    private final transient Gimnasio gimnasio;
    private final MiembroTableModel modelo = new MiembroTableModel();
    private final JTable tabla = new JTable(modelo);
    private final transient TableRowSorter<MiembroTableModel> sorter = new TableRowSorter<>(modelo);
    private final JTextField txtBuscar = new JTextField(20);
    private final JComboBox<Object> cboEstado = new JComboBox<>();
    private final JLabel lblTotal = new JLabel();

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnBaja = new JButton("Dar de baja");
    private final JButton btnFicha = new JButton("Ver ficha");

    public PanelMiembros(Gimnasio gimnasio)
    {
        super(new BorderLayout(0, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        configurarTabla();
        add(crearBarraBusqueda(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);

        actualizar();
    }

    /** Recarga la lista desde {@code Gimnasio} manteniendo el filtro y, si sigue, la selección. */
    @Override
    public void actualizar()
    {
        Miembro antes = seleccionado();
        modelo.setMiembros(gimnasio.listarMiembros());
        if (antes != null)
        {
            seleccionar(antes.getDni());
        }
        actualizarTotal();
        actualizarBotones();
    }

    // ------------------------------------------------------------------
    // Construcción
    // ------------------------------------------------------------------

    private void configurarTabla()
    {
        tabla.setRowSorter(sorter);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);

        // «Vence» y «Días» son texto; se ordenan por su valor real y «-» va al final.
        sorter.setComparator(MiembroTableModel.COL_VENCE, Comparator.comparing(PanelMiembros::fechaParaOrdenar));
        sorter.setComparator(MiembroTableModel.COL_DIAS, Comparator.comparingLong(PanelMiembros::diasParaOrdenar));

        int[] anchos = {80, 200, 100, 140, 85, 50, 100, 140};
        TableColumnModel columnas = tabla.getColumnModel();
        for (int i = 0; i < anchos.length; i++)
        {
            columnas.getColumn(i).setPreferredWidth(anchos[i]);
        }

        tabla.getSelectionModel().addListSelectionListener(e -> actualizarBotones());
        tabla.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0)
                {
                    verFicha();
                }
            }
        });
    }

    private JPanel crearBarraBusqueda()
    {
        cboEstado.addItem(TODOS);
        for (EstadoInscripcion estado : EstadoInscripcion.values())
        {
            cboEstado.addItem(estado);
        }
        cboEstado.addActionListener(e -> filtrar());

        txtBuscar.setToolTipText("Escriba parte del DNI o del nombre");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                filtrar();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtros.add(new JLabel("Buscar (DNI o nombre):"));
        filtros.add(txtBuscar);
        filtros.add(new JLabel("   Estado:"));
        filtros.add(cboEstado);
        panel.add(filtros, BorderLayout.WEST);
        panel.add(lblTotal, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearBotones()
    {
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnFicha.addActionListener(e -> verFicha());
        // Dar de baja necesita Gimnasio.darDeBajaMiembro y Miembro.isActivo (HG-01 · T1, #31).
        // Mientras no estén fusionados, el botón queda desactivado y lo explica.
        btnBaja.setToolTipText("Disponible cuando se fusione HG-01 · T1 (#31): baja lógica del miembro");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnBaja);
        panel.add(btnFicha);
        return panel;
    }

    // ------------------------------------------------------------------
    // Búsqueda y selección
    // ------------------------------------------------------------------

    /** Combina la búsqueda de texto (DNI o nombre, sin distinguir mayúsculas) con el filtro de estado. */
    private void filtrar()
    {
        List<RowFilter<MiembroTableModel, Integer>> filtros = new ArrayList<>();

        String texto = txtBuscar.getText().trim();
        if (!texto.isEmpty())
        {
            filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(texto),
                    MiembroTableModel.COL_DNI, MiembroTableModel.COL_NOMBRE));
        }

        Object estado = cboEstado.getSelectedItem();
        if (estado instanceof EstadoInscripcion)
        {
            filtros.add(new RowFilter<MiembroTableModel, Integer>()
            {
                @Override
                public boolean include(Entry<? extends MiembroTableModel, ? extends Integer> fila)
                {
                    return fila.getValue(MiembroTableModel.COL_ESTADO) == estado;
                }
            });
        }

        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
        actualizarTotal();
        actualizarBotones();
    }

    /** @return el miembro de la fila seleccionada (convertida al índice del modelo), o {@code null} */
    private Miembro seleccionado()
    {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0)
        {
            return null;
        }
        return modelo.getMiembro(tabla.convertRowIndexToModel(filaVista));
    }

    /** Selecciona y muestra la fila del miembro con ese DNI, si está visible con el filtro actual. */
    private void seleccionar(String dni)
    {
        int filaModelo = modelo.indiceDe(dni);
        if (filaModelo < 0)
        {
            return;
        }
        int filaVista = tabla.convertRowIndexToView(filaModelo);
        if (filaVista >= 0)
        {
            tabla.setRowSelectionInterval(filaVista, filaVista);
            tabla.scrollRectToVisible(tabla.getCellRect(filaVista, 0, true));
        }
    }

    private void actualizarTotal()
    {
        int visibles = tabla.getRowCount();
        int total = modelo.getRowCount();
        lblTotal.setText(visibles == total ? total + " miembros" : visibles + " de " + total + " miembros");
    }

    private void actualizarBotones()
    {
        boolean haySeleccion = seleccionado() != null;
        btnEditar.setEnabled(haySeleccion);
        btnFicha.setEnabled(haySeleccion);
        btnBaja.setEnabled(false);
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    private void nuevo()
    {
        DialogoMiembro dialogo = new DialogoMiembro(SwingUtilities.getWindowAncestor(this), gimnasio, null);
        dialogo.setVisible(true);
        Miembro nuevo = dialogo.getMiembroGuardado();
        actualizar();
        if (nuevo != null)
        {
            seleccionar(nuevo.getDni());
            if (seleccionado() == null)
            {
                JOptionPane.showMessageDialog(this, "Miembro " + nuevo.getCodigo()
                        + " registrado. No aparece en la tabla por el filtro actual.",
                        "Miembro registrado", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void editar()
    {
        Miembro m = seleccionado();
        if (m == null)
        {
            return;
        }
        new DialogoMiembro(SwingUtilities.getWindowAncestor(this), gimnasio, m).setVisible(true);
        actualizar();
    }

    private void verFicha()
    {
        Miembro m = seleccionado();
        if (m == null)
        {
            return;
        }
        JTextArea area = new JTextArea(ficha(m));
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setRows(Math.min(20, area.getLineCount()));
        area.setColumns(60);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Ficha de " + m.getNombreCompleto(),
                JOptionPane.PLAIN_MESSAGE);
    }

    /** Texto de la ficha: datos personales, tipo, estado e historial de membresías. */
    static String ficha(Miembro m)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Código      : %s%n", m.getCodigo()));
        sb.append(String.format("Nombre      : %s%n", m.getNombreCompleto()));
        sb.append(String.format("DNI         : %s%n", m.getDni()));
        sb.append(String.format("Tipo        : %s (descuento %d %%)%n", m.getTipo(),
                Math.round(m.getPorcentajeDescuento() * 100)));
        if (m instanceof MiembroEstudiante)
        {
            MiembroEstudiante e = (MiembroEstudiante) m;
            sb.append(String.format("Universidad : %s (código %s)%n", e.getUniversidad(), e.getCodigoUniversitario()));
        }
        else if (m instanceof MiembroCorporativo)
        {
            MiembroCorporativo c = (MiembroCorporativo) m;
            sb.append(String.format("Empresa     : %s (RUC %s)%n", c.getEmpresa(), c.getRuc()));
        }
        if (m.getFechaNacimiento() != null)
        {
            sb.append(String.format("Nacimiento  : %s (%d años)%n", m.getFechaNacimiento().format(FORMATO_FECHA), m.getEdad()));
        }
        sb.append(String.format("Teléfono    : %s%n", valor(m.getTelefono())));
        sb.append(String.format("Email       : %s%n", valor(m.getEmail())));
        sb.append(String.format("Estado      : %s%n", m.getEstadoInscripcion()));

        Membresia vigente = m.getMembresiaVigente();
        if (vigente != null)
        {
            sb.append(String.format("Vence       : %s (%d días)%n", vigente.getFechaFin().format(FORMATO_FECHA),
                    m.getDiasRestantes()));
        }
        else
        {
            // Decisión D2: si solo tiene una membresía que empieza más adelante, se indica cuándo.
            Membresia proxima = proximaMembresia(m);
            if (proxima != null)
            {
                sb.append(String.format("Inicia el   : %s%n", proxima.getFechaInicio().format(FORMATO_FECHA)));
            }
        }

        sb.append(String.format("%nHistorial de membresías:%n"));
        List<Membresia> historial = m.getHistorialOrdenado();
        if (historial.isEmpty())
        {
            sb.append("  (sin membresías)");
        }
        for (Membresia mem : historial)
        {
            sb.append(String.format("  %-6s %-20s %s - %s  %s%n", mem.getCodigo(), mem.getPlan().getNombre(),
                    mem.getFechaInicio().format(FORMATO_FECHA), mem.getFechaFin().format(FORMATO_FECHA),
                    mem.getEstado()));
        }
        return sb.toString();
    }

    /** @return la primera membresía que empieza después de hoy, o {@code null} */
    private static Membresia proximaMembresia(Miembro m)
    {
        LocalDate hoy = LocalDate.now();
        for (Membresia mem : m.getHistorialOrdenado())
        {
            if (mem.getFechaInicio().isAfter(hoy))
            {
                return mem;
            }
        }
        return null;
    }

    private static String valor(String texto)
    {
        return (texto == null || texto.isEmpty()) ? MiembroTableModel.SIN_DATO : texto;
    }

    private static LocalDate fechaParaOrdenar(Object valor)
    {
        String texto = String.valueOf(valor);
        return MiembroTableModel.SIN_DATO.equals(texto) ? LocalDate.MAX : LocalDate.parse(texto, FORMATO_FECHA);
    }

    private static long diasParaOrdenar(Object valor)
    {
        String texto = String.valueOf(valor);
        return MiembroTableModel.SIN_DATO.equals(texto) ? Long.MAX_VALUE : Long.parseLong(texto);
    }
}
