package gympro.gui;

import gympro.modelo.enums.EstadoInscripcion;
import gympro.modelo.persona.Miembro;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Pinta toda la fila de la tabla de miembros según su estado (HG-01 · T4):
 * verde (ACTIVO), amarillo (POR_VENCER), rojo (VENCIDO), blanco (SIN_MEMBRESIA) y
 * gris con texto apagado si el miembro fue dado de baja (decisión D6).
 *
 * <p>El miembro se obtiene del {@link MiembroTableModel} convirtiendo la fila de la vista
 * con {@code convertRowIndexToModel}, así el color es correcto aunque la tabla esté
 * filtrada u ordenada.</p>
 *
 * @author I2
 */
public class EstadoCellRenderer extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = 1L;

    public static final Color VERDE = new Color(198, 239, 206);
    public static final Color AMARILLO = new Color(255, 235, 156);
    public static final Color ROJO = new Color(255, 199, 206);
    public static final Color GRIS = new Color(225, 225, 225);
    private static final Color TEXTO_BAJA = new Color(120, 120, 120);

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna)
    {
        Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
        if (seleccionada)
        {
            return c;
        }
        c.setForeground(tabla.getForeground());
        if (tabla.getModel() instanceof MiembroTableModel)
        {
            Miembro m = ((MiembroTableModel) tabla.getModel()).getMiembro(tabla.convertRowIndexToModel(fila));
            if (!m.isActivo())
            {
                c.setBackground(GRIS);
                c.setForeground(TEXTO_BAJA);
                return c;
            }
            c.setBackground(colorDe(m.getEstadoInscripcion()));
            return c;
        }
        c.setBackground(tabla.getBackground());
        return c;
    }

    /** @return el color de fondo que corresponde al estado */
    public static Color colorDe(EstadoInscripcion estado)
    {
        if (estado == null)
        {
            return Color.WHITE;
        }
        switch (estado)
        {
            case ACTIVO:
                return VERDE;
            case POR_VENCER:
                return AMARILLO;
            case VENCIDO:
                return ROJO;
            default:
                return Color.WHITE;
        }
    }
}
