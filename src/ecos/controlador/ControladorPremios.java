package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;
import ecos.modelo.ModeloMonedas;
import ecos.modelo.RankingPremios;
import ecos.modelo.TipoCambio;
import ecos.pantallas.PantallaPremios;
// Librería Swing, SwingUtilities para ejecutar cosas en el hilo de Swing.
import javax.swing.SwingUtilities;

// Controlador de PantallaPremios, al entrar muestra el top 5 con el dólar guardado y, en segundo plano,
// pide el dólar del día al BCCR (una vez al día). Si llega un valor nuevo, refresca la pantalla.
// La vista no toca el modelo, todo lo que muestra se lo pasa este controlador.
// A cargo de Allison.
public class ControladorPremios {

    private final PantallaPremios vista;
    private final RankingPremios ranking = new RankingPremios();
    // Monedas acumuladas (aparte del ranking).
    private final ModeloMonedas monedas = new ModeloMonedas();

    // Guarda la vista y le dice que, cuando se entre a la pantalla, ejecute alEntrar.
    public ControladorPremios(PantallaPremios vista) {
        this.vista = vista;
        vista.setAlEntrar(this::alEntrar);
    }

    // Al entrar se muestra lo que ya se sabe y en segundo plano se pide el dólar nuevo.
    private void alEntrar() {
        // Primero lo que ya se sabe, para que la pantalla nunca salga vacía. La consulta usa internet, no puede correr en el
        // hilo de la interfaz.
        mostrar();
        Thread t = new Thread(() -> {
            if (TipoCambio.actualizarSiHaceFalta()) {
                // Llegó un dólar nuevo, se refresca.
                SwingUtilities.invokeLater(this::mostrar);
            }
        }, "tipo-cambio");
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
        vista.setMonedas(Sesion.getNombre(), monedas.monedas(Sesion.getNombre()));
    }
}