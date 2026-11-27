package gympro.gui;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.persona.Miembro;
import gympro.modelo.persona.MiembroCorporativo;
import gympro.modelo.persona.MiembroEstudiante;
import gympro.modelo.persona.MiembroRegular;
import gympro.servicio.Gimnasio;
import gympro.util.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Formulario modal para registrar o editar un miembro (HG-01 · T3).
 *
 * <ul>
 *   <li>Campos comunes en un {@link GridLayout}: tipo, DNI, nombres, apellidos,
 *       fecha de nacimiento, teléfono y email.</li>
 *   <li>Los campos propios del tipo van en un {@link CardLayout}: universidad y código
 *       (estudiante) o empresa y RUC (corporativo). Un {@code ItemListener} en el combo
 *       de tipo cambia la tarjeta.</li>
 *   <li>Al editar, el tipo y el DNI no se pueden cambiar (decisión D5: el DNI es la
 *       clave del {@code HashMap} de miembros).</li>
 *   <li>Si un dato no es válido se muestra el mensaje y el diálogo <b>no</b> se cierra.</li>
 * </ul>
 *
 * <p>Uso: crear el diálogo, {@code setVisible(true)} (bloquea hasta cerrarse) y luego
 * {@link #getMiembroGuardado()}: el miembro registrado o editado, o {@code null} si se canceló.</p>
 *
 * @author I2
 */
public final class DialogoMiembro extends JDialog
{
    private static final long serialVersionUID = 1L;

    private static final String REGULAR = "REGULAR";
    private static final String ESTUDIANTE = "ESTUDIANTE";
    private static final String CORPORATIVO = "CORPORATIVO";
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final transient Gimnasio gimnasio;
    /** Miembro que se edita; {@code null} si es un registro nuevo. */
    private final transient Miembro miembro;
    private transient Miembro miembroGuardado;

    private final JComboBox<String> cboTipo = new JComboBox<>(new String[]{REGULAR, ESTUDIANTE, CORPORATIVO});
    private final JTextField txtDni = new JTextField(16);
    private final JTextField txtNombres = new JTextField(16);
    private final JTextField txtApellidos = new JTextField(16);
    private final JTextField txtFechaNacimiento = new JTextField(16);
    private final JTextField txtTelefono = new JTextField(16);
    private final JTextField txtEmail = new JTextField(16);
    private final JTextField txtUniversidad = new JTextField(16);
    private final JTextField txtCodigoUniversitario = new JTextField(16);
    private final JTextField txtEmpresa = new JTextField(16);
    private final JTextField txtRuc = new JTextField(16);

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelTipo = new JPanel(tarjetas);

    /**
     * @param padre    ventana que queda bloqueada mientras el diálogo está abierto
     * @param gimnasio servicio donde se registra el miembro
     * @param miembro  miembro a editar, o {@code null} para registrar uno nuevo
     */
    public DialogoMiembro(Window padre, Gimnasio gimnasio, Miembro miembro)
    {
        super(padre, miembro == null ? "Nuevo miembro" : "Editar miembro " + miembro.getCodigo(),
                ModalityType.APPLICATION_MODAL);
        this.gimnasio = gimnasio;
        this.miembro = miembro;

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        contenido.add(crearCamposComunes(), BorderLayout.NORTH);
        contenido.add(crearCamposPorTipo(), BorderLayout.CENTER);
        contenido.add(crearBotones(), BorderLayout.SOUTH);
        setContentPane(contenido);

        if (miembro != null)
        {
            cargar(miembro);
        }
        cboTipo.addItemListener(e ->
        {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                tarjetas.show(panelTipo, (String) e.getItem());
            }
        });
        tarjetas.show(panelTipo, (String) cboTipo.getSelectedItem());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(padre);
    }

    /** @return el miembro registrado o editado, o {@code null} si se canceló */
    public Miembro getMiembroGuardado()
    {
        return miembroGuardado;
    }

    // ------------------------------------------------------------------
    // Construcción
    // ------------------------------------------------------------------

    private JPanel crearCamposComunes()
    {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        agregarCampo(panel, "Tipo:", cboTipo);
        agregarCampo(panel, "DNI (8 dígitos):", txtDni);
        agregarCampo(panel, "Nombres:", txtNombres);
        agregarCampo(panel, "Apellidos:", txtApellidos);
        agregarCampo(panel, "Fecha de nacimiento:", txtFechaNacimiento);
        agregarCampo(panel, "Teléfono (9 dígitos):", txtTelefono);
        agregarCampo(panel, "Email:", txtEmail);
        txtFechaNacimiento.setToolTipText("dd/mm/aaaa (opcional)");
        return panel;
    }

    private JPanel crearCamposPorTipo()
    {
        JLabel sinDatos = new JLabel("El miembro regular no tiene datos adicionales.", SwingConstants.CENTER);
        sinDatos.setForeground(Color.GRAY);
        JPanel regular = new JPanel(new BorderLayout());
        regular.add(sinDatos, BorderLayout.CENTER);

        JPanel estudiante = new JPanel(new GridLayout(0, 2, 8, 6));
        agregarCampo(estudiante, "Universidad:", txtUniversidad);
        agregarCampo(estudiante, "Código universitario:", txtCodigoUniversitario);

        JPanel corporativo = new JPanel(new GridLayout(0, 2, 8, 6));
        agregarCampo(corporativo, "Empresa:", txtEmpresa);
        agregarCampo(corporativo, "RUC (11 dígitos):", txtRuc);

        panelTipo.add(regular, REGULAR);
        panelTipo.add(estudiante, ESTUDIANTE);
        panelTipo.add(corporativo, CORPORATIVO);
        panelTipo.setBorder(BorderFactory.createTitledBorder("Datos del tipo de miembro"));
        return panelTipo;
    }

    private JPanel crearBotones()
    {
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);
        // Escape equivale a Cancelar
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    private static void agregarCampo(JPanel panel, String etiqueta, JComponent campo)
    {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    /** Llena el formulario con los datos del miembro a editar y bloquea tipo y DNI (D5). */
    private void cargar(Miembro m)
    {
        cboTipo.setSelectedItem(m.getTipo());
        cboTipo.setEnabled(false);
        txtDni.setText(m.getDni());
        txtDni.setEditable(false);
        txtNombres.setText(m.getNombres());
        txtApellidos.setText(m.getApellidos());
        txtFechaNacimiento.setText(m.getFechaNacimiento() == null ? "" : m.getFechaNacimiento().format(FORMATO_FECHA));
        txtTelefono.setText(texto(m.getTelefono()));
        txtEmail.setText(texto(m.getEmail()));
        if (m instanceof MiembroEstudiante)
        {
            MiembroEstudiante e = (MiembroEstudiante) m;
            txtUniversidad.setText(texto(e.getUniversidad()));
            txtCodigoUniversitario.setText(texto(e.getCodigoUniversitario()));
        }
        else if (m instanceof MiembroCorporativo)
        {
            MiembroCorporativo c = (MiembroCorporativo) m;
            txtEmpresa.setText(texto(c.getEmpresa()));
            txtRuc.setText(texto(c.getRuc()));
        }
    }

    // ------------------------------------------------------------------
    // Guardar
    // ------------------------------------------------------------------

    /**
     * Valida <b>todos</b> los datos antes de tocar el miembro, para no dejarlo a medio
     * editar si un campo falla. Si algo no es válido, muestra el mensaje y no cierra.
     */
    private void guardar()
    {
        try
        {
            String tipo = (String) cboTipo.getSelectedItem();
            String dni = txtDni.getText().trim();
            String nombres = obligatorio(txtNombres, "Los nombres no pueden estar vacíos.");
            String apellidos = obligatorio(txtApellidos, "Los apellidos no pueden estar vacíos.");
            LocalDate fechaNacimiento = leerFechaNacimiento();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();

            Validador.validarDni(dni);
            Validador.validarTelefono(telefono);
            Validador.validarEmail(email);

            String extra1 = null;
            String extra2 = null;
            if (ESTUDIANTE.equals(tipo))
            {
                extra1 = obligatorio(txtUniversidad, "La universidad no puede estar vacía.");
                extra2 = obligatorio(txtCodigoUniversitario, "El código universitario no puede estar vacío.");
            }
            else if (CORPORATIVO.equals(tipo))
            {
                extra1 = obligatorio(txtEmpresa, "La empresa no puede estar vacía.");
                extra2 = txtRuc.getText().trim();
                if (!extra2.matches("\\d{11}"))
                {
                    throw new DatosInvalidosException("El RUC debe tener exactamente 11 dígitos numéricos.");
                }
            }

            if (miembro == null)
            {
                miembroGuardado = registrar(tipo, dni, nombres, apellidos, fechaNacimiento, telefono, email, extra1, extra2);
            }
            else
            {
                editar(nombres, apellidos, fechaNacimiento, telefono, email, extra1, extra2);
                miembroGuardado = miembro;
            }
            dispose();
        }
        catch (DatosInvalidosException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Miembro registrar(String tipo, String dni, String nombres, String apellidos, LocalDate fechaNacimiento,
                              String telefono, String email, String extra1, String extra2)
            throws DatosInvalidosException
    {
        // Se revisa antes de crear el objeto para no gastar un código M000n con un DNI repetido.
        if (existeDni(dni))
        {
            throw new DatosInvalidosException("Ya existe un miembro con DNI " + dni + ".");
        }
        Miembro nuevo;
        switch (tipo)
        {
            case ESTUDIANTE:
                nuevo = new MiembroEstudiante(dni, nombres, apellidos, fechaNacimiento, telefono, email, extra1, extra2);
                break;
            case CORPORATIVO:
                nuevo = new MiembroCorporativo(dni, nombres, apellidos, fechaNacimiento, telefono, email, extra1, extra2);
                break;
            default:
                nuevo = new MiembroRegular(dni, nombres, apellidos, fechaNacimiento, telefono, email);
                break;
        }
        gimnasio.registrarMiembro(nuevo);
        return nuevo;
    }

    /**
     * Aplica los cambios con los setters de {@code Persona} y de la subclase (todos los
     * datos ya se validaron) y los confirma con {@code gimnasio.actualizarMiembro}.
     */
    private void editar(String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email,
                        String extra1, String extra2) throws DatosInvalidosException
    {
        miembro.setNombres(nombres);
        miembro.setApellidos(apellidos);
        miembro.setFechaNacimiento(fechaNacimiento);
        miembro.setTelefono(telefono);
        miembro.setEmail(email);
        if (miembro instanceof MiembroEstudiante)
        {
            ((MiembroEstudiante) miembro).setUniversidad(extra1);
            ((MiembroEstudiante) miembro).setCodigoUniversitario(extra2);
        }
        else if (miembro instanceof MiembroCorporativo)
        {
            ((MiembroCorporativo) miembro).setEmpresa(extra1);
            ((MiembroCorporativo) miembro).setRuc(extra2);
        }
        try
        {
            gimnasio.actualizarMiembro(miembro);
        }
        catch (MiembroNoEncontradoException e)
        {
            throw new DatosInvalidosException(e.getMessage());
        }
    }

    private boolean existeDni(String dni)
    {
        try
        {
            gimnasio.buscarMiembro(dni);
            return true;
        }
        catch (MiembroNoEncontradoException e)
        {
            return false;
        }
    }

    /** La fecha es opcional; si se escribe, debe ser dd/mm/aaaa y anterior a hoy. */
    private LocalDate leerFechaNacimiento() throws DatosInvalidosException
    {
        String texto = txtFechaNacimiento.getText().trim();
        if (texto.isEmpty())
        {
            return null;
        }
        try
        {
            LocalDate fecha = LocalDate.parse(texto, FORMATO_FECHA);
            if (!fecha.isBefore(LocalDate.now()))
            {
                throw new DatosInvalidosException("La fecha de nacimiento debe ser anterior a hoy.");
            }
            return fecha;
        }
        catch (DateTimeParseException e)
        {
            throw new DatosInvalidosException("La fecha de nacimiento debe tener el formato dd/mm/aaaa.");
        }
    }

    private static String obligatorio(JTextField campo, String mensaje) throws DatosInvalidosException
    {
        String valor = campo.getText().trim();
        if (valor.isEmpty())
        {
            throw new DatosInvalidosException(mensaje);
        }
        return valor;
    }

    private static String texto(String valor)
    {
        return valor == null ? "" : valor;
    }
}
