package gympro.modelo.persona;

/**
 * Para las clases que tienen un descuento sobre el precio de un plan.
 * La implementan las subclases de Miembro (regular, estudiante, corporativo).
 */
public interface Descontable
{
    /**
     * @return descuento como fracción del precio (0.15 = 15 %)
     */
    double getPorcentajeDescuento();
}
