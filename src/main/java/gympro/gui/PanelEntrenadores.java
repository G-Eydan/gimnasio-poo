package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.modelo.enums.DiaSemana;
import gympro.modelo.enums.Especialidad;
import gympro.modelo.enums.Turno;
import gympro.modelo.persona.Entrenador;
import gympro.servicio.Gimnasio;
import gympro.util.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pestaña «Entrenadores» (HU-06 · T2): formulario para registrar entrenadores.
 *
 * <ul>
 *   <li>Datos personales y sueldo base en {@link JTextField}; turno en un {@link JComboBox}.</li>
 *   <li>Un {@link JCheckBox} por cada {@link Especialidad}, creados con un bucle.</li>
 *   <li>Grilla 7 × 3 de disponibilidad: un arreglo 2D de {@link JCheckBox} que se copia al
 *       arreglo 2D {@code boolean} del entrenador.</li>
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

    public PanelEntrenadores(Gimnasio gimnasio)
    {
        super(new BorderLayout(10, 8));
        this.gimnasio = gimnasio;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(crearFormulario(), BorderLayout.WEST);
        actualizar();
    }

    /** Recarga los datos desde {@code Gimnasio}. */
    @Override
    public void actualizar()
    {
        // El formulario no depende de otros datos; la lista se agrega con la tabla de entrenadores.
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

            Entrenador entrenador = new Entrenador(dni, txtNombres.getText().trim(), txtApellidos.getText().trim(),
                    null, telefono, email, sueldo, (Turno) cboTurno.getSelectedItem());
            for (Especialidad e : Especialidad.values())
            {
                if (chkEspecialidades[e.ordinal()].isSelected())
                {
                    entrenador.agregarEspecialidad(e);
                }
            }
            if (entrenador.getEspecialidades().isEmpty())
            {
                throw new DatosInvalidosException("Marque al menos una especialidad.");
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
}
