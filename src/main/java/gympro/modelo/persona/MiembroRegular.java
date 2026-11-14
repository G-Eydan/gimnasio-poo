package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import java.time.LocalDate;

public class MiembroRegular extends Miembro
{
    private static final long serialVersionUID = 1L;

    public MiembroRegular(String dni, String nombres, String apellidos) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos);
    }

    public MiembroRegular(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos, fechaNacimiento, telefono, email);
    }

    @Override
    public String getTipo()
    {
        return "REGULAR";
    }

    @Override
    public double getPorcentajeDescuento()
    {
        return 0.0;
    }
}