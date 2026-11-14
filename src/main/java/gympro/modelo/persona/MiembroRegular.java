package gympro.modelo.persona;

import java.time.LocalDate;

public class MiembroRegular extends Miembro implements Comparable<Miembro>
{
    public MiembroRegular(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String email, String telefono, String codigo)
    {
        super(dni, nombres, apellidos, fechaNacimiento, email, telefono, codigo);
    }

    @Override
    public int compareTo(Miembro otro)
    {
        return this.dni.compareTo(otro.getDni());
    }
}