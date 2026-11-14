package gympro.servicio;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.MembresiaActivaException;
import gympro.excepciones.MiembroNoEncontradoException;
import gympro.modelo.enums.EstadoMembresia;
import gympro.modelo.enums.MetodoPago;
import gympro.modelo.membresia.Membresia;
import gympro.modelo.membresia.Pago;
import gympro.modelo.membresia.Plan;
import gympro.modelo.persona.Miembro;

/**
 * Clase central (fachada) del sistema: guarda miembros, planes, membresías y pagos,
 * y aplica las reglas de negocio de contratación, renovación y vencimiento.
 *
 * <p>Los métodos que dependen de la fecha tienen una versión con {@code hoy} como
 * parámetro (para poder probarlos con fechas fijas) y otra que usa
 * {@link LocalDate#now()}.</p>
 *
 *
 * @author I1
 */
public class Gimnasio implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String nombre;
    private String ruc;
    private String direccion;

    /** Clave: DNI. */
    private final HashMap<String, Miembro> miembros = new HashMap<>();
    private final ArrayList<Plan> planes = new ArrayList<>();
    private final ArrayList<Membresia> membresias = new ArrayList<>();
    private final ArrayList<Pago> pagos = new ArrayList<>();

    // Contadores de códigos (de instancia, para que se guarden con el respaldo .dat)
    private int contadorMembresias = 0;
    private int contadorPagos = 0;

    public Gimnasio(String nombre, String ruc, String direccion) {
        this.nombre = nombre;
        this.ruc = ruc;
        this.direccion = direccion;
    }

    public Gimnasio(String nombre) {
        this(nombre, "", "");
    }

    // ==================================================================
    // Miembros (HU-01)
    // ==================================================================

    /**
     * Registra un miembro nuevo.
     *
     * @throws DatosInvalidosException si el miembro es nulo o el DNI ya existe
     */
    public void registrarMiembro(Miembro miembro) throws DatosInvalidosException {
        if (miembro == null) {
            throw new DatosInvalidosException("El miembro no puede ser nulo.");
        }
        if (miembros.containsKey(miembro.getDni())) {
            throw new DatosInvalidosException("DNI duplicado: " + miembro.getDni());
        }
        miembros.put(miembro.getDni(), miembro);
    }

    /** Busca un miembro por DNI. */
    public Miembro buscarMiembro(String dni) throws MiembroNoEncontradoException {
        Miembro m = miembros.get(dni);
        if (m == null) {
            throw new MiembroNoEncontradoException("No existe un miembro con DNI " + dni);
        }
        return m;
    }

    /** Sobrecarga: busca un miembro por nombres y apellidos exactos (sin distinguir mayúsculas). */
    public Miembro buscarMiembro(String nombres, String apellidos) throws MiembroNoEncontradoException {
        for (Miembro m : miembros.values()) {
            if (m.getNombres().equalsIgnoreCase(nombres) && m.getApellidos().equalsIgnoreCase(apellidos)) {
                return m;
            }
        }
        throw new MiembroNoEncontradoException("No existe un miembro llamado " + nombres + " " + apellidos);
    }

    /** Todos los miembros, ordenados por apellidos. */
    public ArrayList<Miembro> listarMiembros() {
        ArrayList<Miembro> lista = new ArrayList<>(miembros.values());
        Collections.sort(lista);
        return lista;
    }

    /** Miembros con una membresía vigente hoy. */
    public ArrayList<Miembro> listarMiembrosActivos() {
        return listarMiembrosActivos(LocalDate.now());
    }

    public ArrayList<Miembro> listarMiembrosActivos(LocalDate hoy) {
        ArrayList<Miembro> lista = new ArrayList<>();
        for (Miembro m : listarMiembros()) {
            if (tieneMembresiaVigente(m, hoy)) {
                lista.add(m);
            }
        }
        return lista;
    }

    // ==================================================================
    // Planes (HU-04)
    // ==================================================================

    /** Agrega un plan al catálogo. El código no puede repetirse. */
    public void crearPlan(Plan plan) throws DatosInvalidosException {
        if (plan == null) {
            throw new DatosInvalidosException("El plan no puede ser nulo.");
        }
        if (buscarPlan(plan.getCodigo()) != null) {
            throw new DatosInvalidosException("Ya existe un plan con el código " + plan.getCodigo());
        }
        planes.add(plan);
    }

    /** Busca un plan por código; devuelve {@code null} si no existe. */
    public Plan buscarPlan(String codigo) {
        for (Plan p : planes) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }

    /** Copia de la lista de planes (cada plan muestra su importe en {@code toString()}). */
    public ArrayList<Plan> listarPlanes() {
        return new ArrayList<>(planes);
    }

    // ==================================================================
    // Contratar y renovar (HU-02)
    // ==================================================================

    /**
     * Vista previa del total que pagaría el miembro, sin registrar nada.
     * Considera si es su primera membresía (matrícula).
     */
    public double calcularImporte(Miembro miembro, Plan plan, MetodoPago metodo) {
        return Pago.previsualizarTotal(miembro, plan, metodo, miembro.esPrimeraMembresia());
    }

    /** Contrata una membresía que empieza hoy y registra su pago. */
    public Pago contratarMembresia(String dni, String codPlan, MetodoPago metodo)
            throws MiembroNoEncontradoException, MembresiaActivaException, DatosInvalidosException {
        return contratarMembresia(dni, codPlan, metodo, LocalDate.now());
    }

    /**
     * Contrata una membresía que empieza en {@code hoy}.
     *
     * @throws MembresiaActivaException si el miembro ya tiene una membresía vigente (debe usar renovar)
     * @throws DatosInvalidosException  si el plan no existe, está inactivo o faltan datos
     */
    public Pago contratarMembresia(String dni, String codPlan, MetodoPago metodo, LocalDate hoy)
            throws MiembroNoEncontradoException, MembresiaActivaException, DatosInvalidosException {
        Miembro miembro = buscarMiembro(dni);
        Plan plan = obtenerPlanActivo(codPlan);
        validarMetodoYFecha(metodo, hoy);

        for (Membresia m : membresiasDe(miembro)) {
            if (m.estaVigente(hoy)) {
                throw new MembresiaActivaException("El miembro " + miembro.getNombreCompleto()
                        + " ya tiene una membresía vigente (vence el " + FORMATO.format(m.getFechaFin())
                        + "). Use la opción renovar.");
            }
        }
        return registrar(miembro, plan, metodo, hoy, hoy);
    }

    /** Renueva la membresía del miembro (la nueva empieza al terminar la vigente). */
    public Pago renovarMembresia(String dni, String codPlan, MetodoPago metodo)
            throws MiembroNoEncontradoException, MembresiaActivaException, DatosInvalidosException {
        return renovarMembresia(dni, codPlan, metodo, LocalDate.now());
    }

    /**
     * Renueva en la fecha {@code hoy}. Si el miembro tiene una membresía vigente, la nueva
     * empieza el día siguiente a su fin (queda PENDIENTE) para no perder días (RN-06);
     * si no tiene ninguna vigente, empieza hoy.
     *
     * @throws MembresiaActivaException si ya tiene una renovación pendiente
     */
    public Pago renovarMembresia(String dni, String codPlan, MetodoPago metodo, LocalDate hoy)
            throws MiembroNoEncontradoException, MembresiaActivaException, DatosInvalidosException {
        Miembro miembro = buscarMiembro(dni);
        Plan plan = obtenerPlanActivo(codPlan);
        validarMetodoYFecha(metodo, hoy);

        LocalDate inicio = hoy;
        for (Membresia m : membresiasDe(miembro)) {
            if (!m.estaVigente(hoy)) {
                continue;
            }
            if (hoy.isBefore(m.getFechaInicio())) {
                throw new MembresiaActivaException("El miembro " + miembro.getNombreCompleto()
                        + " ya tiene una renovación pendiente que inicia el "
                        + FORMATO.format(m.getFechaInicio()) + ".");
            }
            LocalDate diaSiguiente = m.getFechaFin().plusDays(1);
            if (diaSiguiente.isAfter(inicio)) {
                inicio = diaSiguiente;
            }
        }
        return registrar(miembro, plan, metodo, inicio, hoy);
    }

    // ==================================================================
    // Vencimientos (HU-05)
    // ==================================================================

    /** Actualiza el estado de todas las membresías con la fecha de hoy. Devuelve cuántas vencieron ahora. */
    public int actualizarEstadosMembresias() {
        return actualizarEstadosMembresias(LocalDate.now());
    }

    public int actualizarEstadosMembresias(LocalDate hoy) {
        int nuevasVencidas = 0;
        for (Membresia m : membresias) {
            EstadoMembresia antes = m.getEstado();
            m.actualizarEstado(hoy);
            if (antes != EstadoMembresia.VENCIDA && m.getEstado() == EstadoMembresia.VENCIDA) {
                nuevasVencidas++;
            }
        }
        return nuevasVencidas;
    }

    /**
     * Membresías vencidas, ordenadas por fecha de fin. Solo cuenta la última membresía de
     * cada miembro: si ya renovó, su membresía antigua no aparece como vencida.
     * (Este orden se puede reemplazar por {@code Ordenamiento.ordenarPorVencimiento}, inserción.)
     */
    public ArrayList<Membresia> getMembresiasVencidas() {
        return getMembresiasVencidas(LocalDate.now());
    }

    public ArrayList<Membresia> getMembresiasVencidas(LocalDate hoy) {
        ArrayList<Membresia> lista = new ArrayList<>();
        for (Membresia m : membresias) {
            if (m.estaVencida(hoy) && esUltimaDelMiembro(m)) {
                lista.add(m);
            }
        }
        Collections.sort(lista);
        return lista;
    }

    /** Membresías que vencen en los próximos {@code dias} días (por defecto se usan 7). */
    public ArrayList<Membresia> getMembresiasProximasAVencer(int dias) {
        return getMembresiasProximasAVencer(dias, LocalDate.now());
    }

    public ArrayList<Membresia> getMembresiasProximasAVencer(int dias, LocalDate hoy) {
        ArrayList<Membresia> lista = new ArrayList<>();
        for (Membresia m : membresias) {
            if (m.venceEnLosProximos(dias, hoy)) {
                lista.add(m);
            }
        }
        Collections.sort(lista);
        return lista;
    }

    // ==================================================================
    // Consultas (copias defensivas)
    // ==================================================================

    public ArrayList<Membresia> getMembresias() { return new ArrayList<>(membresias); }
    public ArrayList<Pago> getPagos() { return new ArrayList<>(pagos); }

    public String getNombre() { return nombre; }
    public String getRuc() { return ruc; }
    public String getDireccion() { return direccion; }

    // ==================================================================
    // Métodos privados de apoyo
    // ==================================================================

    /** Crea la membresía y su pago, y los guarda. Se llama solo cuando todas las validaciones pasaron. */
    private Pago registrar(Miembro miembro, Plan plan, MetodoPago metodo, LocalDate inicio, LocalDate hoy) {
        boolean primeraVez = miembro.esPrimeraMembresia();

        Membresia membresia = new Membresia(String.format("MB%04d", contadorMembresias + 1), miembro, plan, inicio);
        membresia.actualizarEstado(hoy);
        Pago pago = new Pago(String.format("P%04d", contadorPagos + 1), membresia, metodo, primeraVez, hoy);
        membresia.setPago(pago);

        contadorMembresias++;
        contadorPagos++;
        membresias.add(membresia);
        pagos.add(pago);
        miembro.agregarMembresia(membresia);
        return pago;
    }

    private Plan obtenerPlanActivo(String codPlan) throws DatosInvalidosException {
        Plan plan = buscarPlan(codPlan);
        if (plan == null) {
            throw new DatosInvalidosException("No existe el plan " + codPlan);
        }
        if (!plan.isActivo()) {
            throw new DatosInvalidosException("El plan " + plan.getNombre() + " está desactivado.");
        }
        return plan;
    }

    private void validarMetodoYFecha(MetodoPago metodo, LocalDate hoy) throws DatosInvalidosException {
        if (metodo == null) {
            throw new DatosInvalidosException("Debe elegir un método de pago.");
        }
        if (hoy == null) {
            throw new DatosInvalidosException("La fecha no puede ser nula.");
        }
    }

    private ArrayList<Membresia> membresiasDe(Miembro miembro) {
        ArrayList<Membresia> lista = new ArrayList<>();
        for (Membresia m : membresias) {
            if (m.getMiembro().equals(miembro)) {
                lista.add(m);
            }
        }
        return lista;
    }

    private boolean tieneMembresiaVigente(Miembro miembro, LocalDate hoy) {
        for (Membresia m : membresiasDe(miembro)) {
            if (m.estaVigente(hoy)) {
                return true;
            }
        }
        return false;
    }

    /** True si no hay otra membresía (no cancelada) del mismo miembro que termine después. */
    private boolean esUltimaDelMiembro(Membresia m) {
        for (Membresia otra : membresias) {
            if (otra != m
                    && otra.getMiembro().equals(m.getMiembro())
                    && otra.getEstado() != EstadoMembresia.CANCELADA
                    && otra.getFechaFin().isAfter(m.getFechaFin())) {
                return false;
            }
        }
        return true;
    }
}
