package ecos.modelo;

import java.util.Locale; // Para escribir los números con formato fijo.

// Calcula el premio de una partida: premio = 1 / intentos x 100 x dólar del día, y arma el texto para mostrarlo.
// La consultan la ventana de victoria (Isa) y la pantalla de Premios (Allison), así la fórmula solo existe aquí.
// El dólar lo toma de TipoCambio. No guarda nada: solo calcula.
// Responsable: Allison.
public class CalculoPremio {

    // Premio en dólares: 1 / intentos x 100.
    public static double premioDolares(int intentos) {
        return 1.0 / Math.max(1, intentos) * 100; // max evita dividir entre 0.
    }

    // Premio en colones: el premio en dólares x el dólar (venta) del día.
    public static double premioColones(int intentos) {
        return premioDolares(intentos) * TipoCambio.getVenta();
    }

    // Número de colones con separador de miles, sin el símbolo. Ejemplo: "2 320".
    public static String numeroColones(double monto) {
        return String.format(Locale.US, "%,d", Math.round(monto)).replace(',', ' ');
    }

    // Colones con símbolo. Ejemplo: "₡ 2 320".
    public static String colones(double monto) {
        return "₡ " + numeroColones(monto);
    }

    // Dólares con símbolo. Ejemplo: "$4.76".
    public static String dolares(double monto) {
        return "$" + String.format(Locale.US, "%.2f", monto);
    }

    // Texto completo del premio. Ejemplo: "₡ 2 320 ($4.76)".
    public static String textoPremio(int intentos) {
        return colones(premioColones(intentos)) + " (" + dolares(premioDolares(intentos)) + ")";
    }

    // El dólar del día escrito para la pantalla. Ejemplo: "₡ 487,20".
    public static String textoDolar() {
        return "₡ " + String.format(Locale.forLanguageTag("es-CR"), "%.2f", TipoCambio.getVenta());
    }
}