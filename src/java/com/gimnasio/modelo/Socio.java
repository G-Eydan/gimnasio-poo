package com.gimnasio.modelo;

import java.time.LocalDate;

public class Socio extends Persona
{
    private LocalDate fechaRegistro;

    public Socio(int id, String nombre, String dni, String telefono)
    {
        super(id, nombre, dni, telefono);
        this.fechaRegistro = LocalDate.now();
    }

    public LocalDate getFechaRegistro()
    {
        return fechaRegistro;
    }

    @Override
    public String getRol()
    {
        return "SOCIO";
    }
}