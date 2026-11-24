package gympro.gui;

/**
 * Panel que recarga sus datos desde {@code Gimnasio} cuando se selecciona su pestaña
 * (HG-01). Así, un miembro registrado en «Miembros» aparece en las demás pestañas
 * sin reiniciar el programa.
 *
 * @author I2
 */
public interface Actualizable
{
    /** Vuelve a leer los datos del servicio y repinta el panel. */
    void actualizar();
}
