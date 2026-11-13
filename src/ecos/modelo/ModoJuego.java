package ecos.modelo;

// Los modos de juego. Cada uno sabe cómo se escribe en la columna modo de la tabla partidas.
// A cargo de la Parte 2.
public enum ModoJuego {

    CLASICO("clasico"),
    PATRONES("patrones"),
    // Multijugador en red (solo da monedas, no entra al ranking).
    RED("red");

    // Texto que se guarda en la base de datos.
    private final String codigo;

    ModoJuego(String codigo) {
        this.codigo = codigo;
    }

    // Texto para la base de datos ("clasico", "patrones" o "red").
    public String getCodigo() {
        return codigo;
    }

    // Convierte el texto de la base en el modo. Si no lo conoce devuelve CLASICO.
    public static ModoJuego deCodigo(String codigo) {
        for (ModoJuego m : values()) {
            if (m.codigo.equalsIgnoreCase(codigo)) {
                return m;
            }
        }
        return CLASICO;
    }
}
