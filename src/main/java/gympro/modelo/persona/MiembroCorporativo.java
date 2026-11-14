package gympro.modelo.persona;

import gympro.excepciones.DatosInvalidosException;
import java.time.LocalDate;

public class MiembroCorporativo extends Miembro
{
    private static final long serialVersionUID = 1L;

    private String empresa;
    private String ruc;

    public MiembroCorporativo(String dni, String nombres, String apellidos, LocalDate fechaNacimiento, String telefono, String email,
                              String empresa, String ruc) throws DatosInvalidosException
    {
        super(dni, nombres, apellidos, fechaNacimiento, telefono, email);
        this.empresa = empresa;
        this.ruc = ruc;
    }

    @Override
    public String getTipo()
    {
        return "CORPORATIVO";
    }

    @Override
    public double getPorcentajeDescuento()
    {
        return 0.20;
    }

    public String getEmpresa()
    {
        return empresa;
    }

    public void setEmpresa(String empresa)
    {
        this.empresa = empresa;
    }

    public String getRuc()
    {
        return ruc;
    }

    public void setRuc(String ruc)
    {
        this.ruc = ruc;
    }
}