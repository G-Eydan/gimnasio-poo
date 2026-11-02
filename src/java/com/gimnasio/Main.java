package com.gimnasio;

import com.gimnasio.modelo.Entrenador;
import com.gimnasio.modelo.Persona;
import com.gimnasio.modelo.Socio;

public class Main
{
    public static void main(String[] args)
    {
        Persona persona1 = new Socio(1, "Ana Torres", "70123456", "987654321");
        Persona persona2 = new Entrenador(2, "Luis Rojas", "45678912", "912345678", "Crossfit", 2500);

        System.out.println(persona1);
        System.out.println(persona2);
    }
}
