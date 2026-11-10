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

    // Victoria en el modo patrones: guarda la partida (el nivel cuenta como los intentos). Mismas reglas de arriba.
    public Partida registrarPatrones(String usuario, int intentos) {
        return registrar(usuario, ModoJuego.PATRONES, intentos);
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
            boolean guardada = modo == ModoJuego.CLASICO
                    ? repo.guardarVictoriaConMonedas(partida, ModeloMonedas.MONEDAS_POR_VICTORIA)
                    : repo.guardarVictoria(partida);
            return guardada ? partida : null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
