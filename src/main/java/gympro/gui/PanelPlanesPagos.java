package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MembresiaActivaException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.enums.MetodoPago;
import gympro.modelo.membresia.Pago;
import gympro.modelo.membresia.Plan;
import gympro.modelo.membresia.PlanAnual;
import gympro.modelo.membresia.PlanMensual;
import gympro.modelo.membresia.PlanTrimestral;
import gympro.modelo.persona.Miembro;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pestaña «Planes y Pagos» (HG-01 · T5).
 *
 * <ul>
 *   <li><b>NORTH</b>: tabla de planes y formulario para crear uno ({@code gimnasio.crearPlan}).</li>
 *   <li><b>CENTER</b>: contratar o renovar: DNI con Buscar, {@code JComboBox<Plan>} con los
 *       planes activos y un {@link ButtonGroup} de {@link JRadioButton} (uno por
 *       {@link MetodoPago}). Un {@link ItemListener} recalcula el desglose en vivo; al
 *       registrar se muestra el comprobante en un {@link JTextArea} monoespaciado.</li>
 *   <li><b>SOUTH</b>: historial de pagos ({@code gimnasio.getPagos()}).</li>
 * </ul>
 *
 * @author I1
 */
public final class PanelPlanesPagos extends JPanel implements Actualizable
{
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String MENSUAL = "MENSUAL";
    private static final String TRIMESTRAL = "TRIMESTRAL";
    private static final String ANUAL = "ANUAL";

    private final transient Gimnasio gimnasio;

    private final PlanTableModel modeloPlanes = new PlanTableModel();
    private final PagoTableModel modeloPagos = new PagoTableModel();

    private final JComboBox<String> cboTipoPlan = new JComboBox<>(new String[]{MENSUAL, TRIMESTRAL, ANUAL});
    private final JTextField txtCodigoPlan = new JTextField(5);
    private final JTextField txtNombrePlan = new JTextField(12);
    private final JTextField txtPrecioMensual = new JTextField(6);

    // Contratar / renovar
    private transient Miembro miembro;
    private final JTextField txtDni = new JTextField(9);
    private final JLabel lblMiembro = new JLabel("Busque un miembro por DNI.");
    private final JComboBox<Plan> cboPlan = new JComboBox<>();
    private final JRadioButton[] radiosMetodo = new JRadioButton[MetodoPago.values().length];
    private final JLabel lblImporte = new JLabel();
    private final JLabel lblDescuento = new JLabel();
    private final JLabel lblMatricula = new JLabel();
    private final JLabel lblRecargo = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private final JTextArea txtComprobante = new JTextArea(14, 52);

    public PanelPlanesPagos(Gimnasio gimnasio)
    {
        super(new BorderLayout(0, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(crearSeccionPlanes(), BorderLayout.NORTH);
        add(crearSeccionContratar(), BorderLayout.CENTER);
        add(crearSeccionPagos(), BorderLayout.SOUTH);

        actualizar();
    }

    /** Recarga planes, el combo de planes activos, el miembro buscado y los pagos. */
    @Override
    public void actualizar()
    {
        modeloPlanes.setFilas(gimnasio.listarPlanes());
        modeloPagos.setFilas(gimnasio.getPagos());

        Object antes = cboPlan.getSelectedItem();
        cboPlan.removeAllItems();
        for (Plan p : gimnasio.listarPlanes())
        {
            if (p.isActivo())
            {
                cboPlan.addItem(p);
            }
        }
        if (antes != null)
        {
            cboPlan.setSelectedItem(antes);
        }
        mostrarMiembro();
        actualizarDesglose();
    }

    // ------------------------------------------------------------------
    // Contratar / renovar
    // ------------------------------------------------------------------

    private JPanel crearSeccionContratar()
    {
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarMiembro());
        txtDni.addActionListener(e -> buscarMiembro());

        JPanel filaMiembro = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filaMiembro.add(new JLabel("DNI:"));
        filaMiembro.add(txtDni);
        filaMiembro.add(btnBuscar);

        // El total se recalcula en vivo al cambiar de plan o de método de pago.
        ItemListener recalcular = e ->
        {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                actualizarDesglose();
            }
        };
        cboPlan.addItemListener(recalcular);

        ButtonGroup grupo = new ButtonGroup();
        JPanel filaMetodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        for (MetodoPago metodo : MetodoPago.values())
        {
            String texto = metodo.name() + (metodo.getRecargo() > 0
                    ? String.format(Locale.US, " (+%.0f %%)", metodo.getRecargo() * 100) : "");
            JRadioButton radio = new JRadioButton(texto);
            radio.addItemListener(recalcular);
            radiosMetodo[metodo.ordinal()] = radio;
            grupo.add(radio);
            filaMetodo.add(radio);
        }
        radiosMetodo[MetodoPago.EFECTIVO.ordinal()].setSelected(true);

        JPanel desglose = new JPanel(new GridLayout(0, 2, 8, 2));
        desglose.setBorder(BorderFactory.createTitledBorder("Desglose"));
        agregarFila(desglose, "Importe del plan:", lblImporte);
        agregarFila(desglose, "Descuento del miembro:", lblDescuento);
        agregarFila(desglose, "Matrícula:", lblMatricula);
        agregarFila(desglose, "Recargo:", lblRecargo);
        agregarFila(desglose, "TOTAL:", lblTotal);
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnRegistrar = new JButton("Registrar pago");
        btnRegistrar.addActionListener(e -> registrarPago());

        JPanel formulario = new JPanel(new GridLayout(0, 1, 0, 4));
        formulario.add(filaMiembro);
        formulario.add(lblMiembro);
        JPanel filaPlan = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filaPlan.add(new JLabel("Plan:"));
        filaPlan.add(cboPlan);
        formulario.add(filaPlan);
        formulario.add(filaMetodo);

        JPanel izquierda = new JPanel(new BorderLayout(0, 6));
        izquierda.add(formulario, BorderLayout.NORTH);
        izquierda.add(desglose, BorderLayout.CENTER);
        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaBoton.add(btnRegistrar);
        izquierda.add(filaBoton, BorderLayout.SOUTH);

        txtComprobante.setEditable(false);
        txtComprobante.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollComprobante = new JScrollPane(txtComprobante);
        scrollComprobante.setBorder(BorderFactory.createTitledBorder("Comprobante"));

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Contratar / renovar"));
        panel.add(izquierda, BorderLayout.CENTER);
        panel.add(scrollComprobante, BorderLayout.EAST);
        return panel;
    }

