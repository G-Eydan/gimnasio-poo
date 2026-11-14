package gympro.modelo.membresia;

/**
 * Para las clases que aparecen como una línea en los reportes de texto.
 * La implementan Plan y Pago.
 */
public interface Reportable
{
    /**
     * @return una línea de texto con los datos del objeto para un reporte
     */
    String generarLineaReporte();
}
