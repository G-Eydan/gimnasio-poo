package gympro.modelo.entrenamiento;

import gympro.excepciones.DatosInvalidosException;
import gympro.excepciones.RutinaLlenaException;
import gympro.modelo.enums.DiaSemana;
import gympro.modelo.enums.Objetivo;
import gympro.modelo.persona.Entrenador;
import gympro.modelo.persona.Miembro;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Rutina de entrenamiento de un miembro. Guarda sus ejercicios en un <b>arreglo
 * estándar parcialmente lleno</b> de hasta {@link #MAX_EJERCICIOS} elementos: solo las
 * posiciones {@code 0} a {@code numDetalles - 1} están en uso (RN-09).
 *
 * <p>Una rutina nace activa; cuando el miembro recibe otra, la anterior se archiva
 * ({@link #archivar()}) y queda en su historial (RN-10).</p>
 *
 * @author I3
 */
public class Rutina implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int MAX_EJERCICIOS = 20;

    private String codigo;
    private String nombre;
    private Objetivo objetivo;
    private final Miembro miembro;
    private final Entrenador entrenador;
    private final LocalDate fechaCreacion;
    private boolean activa;
    private final DetalleRutina[] detalles = new DetalleRutina[MAX_EJERCICIOS];
    private int numDetalles;

    /**
     * @param miembro    dueño de la rutina (puede ser {@code null} en pruebas)
     * @param entrenador entrenador que la creó, o {@code null} si la creó el administrador
     * @throws DatosInvalidosException si el nombre está vacío o falta el objetivo
     */
    public Rutina(String codigo, String nombre, Objetivo objetivo, Miembro miembro, Entrenador entrenador)
            throws DatosInvalidosException
    {
        if(nombre == null || nombre.isBlank()) throw new DatosInvalidosException("La rutina necesita un nombre.");
        if(objetivo == null) throw new DatosInvalidosException("Debe elegir el objetivo de la rutina.");
        this.codigo = codigo;
        this.nombre = nombre.trim();
        this.objetivo = objetivo;
        this.miembro = miembro;
        this.entrenador = entrenador;
        this.fechaCreacion = LocalDate.now();
        this.activa = true;
        this.numDetalles = 0;
    }

    /**
     * Agrega un ejercicio al final de la rutina.
     *
     * @throws RutinaLlenaException si ya tiene {@link #MAX_EJERCICIOS} ejercicios
     */
    public void agregarDetalle(DetalleRutina detalle) throws RutinaLlenaException
    {
        if(estaLlena())
            throw new RutinaLlenaException("La rutina " + codigo + " ya tiene " + MAX_EJERCICIOS + " ejercicios.");
        detalles[numDetalles++] = detalle;
    }

    /**
     * Crea el detalle (validado con RN-09) y lo agrega al final.
     *
     * @return esta rutina, para encadenar llamadas
     * @throws RutinaLlenaException    si ya tiene {@link #MAX_EJERCICIOS} ejercicios
     * @throws DatosInvalidosException si las series, repeticiones o el peso están fuera de rango
     */
    public Rutina agregarEjercicio(Ejercicio e, DiaSemana dia, int series, int reps)
            throws RutinaLlenaException, DatosInvalidosException
    {
        return agregarEjercicio(e, dia, series, reps, 0.0, 0);
    }

    /** Igual que {@link #agregarEjercicio(Ejercicio, DiaSemana, int, int)} con peso y descanso. */
    public Rutina agregarEjercicio(Ejercicio e, DiaSemana dia, int series, int reps, double peso, int descansoSeg)
            throws RutinaLlenaException, DatosInvalidosException
    {
        if(estaLlena())
            throw new RutinaLlenaException("La rutina " + codigo + " ya tiene " + MAX_EJERCICIOS + " ejercicios.");
        agregarDetalle(new DetalleRutina(e, dia, series, reps, peso, descansoSeg));
        return this;
    }

    /** @return true si ya no se pueden agregar ejercicios */
    public boolean estaLlena()
    {
        return numDetalles == MAX_EJERCICIOS;
    }

    /**
     * Elimina el ejercicio de la posición indicada y desplaza los siguientes
     * una posición a la izquierda para no dejar huecos.
     *
     * @return false si el índice está fuera de rango
     */
    public boolean eliminarDetalle(int indice)
    {
        if(indice < 0 || indice >= numDetalles) return false;
        for(int i = indice; i < numDetalles - 1; i++) detalles[i] = detalles[i + 1];
        detalles[--numDetalles] = null;
        return true;
    }

    /**
     * Ordena los ejercicios de lunes a domingo con el <b>método de inserción</b>.
     * Es estable: los ejercicios del mismo día conservan su orden.
     */
    public void ordenarPorDia()
    {
        for(int i = 1; i < numDetalles; i++)
        {
            DetalleRutina actual = detalles[i];
            int j = i - 1;
            while(j >= 0 && detalles[j].getDia().compareTo(actual.getDia()) > 0)
            {
                detalles[j + 1] = detalles[j];
                j--;
            }
            detalles[j + 1] = actual;
        }
    }

    /**
     * Búsqueda lineal por nombre del ejercicio, sin distinguir mayúsculas.
     *
     * @return la posición del primer ejercicio con ese nombre, o −1 si no está
     */
    public int buscarEjercicio(String nombreEjercicio)
    {
        if(nombreEjercicio == null) return -1;
        for(int i = 0; i < numDetalles; i++)
        {
            if(detalles[i].getEjercicio().getNombre().equalsIgnoreCase(nombreEjercicio.trim())) return i;
        }
        return -1;
    }

    /** Marca la rutina como no activa (pasa al historial del miembro, RN-10). */
    public void archivar()
    {
        this.activa = false;
    }

    /** @return los ejercicios de ese día, en el orden en que se agregaron */
    public DetalleRutina[] getDetallesPorDia(DiaSemana dia)
    {
        int cuenta = 0;
        for(int i = 0; i < numDetalles; i++) if(detalles[i].getDia() == dia) cuenta++;
        DetalleRutina[] res = new DetalleRutina[cuenta];
        int k = 0;
        for(int i = 0; i < numDetalles; i++) if(detalles[i].getDia() == dia) res[k++] = detalles[i];
        return res;
    }

    /** @return suma de las series de todos los ejercicios */
    public int getTotalSeries()
    {
        int total = 0;
        for(int i = 0; i < numDetalles; i++) total += detalles[i].getSeries();
        return total;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getNombre()
    {
        return nombre;
    }

    public Objetivo getObjetivo()
    {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo)
    {
        this.objetivo = objetivo;
    }

    public Miembro getMiembro()
    {
        return miembro;
    }

    public Entrenador getEntrenador()
    {
        return entrenador;
    }

    public LocalDate getFechaCreacion()
    {
        return fechaCreacion;
    }

    public boolean isActiva()
    {
        return activa;
    }

    /** @return copia de los ejercicios en uso (sin las posiciones vacías) */
    public DetalleRutina[] getDetalles()
    {
        return Arrays.copyOf(detalles, numDetalles);
    }

    /** @return cantidad de ejercicios en la rutina */
    public int getNumDetalles()
    {
        return numDetalles;
    }

    @Override
    public String toString()
    {
        return codigo + " | " + nombre + " | " + objetivo + " | " + numDetalles + " ejercicio(s)"
                + (activa ? "" : " | archivada");
    }
}
