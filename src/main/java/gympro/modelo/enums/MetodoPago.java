package gympro.modelo.enums;

/**
 * Métodos de pago aceptados. Cada uno tiene un recargo sobre el monto
 * (por ejemplo, 0.03 = 3 % para pagos con tarjeta).
 */
public enum MetodoPago
{
    EFECTIVO(0.0),
    YAPE_PLIN(0.0),
    TRANSFERENCIA(0.0),
    TARJETA(0.03);

    private final double recargo;

    MetodoPago(double recargo)
    {
        this.recargo = recargo;
    }

    /**
     * @return recargo como fracción del monto (0.03 = 3 %)
     */
    public double getRecargo()
    {
        return recargo;
    }
}
