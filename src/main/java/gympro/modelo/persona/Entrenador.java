package gympro.modelo.persona;

public class Entrenador extends Persona
{
    private String turno;
    private int cupoMaximo;

    public Entrenador(String dni, String nombre, String email, String telefono, String turno, int cupoMaximo)
    {
        super(dni, nombre, email, telefono);
        this.turno = turno;
        this.cupoMaximo = cupoMaximo;
    }

    public String getTurno()
    {
        return turno;
    }

    public void setTurno(String turno)
    {
        this.turno = turno;
    }

    public int getCupoMaximo()
    {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo)
    {
        this.cupoMaximo = cupoMaximo;
    }
}