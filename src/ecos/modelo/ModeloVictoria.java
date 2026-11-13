package ecos.modelo;

// Fecha y hora de la partida.
import java.time.LocalDateTime;

// Registra una victoria: calcula el premio (CalculoPremio, con el dólar de compra de TipoCambio) y lo guarda en la base.
// En el clásico la partida y las 15 monedas se guardan juntas (todo o nada). En patrones solo se guarda la partida.
// Sin cuenta (usuario null) no se guarda nada: no hay premio, monedas ni ranking.
// Si la base falla se avisa en la consola, se devuelve null y el juego sigue.
// A cargo de la Parte 2.
public class ModeloVictoria {

    private final RepositorioPartidas repo;

    public ModeloVictoria() {
        this(new RepositorioPartidas());
    }

    public ModeloVictoria(RepositorioPartidas repo) {
        this.repo = repo;
    }

    // Victoria en el modo clásico: guarda la partida y suma las 15 monedas. Puede tardar (base de datos), llamarlo fuera
    // del hilo de la interfaz. Devuelve la partida guardada, o null si no hay cuenta o no se pudo guardar.
    public Partida registrarClasico(String usuario, int intentos) {
        return registrar(usuario, ModoJuego.CLASICO, intentos);
    }

    // Partida del multijugador en red: se guarda siempre (queda en el historial) pero sin premio, así no entra al ranking.
    // Solo quien gana recibe las monedas. Puede tardar (base de datos), llamarlo fuera del hilo de la interfaz. Devuelve la
    // partida guardada, o null si no hay cuenta o no se pudo guardar.
    public Partida registrarRed(String usuario, int intentos, boolean gano) {
        if (usuario == null || usuario.isBlank()) {
            return null;
        }
        // La base exige intentos mayores que 0.
        Partida partida = new Partida(usuario, ModoJuego.RED, Math.max(1, intentos), 0, 0, TipoCambio.getCompra(),
                LocalDateTime.now());
        try {
            boolean guardada = repo.guardarVictoriaConMonedas(partida, gano ? ModeloMonedas.MONEDAS_POR_VICTORIA : 0);
            return guardada ? partida : null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Partida registrar(String usuario, ModoJuego modo, int intentos) {
        // Sin cuenta no se guarda nada.
        if (usuario == null || usuario.isBlank()) {
            return null;
        }
        // Premio con la fórmula del equipo y el dólar de compra de ese momento.
        Partida partida = new Partida(usuario, modo, intentos, CalculoPremio.premioDolares(intentos),
                CalculoPremio.premioColones(intentos), TipoCambio.getCompra(), LocalDateTime.now());
        try {
            boolean guardada = repo.guardarVictoriaConMonedas(partida, ModeloMonedas.MONEDAS_POR_VICTORIA);
            return guardada ? partida : null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}