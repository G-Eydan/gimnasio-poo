package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.membresia.Pago;
import gympro.modelo.membresia.Plan;
import gympro.modelo.membresia.PlanAnual;
import gympro.modelo.membresia.PlanMensual;
import gympro.modelo.membresia.PlanTrimestral;
import gympro.servicio.Gimnasio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pestaña «Planes y Pagos» (HG-01 · T5).
 *
 * <ul>
 *   <li><b>NORTH</b>: tabla de planes y formulario para crear uno ({@code gimnasio.crearPlan}).</li>
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

    public PanelPlanesPagos(Gimnasio gimnasio)
    {
        super(new BorderLayout(0, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(crearSeccionPlanes(), BorderLayout.NORTH);
        add(crearSeccionPagos(), BorderLayout.SOUTH);

        actualizar();
    }

    /** Recarga planes y pagos desde {@code Gimnasio}. */
    @Override
    public void actualizar()
    {
        modeloPlanes.setFilas(gimnasio.listarPlanes());
        modeloPagos.setFilas(gimnasio.getPagos());
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
