package gympro;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MembresiaActivaException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.enums.EstadoMembresia;
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
import gympro.modelo.persona.Usuario;
import gympro.modelo.enums.Rol;
import gympro.servicio.Gimnasio;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Pruebas de la lógica de negocio de I1 (sin JUnit, para que corran con solo Java).
 * Cubren HU-04, HU-12 y los casos CP-01 a CP-09 y CP-18 de la especificación.
 *
 * Se ejecuta como cualquier clase con main (en IntelliJ: clic derecho, Run).
 */
public class PruebasLogicaNegocio {

    private static int correctas = 0;
    private static int fallidas = 0;

    private interface Accion {
        void ejecutar() throws Exception;
    }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            correctas++;
            System.out.println("  OK     " + nombre);
        } else {
            fallidas++;
            System.out.println("  FALLO  " + nombre);
        }
    }

    private static void verificarMonto(String nombre, double obtenido, double esperado) {
        boolean ok = Math.abs(obtenido - esperado) < 0.0001;
        verificar(nombre + " (obtenido " + obtenido + ", esperado " + esperado + ")", ok);
    }

    private static void esperarExcepcion(String nombre, Class<? extends Exception> tipo, Accion accion) {
        try {
            accion.ejecutar();
            verificar(nombre + " (no lanzó excepción)", false);
        } catch (Exception e) {
            verificar(nombre + " -> " + e.getClass().getSimpleName(), tipo.isInstance(e));
        }
    }

    private static Gimnasio nuevoGimnasio() throws Exception {
        Gimnasio g = new Gimnasio("GymPro Test");
        g.crearPlan(new PlanMensual("PM", 120));
        g.crearPlan(new PlanTrimestral("PT", 120));
        g.crearPlan(new PlanAnual("PA", 120));
        g.registrarMiembro(new MiembroRegular("70000001", "Luis", "Perez Rojas", null, null, null));
        g.registrarMiembro(new MiembroEstudiante("70000002", "Ana", "Quispe Mamani", null, null, null, null, null));
        g.registrarMiembro(new MiembroCorporativo("70000003", "Carlos", "Flores Vega", null, null, null, null, null));
        return g;
    }

    public static void main(String[] args) throws Exception {
        pruebaPlanes();
        pruebaImportes();
        pruebaContratarYRenovar();
        pruebaVencimientos();
        pruebaOrdenVencimientos();
        pruebaMiembrosYPlanes();
        pruebaComprobante();
        pruebaUsuarios();
        pruebaActualizarYDarDeBaja();

        System.out.println();
        System.out.println("Resultado: " + correctas + " correctas, " + fallidas + " con fallo.");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    // HG-01 · T1: editar y dar de baja (decisiones D5 y D6)
    private static void pruebaActualizarYDarDeBaja() throws Exception {
        Gimnasio g = nuevoGimnasio();
        Miembro luis = g.buscarMiembro("70000001");
        verificar("Un miembro nuevo está activo", luis.isActivo());

        luis.setNombres("Luis Alberto");
        luis.setTelefono("987654321");
        g.actualizarMiembro(luis);
        verificar("actualizarMiembro conserva los cambios", g.buscarMiembro("70000001").getNombres().equals("Luis Alberto"));
        esperarExcepcion("actualizarMiembro(null)", DatosInvalidosException.class, () -> g.actualizarMiembro(null));
        esperarExcepcion("Actualizar un miembro no registrado", MiembroNoEncontradoException.class,
                () -> g.actualizarMiembro(new MiembroRegular("79999999", "No", "Existe", null, null, null)));
        esperarExcepcion("Actualizar otro objeto con el mismo DNI", DatosInvalidosException.class,
                () -> g.actualizarMiembro(new MiembroRegular("70000001", "Otro", "Objeto", null, null, null)));

        g.contratarMembresia("70000002", "PM", MetodoPago.EFECTIVO);
        g.darDeBajaMiembro("70000002");
        Miembro ana = g.buscarMiembro("70000002");
        verificar("Dar de baja deja activo = false", !ana.isActivo());
        verificar("El dado de baja sigue registrado (baja lógica)", g.listarMiembros().contains(ana));
        verificar("El dado de baja no aparece en listarMiembrosActivos", !g.listarMiembrosActivos().contains(ana));
        esperarExcepcion("Dar de baja un DNI inexistente", MiembroNoEncontradoException.class, () -> g.darDeBajaMiembro("79999999"));
        esperarExcepcion("Contratar a un dado de baja", DatosInvalidosException.class,
                () -> g.contratarMembresia("70000002", "PT", MetodoPago.EFECTIVO, LocalDate.now().plusMonths(3)));
        esperarExcepcion("Renovar a un dado de baja", DatosInvalidosException.class,
                () -> g.renovarMembresia("70000002", "PT", MetodoPago.EFECTIVO));
    }

    // HU-12 y CP-18
    private static void pruebaUsuarios() throws Exception {
        System.out.println("HU-12 - Usuarios y roles");
        Gimnasio g = new Gimnasio("GymPro Test");
        g.registrarUsuario(new Usuario("admin", "admin123", Rol.ADMINISTRADOR));
        g.registrarUsuario(new Usuario("recepcion", "recepcion123", Rol.RECEPCIONISTA));
        g.registrarUsuario(new Usuario("entrenador", "entrenador123", Rol.ENTRENADOR));

        verificar("Login admin correcto", g.iniciarSesion("admin", "admin123").getRol() == Rol.ADMINISTRADOR);
        verificar("Usuario sin distinguir mayúsculas", g.iniciarSesion("Recepcion", "recepcion123").getRol() == Rol.RECEPCIONISTA);
        esperarExcepcion("Contraseña incorrecta", DatosInvalidosException.class,
                () -> g.iniciarSesion("admin", "otra"));
        esperarExcepcion("Contraseña distingue mayúsculas", DatosInvalidosException.class,
                () -> g.iniciarSesion("admin", "ADMIN123"));
        esperarExcepcion("Usuario inexistente", DatosInvalidosException.class,
                () -> g.iniciarSesion("nadie", "admin123"));
        esperarExcepcion("Usuario duplicado", DatosInvalidosException.class,
                () -> g.registrarUsuario(new Usuario("ADMIN", "x", Rol.RECEPCIONISTA)));
        esperarExcepcion("Usuario vacío", DatosInvalidosException.class,
                () -> new Usuario(" ", "x", Rol.ADMINISTRADOR));

        Usuario admin = g.iniciarSesion("admin", "admin123");
        Usuario recepcion = g.iniciarSesion("recepcion", "recepcion123");
        Usuario entrenador = g.iniciarSesion("entrenador", "entrenador123");
        verificar("Admin accede a reportes", admin.puedeAcceder(Usuario.MODULO_REPORTES));
        verificar("Recepción accede a planes y pagos", recepcion.puedeAcceder(Usuario.MODULO_PLANES_PAGOS));
        verificar("Recepción no accede a reportes", !recepcion.puedeAcceder(Usuario.MODULO_REPORTES));
        verificar("CP-18 entrenador no ve reportes", !entrenador.puedeAcceder(Usuario.MODULO_REPORTES));
        verificar("Entrenador accede a rutinas", entrenador.puedeAcceder(Usuario.MODULO_RUTINAS));
        verificar("toString no muestra la contraseña", !admin.toString().contains("admin123"));
    }

    // HU-04
    private static void pruebaPlanes() throws Exception {
        System.out.println("HU-04 - Planes");
        verificarMonto("Mensual 120", new PlanMensual("PM", 120).calcularImporte(), 120.00);
        verificarMonto("Trimestral 120", new PlanTrimestral("PT", 120).calcularImporte(), 324.00);
        verificarMonto("Anual 120", new PlanAnual("PA", 120).calcularImporte(), 1152.00);
        esperarExcepcion("Precio negativo", DatosInvalidosException.class, () -> new PlanMensual("PM", -5));
        esperarExcepcion("Nombre vacío", DatosInvalidosException.class, () -> new PlanMensual("PM", "  ", 120, ""));

        Plan trimestral = new PlanTrimestral("PT", 120);
        verificar("Fin trimestral desde 01/11/2026 = 31/01/2027",
                trimestral.calcularFechaFin(LocalDate.of(2026, 11, 1)).equals(LocalDate.of(2027, 1, 31)));
        verificar("Polimorfismo: lista de Plan con las 3 subclases",
                nuevoGimnasio().listarPlanes().size() == 3);
    }

    // CP-03, CP-04, CP-05
    private static void pruebaImportes() throws Exception {
        System.out.println("CP-03/04/05 - Importes");

        // CP-03: estudiante + trimestral + primera vez + tarjeta
        Gimnasio g = nuevoGimnasio();
        Miembro ana = g.buscarMiembro("70000002");
        Plan pt = g.buscarPlan("PT");
        verificarMonto("Vista previa antes de contratar", g.calcularImporte(ana, pt, MetodoPago.TARJETA), 335.16);
        Pago p = g.contratarMembresia("70000002", "PT", MetodoPago.TARJETA, LocalDate.of(2026, 11, 1));
        verificarMonto("CP-03 total", p.getTotal(), 335.16);
        verificarMonto("CP-03 importe del plan", p.getMontoPlan(), 324.00);
        verificarMonto("CP-03 descuento", p.getDescuento(), 48.60);
        verificarMonto("CP-03 matrícula", p.getMatricula(), 50.00);
        verificarMonto("CP-03 recargo", p.getRecargo(), 9.76);
        verificarMonto("CP-03 calcularTotal()", p.calcularTotal(), 335.16);
        verificarMonto("Vista previa 2da vez (sin matrícula)", g.calcularImporte(ana, pt, MetodoPago.TARJETA), 283.66);

        // CP-04: regular + mensual + segunda vez + efectivo
        Gimnasio g2 = nuevoGimnasio();
        Pago primera = g2.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 1, 1));
        verificarMonto("Regular 1ra vez (con matrícula)", primera.getTotal(), 170.00);
        Pago segunda = g2.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 6, 1));
        verificarMonto("CP-04 total", segunda.getTotal(), 120.00);
        verificarMonto("CP-04 importe del plan", segunda.getMontoPlan(), 120.00);
        verificarMonto("CP-04 descuento", segunda.getDescuento(), 0.00);
        verificarMonto("CP-04 sin matrícula", segunda.getMatricula(), 0.00);
        verificarMonto("CP-04 recargo", segunda.getRecargo(), 0.00);
        verificarMonto("CP-04 calcularTotal()", segunda.calcularTotal(), 120.00);

        // CP-05: corporativo + anual + primera vez + Yape
        Gimnasio g3 = nuevoGimnasio();
        Pago p5 = g3.contratarMembresia("70000003", "PA", MetodoPago.YAPE_PLIN, LocalDate.of(2026, 11, 1));
        verificarMonto("CP-05 total", p5.getTotal(), 971.60);
        verificarMonto("CP-05 importe del plan", p5.getMontoPlan(), 1152.00);
        verificarMonto("CP-05 descuento", p5.getDescuento(), 230.40);
        verificarMonto("CP-05 matrícula", p5.getMatricula(), 50.00);
        verificarMonto("CP-05 recargo", p5.getRecargo(), 0.00);
        verificarMonto("CP-05 calcularTotal()", p5.calcularTotal(), 971.60);
    }

    // CP-06, CP-07
    private static void pruebaContratarYRenovar() throws Exception {
        System.out.println("CP-06/07 - Contratar y renovar");
        Gimnasio g = nuevoGimnasio();
        g.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 1));

        esperarExcepcion("CP-06 contratar con membresía vigente", MembresiaActivaException.class,
                () -> g.contratarMembresia("70000001", "PT", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 10)));

        Pago renovacion = g.renovarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 25));
        Membresia nueva = renovacion.getMembresia();
        verificar("CP-07 inicia el 01/12/2026", nueva.getFechaInicio().equals(LocalDate.of(2026, 12, 1)));
        verificar("CP-07 termina el 31/12/2026", nueva.getFechaFin().equals(LocalDate.of(2026, 12, 31)));
        verificar("CP-07 estado PENDIENTE", nueva.getEstado() == EstadoMembresia.PENDIENTE);
        verificarMonto("Renovación sin matrícula", renovacion.getTotal(), 120.00);

        esperarExcepcion("Segunda renovación pendiente", MembresiaActivaException.class,
                () -> g.renovarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 26)));

        // Renovar sin tener nada vigente: empieza hoy
        Pago desdeCero = g.renovarMembresia("70000002", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 25));
        verificar("Renovar sin vigente empieza hoy",
                desdeCero.getMembresia().getFechaInicio().equals(LocalDate.of(2026, 11, 25)));

        esperarExcepcion("Miembro inexistente", MiembroNoEncontradoException.class,
                () -> g.contratarMembresia("99999999", "PM", MetodoPago.EFECTIVO));
        esperarExcepcion("Plan inexistente", DatosInvalidosException.class,
                () -> g.contratarMembresia("70000003", "XX", MetodoPago.EFECTIVO));
        g.buscarPlan("PA").setActivo(false);
        esperarExcepcion("Plan desactivado", DatosInvalidosException.class,
                () -> g.contratarMembresia("70000003", "PA", MetodoPago.EFECTIVO));
        verificar("El pago queda guardado en la lista del gimnasio", g.getPagos().size() == 3);
    }

    // CP-08, CP-09
    private static void pruebaVencimientos() throws Exception {
        System.out.println("CP-08/09 - Vencimientos");

        // CP-08: fin 31/10/2026, hoy 01/11/2026
        Gimnasio g = nuevoGimnasio();
        g.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 10, 1));
        LocalDate hoy = LocalDate.of(2026, 11, 1);
        verificar("CP-08 actualizarEstados devuelve 1", g.actualizarEstadosMembresias(hoy) == 1);
        Membresia m = g.getMembresias().get(0);
        verificar("CP-08 estado VENCIDA", m.getEstado() == EstadoMembresia.VENCIDA);
        verificar("CP-08 aparece en vencidas", g.getMembresiasVencidas(hoy).size() == 1);
        verificar("Segunda actualización no cuenta de nuevo", g.actualizarEstadosMembresias(hoy) == 0);

        // CP-09: vence en 5 días
        Gimnasio g2 = nuevoGimnasio();
        g2.contratarMembresia("70000002", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 1));
        LocalDate hoy2 = LocalDate.of(2026, 11, 25);
        Membresia m2 = g2.getMembresias().get(0);
        verificar("CP-09 quedan 5 días", m2.diasRestantes(hoy2) == 5);
        verificar("CP-09 aparece en próximas a vencer (7 días)", g2.getMembresiasProximasAVencer(7, hoy2).size() == 1);
        verificar("No aparece si N = 3", g2.getMembresiasProximasAVencer(3, hoy2).isEmpty());
        verificar("No aparece en vencidas", g2.getMembresiasVencidas(hoy2).isEmpty());

        // Quien ya renovó no figura como vencido
        Gimnasio g3 = nuevoGimnasio();
        g3.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 1, 1));
        g3.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 6, 1));
        g3.contratarMembresia("70000002", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 1, 1));
        LocalDate hoy3 = LocalDate.of(2026, 6, 5);
        ArrayList<Membresia> vencidas = g3.getMembresiasVencidas(hoy3);
        verificar("Solo figura el miembro que no renovó", vencidas.size() == 1
                                                          && vencidas.get(0).getMiembro().getDni().equals("70000002"));
        verificar("Miembros activos = 1 (el que renovó)", g3.listarMiembrosActivos(hoy3).size() == 1);
    }

    /** True si la fecha de fin no decrece de una membresía a la siguiente. */
    private static boolean ordenadasPorFin(ArrayList<Membresia> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            if (lista.get(i).getFechaFin().isAfter(lista.get(i + 1).getFechaFin())) {
                return false;
            }
        }
        return true;
    }

    /** DNI de los dueños de las membresías, en el orden en que vienen (para comparar el orden). */
    private static String dnis(ArrayList<Membresia> lista) {
        StringBuilder sb = new StringBuilder();
        for (Membresia m : lista) {
            sb.append(m.getMiembro().getDni()).append(' ');
        }
        return sb.toString().trim();
    }

    // HU-05: vencidas y próximas a vencer salen ordenadas por fecha de fin (inserción)
    private static void pruebaOrdenVencimientos() throws Exception {
        System.out.println("HU-05 - Orden de vencimientos");

        // Vencidas contratadas deliberadamente desordenadas (fin: 14/11, 19/10, 04/11)
        Gimnasio g = nuevoGimnasio();
        g.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 10, 15));
        g.contratarMembresia("70000002", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 9, 20));
        g.contratarMembresia("70000003", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 10, 5));
        ArrayList<Membresia> vencidas = g.getMembresiasVencidas(LocalDate.of(2026, 11, 20));
        verificar("Vencidas: aparecen las 3", vencidas.size() == 3);
        verificar("Vencidas: fechas de fin en orden cronológico", ordenadasPorFin(vencidas));
        verificar("Vencidas: orden 19/10, 04/11, 14/11 (obtenido " + dnis(vencidas) + ")",
                dnis(vencidas).equals("70000002 70000003 70000001"));

        // Próximas a vencer contratadas desordenadas (fin: 30/11, 27/11, 29/11; hoy = 25/11)
        Gimnasio g2 = nuevoGimnasio();
        g2.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 11, 1));
        g2.contratarMembresia("70000002", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 10, 28));
        g2.contratarMembresia("70000003", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 10, 30));
        ArrayList<Membresia> proximas = g2.getMembresiasProximasAVencer(7, LocalDate.of(2026, 11, 25));
        verificar("Próximas: aparecen las 3", proximas.size() == 3);
        verificar("Próximas: fechas de fin en orden cronológico", ordenadasPorFin(proximas));
        verificar("Próximas: orden 27/11, 29/11, 30/11 (obtenido " + dnis(proximas) + ")",
                dnis(proximas).equals("70000002 70000003 70000001"));

        // Vencidas con una renovación: la membresía antigua no se repite y el resto sale ordenado
        Gimnasio g3 = nuevoGimnasio();
        g3.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 1, 1));  // fin 31/01 (ya renovó)
        g3.contratarMembresia("70000001", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 6, 1));  // fin 30/06
        g3.contratarMembresia("70000002", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 3, 1));  // fin 31/03
        g3.contratarMembresia("70000003", "PM", MetodoPago.EFECTIVO, LocalDate.of(2026, 2, 1));  // fin 28/02
        ArrayList<Membresia> vencidas3 = g3.getMembresiasVencidas(LocalDate.of(2026, 7, 10));
        verificar("Vencidas con renovación: 3 membresías, sin repetir la antigua", vencidas3.size() == 3);
        verificar("Vencidas con renovación: orden 28/02, 31/03, 30/06 (obtenido " + dnis(vencidas3) + ")",
                dnis(vencidas3).equals("70000003 70000002 70000001"));

        // Sin membresías: listas vacías y sin errores
        Gimnasio vacio = nuevoGimnasio();
        LocalDate hoy = LocalDate.of(2026, 11, 20);
        verificar("Sin membresías: vencidas y próximas vacías",
                vacio.getMembresiasVencidas(hoy).isEmpty() && vacio.getMembresiasProximasAVencer(7, hoy).isEmpty());
    }

    // HU-01 (parte de I1)
    private static void pruebaMiembrosYPlanes() throws Exception {
        System.out.println("HU-01 - Miembros y planes en el gimnasio");
        Gimnasio g = nuevoGimnasio();
        esperarExcepcion("CP-01 DNI de 4 dígitos", DatosInvalidosException.class,
                () -> g.registrarMiembro(new MiembroRegular("1234", "Corto", "Dni", null, null, null)));
        esperarExcepcion("CP-02 DNI duplicado", DatosInvalidosException.class,
                () -> g.registrarMiembro(new MiembroRegular("70000001", "Otro", "Distinto", null, null, null)));
        esperarExcepcion("Buscar DNI inexistente", MiembroNoEncontradoException.class,
                () -> g.buscarMiembro("11111111"));
        verificar("Buscar por nombres y apellidos",
                g.buscarMiembro("ana", "quispe mamani").getDni().equals("70000002"));
        verificar("Listar miembros ordenados por apellido",
                g.listarMiembros().get(0).getApellidos().equals("Flores Vega"));
        esperarExcepcion("Plan con código repetido", DatosInvalidosException.class,
                () -> g.crearPlan(new PlanMensual("PM", 99)));
    }

    private static void pruebaComprobante() throws Exception {
        System.out.println("Comprobante");
        Gimnasio g = nuevoGimnasio();
        Pago p = g.contratarMembresia("70000002", "PT", MetodoPago.TARJETA, LocalDate.of(2026, 11, 1));
        String c = p.generarComprobante();
        verificar("Incluye descuento", c.contains("Descuento  : S/  -48.60  (15 %)"));
        verificar("Incluye matrícula", c.contains("Matrícula  : S/   50.00"));
        verificar("Incluye recargo", c.contains("Recargo    : S/    9.76  (TARJETA 3 %)"));
        verificar("Incluye total", c.contains("TOTAL      : S/  335.16"));
        System.out.println();
        System.out.println(c);
    }
}