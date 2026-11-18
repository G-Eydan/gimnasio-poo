package gympro.app;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MembresiaActivaException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.enums.MetodoPago;
import gympro.modelo.membresia.Membresia;
import gympro.modelo.membresia.Pago;
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
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;

/**
 * Menú de consola: registrar, buscar y listar miembros, crear y listar planes
 * (Sprint 1: HU-01 y HU-04), contratar o renovar membresías y ver la ficha del
 * miembro con su estado e historial (Sprint 2: HU-02 y HU-03).
 * En el Sprint 3 se reemplaza por la GUI.
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
                case 6 -> contratarMembresia();
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
        System.out.println("6. Contratar / renovar membresía");
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
            gimnasio.actualizarEstadosMembresias();
            mostrarFicha(gimnasio.buscarMiembro(dni));
        }
        catch (MiembroNoEncontradoException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Ficha del miembro (HU-03): datos personales, estado de inscripción, fecha de
     * vencimiento, días restantes e historial de membresías ordenado por fecha de inicio.
     */
    private void mostrarFicha(Miembro m)
    {
        System.out.println(SEPARADOR);
        System.out.println("Código     : " + m.getCodigo());
        System.out.println("Nombre     : " + m.getNombreCompleto());
        System.out.println("DNI        : " + m.getDni());
        System.out.println("Tipo       : " + m.getTipo()
                + " (descuento " + Math.round(m.getPorcentajeDescuento() * 100) + " %)");
        System.out.println("Teléfono   : " + m.getTelefono());
        System.out.println("Email      : " + m.getEmail());
        System.out.println("Inscripción: " + m.getEstadoInscripcion());

        Membresia actual = m.getMembresiaActual();
        if (actual != null)
        {
            System.out.println("Vence      : " + actual.getFechaFin().format(FORMATO_FECHA));
            System.out.println("Días rest. : " + m.getDiasRestantes());
        }

        System.out.println();
        System.out.println("Historial de membresías:");
        ArrayList<Membresia> historial = m.getHistorialMembresias();
        if (historial.isEmpty())
        {
            System.out.println("  (sin membresías)");
        }
        else
        {
            historial.sort(Comparator.comparing(Membresia::getFechaInicio));
            System.out.printf("  %-7s %-18s %-10s %-10s %-10s%n", "Código", "Plan", "Inicio", "Fin", "Estado");
            for (Membresia mem : historial)
            {
                System.out.printf("  %-7s %-18s %-10s %-10s %-10s%n", mem.getCodigo(),
                        recortar(mem.getPlan().getNombre(), 18),
                        mem.getFechaInicio().format(FORMATO_FECHA),
                        mem.getFechaFin().format(FORMATO_FECHA), mem.getEstado());
            }
        }
        System.out.println(SEPARADOR);
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
    // Membresías (HU-02)
    // ------------------------------------------------------------------

    /**
     * Contrata una membresía: pide miembro, plan y método de pago, muestra el total
     * y, al confirmar, registra el pago y muestra el comprobante con el desglose.
     * Si el miembro ya tiene una membresía vigente, ofrece renovarla (RN-06).
     */
    private void contratarMembresia()
    {
        System.out.println("--- Contratar / renovar membresía ---");
        try
        {
            String dni = leerTexto("DNI del miembro: ");
            Miembro miembro = gimnasio.buscarMiembro(dni);
            System.out.println("Miembro: " + miembro.getNombreCompleto() + " (" + miembro.getTipo() + ")");
            System.out.println();

            if (gimnasio.listarPlanes().isEmpty())
            {
                System.out.println("No hay planes registrados. Cree uno con la opción 4.");
                return;
            }
            listarPlanes();
            String codPlan = leerTexto("Código del plan: ");
            Plan plan = gimnasio.buscarPlan(codPlan);
            if (plan == null)
            {
                System.out.println("Error: no existe un plan con el código " + codPlan + ".");
                return;
            }

            MetodoPago metodo = leerMetodoPago();
            if (metodo == null)
            {
                System.out.println("Método de pago no válido.");
                return;
            }

            System.out.printf(Locale.US, "Total a pagar: S/ %.2f%n", gimnasio.calcularImporte(miembro, plan, metodo));
            if (!confirmar("¿Confirmar el pago? (S/N): "))
            {
                System.out.println("Operación cancelada.");
                return;
            }

            Pago pago;
            try
            {
                pago = gimnasio.contratarMembresia(dni, codPlan, metodo);
            }
            catch (MembresiaActivaException e)
            {
                System.out.println(e.getMessage());
                if (!confirmar("¿Desea renovarla? La nueva empieza al terminar la vigente. (S/N): "))
                {
                    System.out.println("Operación cancelada.");
                    return;
                }
                pago = gimnasio.renovarMembresia(dni, codPlan, metodo);
            }
            System.out.println();
            System.out.print(pago.generarComprobante());
            System.out.println("Estado de la membresía: " + pago.getMembresia().getEstado());
        }
        catch (MiembroNoEncontradoException | MembresiaActivaException | DatosInvalidosException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Lectura de datos
    // ------------------------------------------------------------------

    /** Muestra los métodos de pago con su recargo y devuelve el elegido, o {@code null} si no es válido. */
    private MetodoPago leerMetodoPago()
    {
        MetodoPago[] metodos = MetodoPago.values();
        System.out.println("Método de pago:");
        for (int i = 0; i < metodos.length; i++)
        {
            System.out.printf(Locale.US, "  %d. %s (recargo %.0f %%)%n", i + 1, metodos[i], metodos[i].getRecargo() * 100);
        }
        int opcion = leerEntero("Método: ");
        return (opcion >= 1 && opcion <= metodos.length) ? metodos[opcion - 1] : null;
    }

    /** Devuelve {@code true} solo si el usuario responde S o s. */
    private boolean confirmar(String mensaje)
    {
        return leerTexto(mensaje).equalsIgnoreCase("S");
    }

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