    private static void agregarFila(JPanel panel, String etiqueta, JLabel valor)
    {
        panel.add(new JLabel(etiqueta));
        panel.add(valor);
    }

    private void buscarMiembro()
    {
        try
        {
            miembro = gimnasio.buscarMiembro(txtDni.getText().trim());
        }
        catch (MiembroNoEncontradoException e)
        {
            miembro = null;
            JOptionPane.showMessageDialog(this, e.getMessage(), "Buscar miembro", JOptionPane.WARNING_MESSAGE);
        }
        mostrarMiembro();
        actualizarDesglose();
    }

    private void mostrarMiembro()
    {
        if (miembro == null)
        {
            lblMiembro.setText("Busque un miembro por DNI.");
            return;
        }
        lblMiembro.setText(String.format("%s · %s (descuento %d %%) · %s%s", miembro.getNombreCompleto(),
                miembro.getTipo(), Math.round(miembro.getPorcentajeDescuento() * 100), miembro.getEstadoInscripcion(),
                miembro.isActivo() ? "" : " · DADO DE BAJA"));
    }

    private MetodoPago metodoSeleccionado()
    {
        for (MetodoPago metodo : MetodoPago.values())
        {
            if (radiosMetodo[metodo.ordinal()].isSelected())
            {
                return metodo;
            }
        }
        return null;
    }

    /**
     * Recalcula el desglose sin registrar nada. El total sale de {@code gimnasio.calcularImporte}
     * (la misma regla que el pago real); las líneas de detalle siguen RN-02 a RN-05.
     */
    private void actualizarDesglose()
    {
        Plan plan = (Plan) cboPlan.getSelectedItem();
        MetodoPago metodo = metodoSeleccionado();
        if (miembro == null || plan == null || metodo == null)
        {
            for (JLabel l : new JLabel[]{lblImporte, lblDescuento, lblMatricula, lblRecargo, lblTotal})
            {
                l.setText("S/ -");
            }
            return;
        }
        double importe = plan.calcularImporte();
        double descuento = redondear(importe * miembro.getPorcentajeDescuento());
        double matricula = miembro.esPrimeraMembresia() ? Pago.MATRICULA : 0.0;
        double recargo = redondear((importe - descuento + matricula) * metodo.getRecargo());

        lblImporte.setText(soles(importe));
        lblDescuento.setText(String.format(Locale.US, "- %s  (%d %%)", soles(descuento),
                Math.round(miembro.getPorcentajeDescuento() * 100)));
        lblMatricula.setText(soles(matricula) + (matricula > 0 ? "  (primera membresía)" : ""));
        lblRecargo.setText(soles(recargo));
        lblTotal.setText(soles(gimnasio.calcularImporte(miembro, plan, metodo)));
    }

