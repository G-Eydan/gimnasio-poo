package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import java.time.LocalDate;

public class MiembroEstudiante extends Miembro
{
    private static final long serialVersionUID = 1L;

    private String universidad;
    private String codigoUniversitario;

    public MiembroEstudiante(String dni, String nombres, String apellidos) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos);
    }

    public MiembroEstudiante(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email,
                             String universidad, String codigoUniversitario) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos, fechaNacimiento, telefono, email);
        this.universidad = universidad;
        this.codigoUniversitario = codigoUniversitario;
    }

    @Override
    public String getTipo()
    {
        return "ESTUDIANTE";
    }

    @Override
    public double getPorcentajeDescuento()
    {
        return 0.15;
    }

    public String getUniversidad()
    {
        return universidad;
    }

    public void setUniversidad(String universidad)
    {
        this.universidad = universidad;
    }

    public String getCodigoUniversitario()
    {
        return codigoUniversitario;
    }

    public void setCodigoUniversitario(String codigoUniversitario)
    {
        this.codigoUniversitario = codigoUniversitario;
    }
}