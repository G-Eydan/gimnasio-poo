package gympro.modelo.persona;

import java.time.LocalDate;

public class MiembroCorporativo extends Miembro
{
    public MiembroCorporativo(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String email, String telefono, String codigo)
    {
        super(dni, nombres, apellidos, fechaNacimiento, email, telefono, codigo);
    }
}