package gympro.gui;

import gympro.modelo.membresia.Membresia;
import gympro.modelo.persona.Miembro;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de la tabla de miembros (HG-01 · T2): adapta la lista de {@link Miembro}
 * de {@code Gimnasio} a la {@link javax.swing.JTable} de {@link PanelMiembros}.
 *
 * <p>Columnas: DNI, Nombre, Tipo, Plan, Vence, Días, Estado y Entrenador. La columna
 * Estado devuelve el {@link gympro.modelo.enums.EstadoInscripcion} (no un texto) para
 * que el filtro de estado y el renderer de colores puedan compararlo directamente.</p>
 *
 * @author I2
 */
public class MiembroTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    public static final int COL_DNI = 0;
    public static final int COL_NOMBRE = 1;
    public static final int COL_TIPO = 2;
    public static final int COL_PLAN = 3;
    public static final int COL_VENCE = 4;
    public static final int COL_DIAS = 5;
    public static final int COL_ESTADO = 6;
    public static final int COL_ENTRENADOR = 7;

    /** Formato de la columna Vence; {@link PanelMiembros} lo usa para ordenar por fecha. */
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Texto de las celdas sin dato (sin plan, sin vencimiento, sin entrenador). */
    public static final String SIN_DATO = "-";

    private static final String[] COLUMNAS =
            {"DNI", "Nombre", "Tipo", "Plan", "Vence", "Días", "Estado", "Entrenador"};

    private transient List<Miembro> miembros = new ArrayList<>();

    /** Reemplaza los datos y avisa a la tabla para que se repinte. */
    public void setMiembros(List<Miembro> lista)
    {
        miembros = (lista == null) ? new ArrayList<>() : new ArrayList<>(lista);
        fireTableDataChanged();
    }

    /**
     * @param filaModelo fila del <b>modelo</b>; si viene de la tabla, convertirla antes
     *                   con {@code tabla.convertRowIndexToModel(filaVista)}
     */
    public Miembro getMiembro(int filaModelo)
    {
        return miembros.get(filaModelo);
    }

    /** @return fila del modelo del miembro con ese DNI, o −1 si no está */
    public int indiceDe(String dni)
    {
        for (int i = 0; i < miembros.size(); i++)
        {
            if (miembros.get(i).getDni().equals(dni))
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getRowCount()
    {
        return miembros.size();
    }

    @Override
    public int getColumnCount()
    {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna)
    {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna)
    {
        Miembro m = miembros.get(fila);
        Membresia actual = m.getMembresiaActual();
        switch (columna)
        {
            case COL_DNI:
                return m.getDni();
            case COL_NOMBRE:
                return m.getNombreCompleto();
            case COL_TIPO:
                return m.getTipo();
            case COL_PLAN:
                return (actual == null) ? SIN_DATO : actual.getPlan().getNombre();
            case COL_VENCE:
                LocalDate vence = m.getFechaVencimiento();
                return (vence == null) ? SIN_DATO : vence.format(FORMATO_FECHA);
            case COL_DIAS:
                return (m.getMembresiaVigente() == null) ? SIN_DATO : String.valueOf(m.getDiasRestantes());
            case COL_ESTADO:
                return m.getEstadoInscripcion();
            default:
                return (m.getEntrenador() == null) ? SIN_DATO : m.getEntrenador().getNombreCompleto();
        }
    }
}
