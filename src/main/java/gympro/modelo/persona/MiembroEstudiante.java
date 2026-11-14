package gympro.modelo.persona;

import java.time.LocalDate;

public class MiembroEstudiante extends Miembro implements Descontable
{
    public MiembroEstudiante(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String email, String telefono, String codigo)
    {
        super(dni, nombres, apellidos, fechaNacimiento, email, telefono, codigo);
    }

    @Override
    public double getPorcentajeDescuento()
    {
        return 0.15;
    }
}