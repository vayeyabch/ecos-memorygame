package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;
import ecos.modelo.ModeloMonedas;
import ecos.modelo.RankingPremios;
import ecos.modelo.TipoCambio;
import ecos.pantallas.PantallaPremios;
// Librería Swing, SwingUtilities para ejecutar cosas en el hilo de Swing.
import javax.swing.SwingUtilities;

// Controlador de PantallaPremios, al entrar muestra lo último que se sabía y, en segundo plano, pide el dólar del día al
// BCCR (una vez al día), lee el top 5 real de la base y las monedas de quien juega. Cuando todo llega, refresca la pantalla.
// La vista no toca el modelo, todo lo que muestra se lo pasa este controlador.
// A cargo de Allison.
public class ControladorPremios {

    private final PantallaPremios vista;
    private final RankingPremios ranking = new RankingPremios();
    // Monedas acumuladas (aparte del ranking).
    private final ModeloMonedas monedas = new ModeloMonedas();
    // Monedas de quien inició sesión, leídas en segundo plano. Cada entrada a la pantalla suma 1 para ignorar cargas viejas.
    private volatile int misMonedas = 0;
    private int carga = 0;

    // Guarda la vista y le dice que, cuando se entre a la pantalla, ejecute alEntrar.
    public ControladorPremios(PantallaPremios vista) {
        this.vista = vista;
        vista.setAlEntrar(this::alEntrar);
    }

    // Al entrar se muestra lo que ya se sabe y en segundo plano se pide todo lo que usa internet o la base.
    private void alEntrar() {
        // Primero lo que ya se sabe, para que la pantalla nunca salga vacía.
        mostrar();
        final int actual = ++carga;
        final String usuario = Sesion.getNombre();
        Thread t = new Thread(() -> {
            TipoCambio.actualizarSiHaceFalta();
            ranking.cargar();
            misMonedas = monedas.monedas(usuario);
            SwingUtilities.invokeLater(() -> {
                // Si el jugador ya salió de la pantalla o volvió a entrar, esta carga ya no vale.
                if (actual == carga) {
                    mostrar();
                }
            });
        }, "premios-carga");
        t.setDaemon(true);
        t.start();
    }

    // Le pasa a la vista los datos del ranking ya listos para escribir.
    private void mostrar() {
        // Se pasan los datos del ranking a listas, una por columna, y se mandan a la vista junto con el dólar del día.
        int n = ranking.cantidad();
        String[] nombres = new String[n];
        int[] intentos = new int[n];
        String[] colones = new String[n];
        String[] dolares = new String[n];
        for (int i = 0; i < n; i++) {
            nombres[i] = ranking.nombre(i);
            intentos[i] = ranking.intentos(i);
            colones[i] = ranking.colones(i);
            dolares[i] = ranking.dolares(i);
        }
        vista.mostrar(ranking.dolarDelDia(), nombres, intentos, colones, dolares);
        // Monedas de quien inició sesión.
        vista.setMonedas(Sesion.getNombre(), misMonedas);
    }
}
