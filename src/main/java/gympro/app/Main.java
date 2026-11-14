package gympro.app;

import gympro.servicio.Gimnasio;

/**
 * Punto de entrada de GymPro. En el Sprint 1 abre el menú de consola.
 *
 * @author I2
 */
public class Main
{
    public static void main(String[] args)
    {
        Gimnasio gimnasio = new Gimnasio("GymPro");
        new MenuConsola(gimnasio).iniciar();
    }
}
