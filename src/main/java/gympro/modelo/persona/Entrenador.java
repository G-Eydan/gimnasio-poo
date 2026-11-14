package gympro.modelo.persona;

import java.time.LocalDate;

public class Entrenador extends Persona
{
    private String especialidad;
    private static final int MAX_MIEMBROS = 10;
    private boolean[][] disponibilidad; // [7 días][3 turnos]

    public Entrenador(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String email, String telefono, String especialidad)
    {
        super(dni, nombres, apellidos, fechaNacimiento, email, telefono);
        this.especialidad = especialidad;
        this.disponibilidad = new boolean[7][3];
    }

    @Override
    public String getRol()
    {
        return "ENTRENADOR";
    }
}