    /**
     * Contrata; si el miembro ya tiene una membresía vigente, ofrece renovarla.
     * Muestra el comprobante del pago registrado.
     */
    private void registrarPago()
    {
        Plan plan = (Plan) cboPlan.getSelectedItem();
        MetodoPago metodo = metodoSeleccionado();
        if (miembro == null || plan == null || metodo == null)
        {
            JOptionPane.showMessageDialog(this, "Busque un miembro y elija el plan y el método de pago.",
                    "Registrar pago", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try
        {
            Pago pago;
            try
            {
                pago = gimnasio.contratarMembresia(miembro.getDni(), plan.getCodigo(), metodo);
            }
            catch (MembresiaActivaException e)
            {
                int opcion = JOptionPane.showConfirmDialog(this, e.getMessage() + "\n¿Desea renovarla?",
                        "Membresía vigente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opcion != JOptionPane.YES_OPTION)
                {
                    return;
                }
                pago = gimnasio.renovarMembresia(miembro.getDni(), plan.getCodigo(), metodo);
            }
            txtComprobante.setText(pago.generarComprobante());
            txtComprobante.setCaretPosition(0);
            actualizar();
        }
        catch (MembresiaActivaException | MiembroNoEncontradoException | DatosInvalidosException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Registrar pago", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static double redondear(double valor)
    {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static String soles(double monto)
    {
        return String.format(Locale.US, "S/ %.2f", monto);
    }

    // ------------------------------------------------------------------
    // Planes
    // ------------------------------------------------------------------

    private JPanel crearSeccionPlanes()
    {
        JTable tabla = new JTable(modeloPlanes);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(600, 110));

        JButton btnCrear = new JButton("Crear plan");
        btnCrear.addActionListener(e -> crearPlan());
        txtPrecioMensual.setToolTipText("Precio mensual base, por ejemplo 120");

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cboTipoPlan);
        formulario.add(new JLabel("Código:"));
        formulario.add(txtCodigoPlan);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombrePlan);
        formulario.add(new JLabel("Precio mensual S/:"));
        formulario.add(txtPrecioMensual);
        formulario.add(btnCrear);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Planes"));
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(formulario, BorderLayout.SOUTH);
        return panel;
    }

    /** Crea el plan del tipo elegido; si el nombre queda vacío usa el nombre por defecto del tipo. */
    private void crearPlan()
    {
        try
        {
            String codigo = txtCodigoPlan.getText().trim();
            String nombre = txtNombrePlan.getText().trim();
            double precio;
            try
            {
                precio = Double.parseDouble(txtPrecioMensual.getText().trim().replace(',', '.'));
            }
            catch (NumberFormatException e)
            {
                throw new DatosInvalidosException("El precio mensual debe ser un número.");
            }
            Plan plan = crearPlanDelTipo((String) cboTipoPlan.getSelectedItem(), codigo, nombre, precio);
            gimnasio.crearPlan(plan);
            txtCodigoPlan.setText("");
            txtNombrePlan.setText("");
            txtPrecioMensual.setText("");
            actualizar();
        }
        catch (DatosInvalidosException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Crear plan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Plan crearPlanDelTipo(String tipo, String codigo, String nombre, double precio)
            throws DatosInvalidosException
    {
        boolean sinNombre = nombre.isEmpty();
        switch (tipo)
        {
            case TRIMESTRAL:
                return sinNombre ? new PlanTrimestral(codigo, precio) : new PlanTrimestral(codigo, nombre, precio, "");
            case ANUAL:
                return sinNombre ? new PlanAnual(codigo, precio) : new PlanAnual(codigo, nombre, precio, "");
            default:
                return sinNombre ? new PlanMensual(codigo, precio) : new PlanMensual(codigo, nombre, precio, "");
        }
    }

    // ------------------------------------------------------------------
    // Historial de pagos
    // ------------------------------------------------------------------

    private JPanel crearSeccionPagos()
    {
        JTable tabla = new JTable(modeloPagos);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(600, 120));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historial de pagos"));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------
    // Modelos de tabla
    // ------------------------------------------------------------------

    /** Tabla de planes: código, nombre, tipo, meses, descuento del plan, importe y si está activo. */
    private static final class PlanTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;
        private static final String[] COLUMNAS = {"Código", "Nombre", "Tipo", "Meses", "Desc. plan", "Importe", "Activo"};
        private transient List<Plan> filas = new ArrayList<>();

        void setFilas(List<Plan> lista)
        {
            filas = new ArrayList<>(lista);
            fireTableDataChanged();
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
            Plan p = filas.get(fila);
            switch (col)
            {
                case 0:
                    return p.getCodigo();
                case 1:
                    return p.getNombre();
                case 2:
                    return p.getTipo();
                case 3:
                    return p.getDuracionMeses();
                case 4:
                    return Math.round(p.getDescuentoPlan() * 100) + " %";
                case 5:
                    return String.format(Locale.US, "S/ %.2f", p.calcularImporte());
                default:
                    return p.isActivo() ? "Sí" : "No";
            }
        }
    }

    /** Tabla de pagos: código, fecha, miembro, plan, método y total. */
    private static final class PagoTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;
        private static final String[] COLUMNAS = {"Código", "Fecha", "Miembro", "Plan", "Método", "Total"};
        private transient List<Pago> filas = new ArrayList<>();

        void setFilas(List<Pago> lista)
        {
            filas = new ArrayList<>(lista);
            fireTableDataChanged();
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
            Pago p = filas.get(fila);
            switch (col)
            {
                case 0:
                    return p.getCodigo();
                case 1:
                    return p.getFecha().format(FORMATO_FECHA);
                case 2:
                    return p.getMembresia().getMiembro().getNombreCompleto();
                case 3:
                    return p.getMembresia().getPlan().getNombre();
                case 4:
                    return p.getMetodo();
                default:
                    return String.format(Locale.US, "S/ %.2f", p.getTotal());
            }
        }
    }
}
