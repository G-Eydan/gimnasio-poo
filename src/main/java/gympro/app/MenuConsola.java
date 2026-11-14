package gympro.app;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.membresia.Plan;
import gympro.modelo.membresia.PlanAnual;
import gympro.modelo.membresia.PlanMensual;
import gympro.modelo.membresia.PlanTrimestral;
import gympro.modelo.persona.Miembro;
import gympro.modelo.persona.MiembroCorporativo;
import gympro.modelo.persona.MiembroEstudiante;
import gympro.modelo.persona.MiembroRegular;
import gympro.servicio.Gimnasio;
import gympro.util.Validador;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Menú de consola del Sprint 1 (HU-01 y HU-04): registrar, buscar y listar
 * miembros, y crear y listar planes. En el Sprint 3 se reemplaza por la GUI.
 *
 * <p>Toda la lógica está en {@link Gimnasio}; esta clase solo pide los datos,
 * llama al servicio y muestra el resultado o el mensaje de error.</p>
 *
 * @author I2
 */
public class MenuConsola
{
    /** Formato estricto: rechaza fechas imposibles como 31/02/2000. */
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final String SEPARADOR = "------------------------------------------------------------";

    private final Gimnasio gimnasio;
    private final Scanner entrada;

    /** Menú que lee del teclado. */
    public MenuConsola(Gimnasio gimnasio)
    {
        this(gimnasio, new Scanner(System.in));
    }

    /** Menú que lee de un {@link Scanner} dado (útil para pruebas con entradas fijas). */
    public MenuConsola(Gimnasio gimnasio, Scanner entrada)
    {
        this.gimnasio = gimnasio;
        this.entrada = entrada;
    }

    /** Muestra el menú hasta que el usuario elige salir. */
    public void iniciar()
    {
        int opcion;
        do
        {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            System.out.println();
            switch (opcion)
            {
                case 1 -> registrarMiembro();
                case 2 -> buscarMiembro();
                case 3 -> listarMiembros();
                case 4 -> crearPlan();
                case 5 -> listarPlanes();
                case 0 -> System.out.println("Hasta pronto.");
                default -> System.out.println("Opción no válida.");
            }
        }
        while (opcion != 0);
    }

    private void mostrarMenu()
    {
        System.out.println();
        System.out.println("========== " + gimnasio.getNombre() + " ==========");
        System.out.println("1. Registrar miembro");
        System.out.println("2. Buscar miembro por DNI");
        System.out.println("3. Listar miembros");
        System.out.println("4. Crear plan");
        System.out.println("5. Listar planes");
        System.out.println("0. Salir");
    }

    // ------------------------------------------------------------------
    // Miembros (HU-01)
    // ------------------------------------------------------------------

