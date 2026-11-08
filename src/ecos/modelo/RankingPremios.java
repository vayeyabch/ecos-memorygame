package ecos.modelo;

// Modelo de PantallaPremios, los datos del top 5 de jugadores. El premio de cada uno lo calcula CalculoPremio.
// Por ahora son datos de ejemplo, guardar quién ganó necesita base de datos y no entra esta semana.
// A cargo de Allison.
public class RankingPremios {

    // Los 5 mejores (ejemplo). Intentos de cada uno (ejemplo), de menor a mayor.
    private static final String[] NOMBRES = {"Valeria", "Mateo", "Sofía", "Dani", "Kenneth"};
    private static final int[] INTENTOS = {21, 24, 27, 31, 36};

    // Cuántos puestos tiene el ranking.
    public int cantidad() {
        return NOMBRES.length;
    }

    // Nombre del jugador del puesto i (0 = primero).
    public String nombre(int i) {
        return NOMBRES[i];
    }

    // Intentos del jugador del puesto i.
    public int intentos(int i) {
        return INTENTOS[i];
    }

    // Premio en colones del puesto i, sin símbolo. Por ejemplo "2 320".
    public String colones(int i) {
        return CalculoPremio.numeroColones(CalculoPremio.premioColones(INTENTOS[i]));
    }

    // Premio en dólares del puesto i. Por ejemplo "$4.76".
    public String dolares(int i) {
        return CalculoPremio.dolares(CalculoPremio.premioDolares(INTENTOS[i]));
    }

    // El dólar del día para mostrar arriba de la tabla.
    public String dolarDelDia() {
        return CalculoPremio.textoDolar();
    }
} 
