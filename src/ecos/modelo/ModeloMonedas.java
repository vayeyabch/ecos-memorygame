package ecos.modelo;

// Modelo de las monedas, cada cuenta acumula monedas al ganar y las gasta en el Ojo espía. Se guardan en la columna
// monedas de la tabla usuarios, así no se pierden al cerrar el programa.
// Las monedas NO afectan el ranking ni el premio en dinero, eso sale solo de los intentos de la partida.
// Un jugador sin cuenta (usuario null) siempre tiene 0 monedas y no se le guarda nada.
// Si la base falla se avisa en la consola y el juego sigue (0 monedas, no se cobra nada).
// A cargo de Hellen.
public class ModeloMonedas {

    // Lo que gana quien gana una partida. Lo que cuesta el Ojo espía.
    public static final int MONEDAS_POR_VICTORIA = 15;
    public static final int PRECIO_OJO = 30;

    // De aquí se leen y se guardan las monedas.
    private final RepositorioUsuarios repo;

    public ModeloMonedas() {
        this(new RepositorioUsuarios());
    }

    public ModeloMonedas(RepositorioUsuarios repo) {
        this.repo = repo;
    }

    // Monedas del usuario (0 si no tiene cuenta, es nuevo o la base no responde).
    public int monedas(String usuario) {
        if (usuario == null) {
            return 0;
        }
        try {
            return repo.monedas(usuario);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Dice si le alcanza para el Ojo espía.
    public boolean alcanzaParaOjo(String usuario) {
        return monedas(usuario) >= PRECIO_OJO;
    }

    // Cobra el Ojo espía; false si no le alcanza (o la base no responde).
    public boolean comprarOjo(String usuario) {
        if (usuario == null) {
            return false;
        }
        try {
            return repo.cobrarMonedas(usuario, PRECIO_OJO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Regla de las monedas para la pantalla de reglas.
    public String reglaMonedas() {
        return "Cada victoria te da <b>" + MONEDAS_POR_VICTORIA + " monedas</b>. Con <b>" + PRECIO_OJO + "</b> comprás el Ojo espía. Sin cuenta no hay premio ni monedas.";
    }
}