    private void registrarMiembro()
    {
        System.out.println("--- Registrar miembro ---");
        System.out.println("Tipo: 1. Regular   2. Estudiante (15 %)   3. Corporativo (20 %)");
        int tipo = leerEntero("Tipo: ");
        if (tipo < 1 || tipo > 3)
        {
            System.out.println("Tipo no válido.");
            return;
        }
        try
        {
            String dni = leerTexto("DNI (8 dígitos): ");
            Validador.validarDni(dni);
            String nombres = leerTextoObligatorio("Nombres: ");
            String apellidos = leerTextoObligatorio("Apellidos: ");
            LocalDate fechaNacimiento = leerFechaOpcional("Fecha de nacimiento (dd/mm/aaaa, Enter para omitir): ");
            String telefono = leerTexto("Teléfono (9 dígitos): ");
            Validador.validarTelefono(telefono);
            String email = leerTexto("Email: ");
            Validador.validarEmail(email);

            Miembro miembro = crearMiembro(tipo, dni, nombres, apellidos, fechaNacimiento, telefono, email);
            gimnasio.registrarMiembro(miembro);
            System.out.println("Miembro registrado con código " + miembro.getCodigo() + ".");
        }
        catch (DatosInvalidosException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Crea el miembro del tipo elegido y pide los datos propios de cada subclase.
     * Es el único lugar que depende de los constructores de las subclases de Miembro.
     */
    private Miembro crearMiembro(int tipo, String dni, String nombres, String apellidos,
                                 LocalDate fechaNacimiento, String telefono, String email)
            throws DatosInvalidosException
    {
        switch (tipo)
        {
            case 2:
                String universidad = leerTextoObligatorio("Universidad: ");
                String codigoUniversitario = leerTextoObligatorio("Código universitario: ");
                return new MiembroEstudiante(dni, nombres, apellidos, fechaNacimiento, telefono, email,
                        universidad, codigoUniversitario);
            case 3:
                String empresa = leerTextoObligatorio("Empresa: ");
                String ruc = leerTextoObligatorio("RUC: ");
                return new MiembroCorporativo(dni, nombres, apellidos, fechaNacimiento, telefono, email,
                        empresa, ruc);
            default:
                return new MiembroRegular(dni, nombres, apellidos, fechaNacimiento, telefono, email);
        }
    }

    private void buscarMiembro()
    {
        System.out.println("--- Buscar miembro ---");
        String dni = leerTexto("DNI: ");
        try
        {
            Miembro m = gimnasio.buscarMiembro(dni);
            System.out.println(SEPARADOR);
            System.out.println("Código     : " + m.getCodigo());
            System.out.println("Nombre     : " + m.getNombreCompleto());
            System.out.println("DNI        : " + m.getDni());
            System.out.println("Tipo       : " + m.getTipo()
                    + " (descuento " + Math.round(m.getPorcentajeDescuento() * 100) + " %)");
            System.out.println("Teléfono   : " + m.getTelefono());
            System.out.println("Email      : " + m.getEmail());
            System.out.println("Inscripción: " + m.getEstadoInscripcion());
            System.out.println(SEPARADOR);
        }
        catch (MiembroNoEncontradoException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarMiembros()
    {
        System.out.println("--- Miembros (ordenados por apellido) ---");
        ArrayList<Miembro> miembros = gimnasio.listarMiembros();
        if (miembros.isEmpty())
        {
            System.out.println("No hay miembros registrados.");
            return;
        }
        System.out.printf("%-7s %-10s %-32s %-12s%n", "Código", "DNI", "Apellidos, nombres", "Tipo");
        System.out.println(SEPARADOR);
        for (Miembro m : miembros)
        {
            System.out.printf("%-7s %-10s %-32s %-12s%n", m.getCodigo(), m.getDni(),
                    recortar(m.getApellidos() + ", " + m.getNombres(), 32), m.getTipo());
        }
        System.out.println("Total: " + miembros.size());
    }

    // ------------------------------------------------------------------
    // Planes (HU-04)
    // ------------------------------------------------------------------

    private void crearPlan()
    {
        System.out.println("--- Crear plan ---");
        System.out.println("Tipo: 1. Mensual   2. Trimestral (10 %)   3. Anual (20 %)");
        int tipo = leerEntero("Tipo: ");
        if (tipo < 1 || tipo > 3)
        {
            System.out.println("Tipo no válido.");
            return;
        }
        String codigo = leerTexto("Código del plan (ej. PM, PT, PA): ");
        double precio = leerDouble("Precio mensual base (S/): ");
        try
        {
            Plan plan;
            switch (tipo)
            {
                case 2:
                    plan = new PlanTrimestral(codigo, precio);
                    break;
                case 3:
                    plan = new PlanAnual(codigo, precio);
                    break;
                default:
                    plan = new PlanMensual(codigo, precio);
            }
            gimnasio.crearPlan(plan);
            System.out.println("Plan creado: " + plan);
        }
        catch (DatosInvalidosException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarPlanes()
    {
        System.out.println("--- Planes ---");
        ArrayList<Plan> planes = gimnasio.listarPlanes();
        if (planes.isEmpty())
        {
            System.out.println("No hay planes registrados.");
            return;
        }
        for (Plan p : planes)
        {
            System.out.println(p);
        }
    }

    // ------------------------------------------------------------------
    // Lectura de datos
    // ------------------------------------------------------------------

    private String leerTexto(String mensaje)
    {
        System.out.print(mensaje);
        return entrada.hasNextLine() ? entrada.nextLine().trim() : "";
    }

    private String leerTextoObligatorio(String mensaje) throws DatosInvalidosException
    {
        String texto = leerTexto(mensaje);
        if (texto.isEmpty())
        {
            throw new DatosInvalidosException("El campo no puede estar vacío.");
        }
        return texto;
    }

    /** Pide un entero hasta que sea válido; si se acaba la entrada devuelve 0 (salir). */
    private int leerEntero(String mensaje)
    {
        while (entrada.hasNextLine())
        {
            String texto = leerTexto(mensaje);
            try
            {
                return Integer.parseInt(texto);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Ingrese un número entero.");
            }
        }
        return 0;
    }

    /** Pide un número decimal hasta que sea válido (acepta coma o punto); sin entrada devuelve -1. */
    private double leerDouble(String mensaje)
    {
        while (entrada.hasNextLine())
        {
            String texto = leerTexto(mensaje).replace(',', '.');
            try
            {
                return Double.parseDouble(texto);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Ingrese un número válido.");
            }
        }
        return -1;
    }

    /** Pide una fecha dd/mm/aaaa; si se deja vacía devuelve {@code null}. */
    private LocalDate leerFechaOpcional(String mensaje) throws DatosInvalidosException
    {
        String texto = leerTexto(mensaje);
        if (texto.isEmpty())
        {
            return null;
        }
        LocalDate fecha;
        try
        {
            fecha = LocalDate.parse(texto, FORMATO_FECHA);
        }
        catch (DateTimeParseException e)
        {
            throw new DatosInvalidosException("Fecha no válida, use dd/mm/aaaa.");
        }
        if (fecha.isAfter(LocalDate.now()))
        {
            throw new DatosInvalidosException("La fecha de nacimiento no puede ser futura.");
        }
        return fecha;
    }

    private static String recortar(String texto, int max)
    {
        return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
    }
}
