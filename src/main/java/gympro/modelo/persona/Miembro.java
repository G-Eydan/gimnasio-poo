package gympro.modelo.persona;

import gympro.modelo.membresia.Membresia;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class Miembro extends Persona {
    private String codigo;
    private List<Membresia> historialMembresias = new ArrayList<>();

    public Miembro(String dni, String nombre, String email, String telefono, String codigo) {
        super(dni, nombre, email, telefono);
        this.codigo = codigo;
    }

    public void agregarMembresia(Membresia m) {
        this.historialMembresias.add(m);
    }

    public List<Membresia> getHistorialMembresias() {
        return historialMembresias;
    }

    public Membresia getMembresiaActual() {
        if (historialMembresias.isEmpty()) return null;
        return historialMembresias.get(historialMembresias.size() - 1);
    }

    public long getDiasRestantes() {
        Membresia actual = getMembresiaActual();
        if (actual == null || LocalDate.now().isAfter(actual.getFechaFin())) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), actual.getFechaFin());
    }

    public String getEstadoInscripcion() {
        Membresia actual = getMembresiaActual();
        if (actual == null) return "SIN_MEMBRESIA";

        long dias = getDiasRestantes();
        if (LocalDate.now().isAfter(actual.getFechaFin())) {
            return "VENCIDO";
        } else if (dias <= 7) {
            return "POR_VENCER";
        }
        return "ACTIVO";
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}