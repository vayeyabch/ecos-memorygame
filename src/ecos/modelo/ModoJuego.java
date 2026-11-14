package ecos.modelo;

// Los modos de juego y cómo se escribe cada uno en la columna modo de la tabla partidas.
public enum ModoJuego {

    CLASICO("clasico"),
    PATRONES("patrones"),
    // Multijugador en red (solo da monedas, no entra al ranking).
    RED("red");

    private final String codigo;

    ModoJuego(String codigo) {
        this.codigo = codigo;
    }

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
