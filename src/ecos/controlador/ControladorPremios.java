package ecos.controlador;

import ecos.modelo.RankingPremios;
import ecos.modelo.TipoCambio;
import ecos.pantallas.PantallaPremios;
import javax.swing.SwingUtilities;

// Controlador de PantallaPremios: al entrar muestra el top 5 con el dólar guardado y, en segundo plano,
// pide el dólar del día al BCCR (una vez al día). Si llega un valor nuevo, refresca la pantalla.
// La vista no toca el modelo: todo lo que muestra se lo pasa este controlador.
// Responsable: Allison.
public class ControladorPremios {

    private final PantallaPremios vista;
    private final RankingPremios ranking = new RankingPremios();

    public ControladorPremios(PantallaPremios vista) {
        this.vista = vista;
        vista.setAlEntrar(this::alEntrar);
    }

    private void alEntrar() {
        mostrar(); // Primero lo que ya se sabe, para que la pantalla nunca salga vacía.
        Thread t = new Thread(() -> { // La consulta usa internet: no puede correr en el hilo de la interfaz.
            if (TipoCambio.actualizarSiHaceFalta()) {
                SwingUtilities.invokeLater(this::mostrar); // Llegó un dólar nuevo: se refresca.
            }
        }, "tipo-cambio");
        t.setDaemon(true);
        t.start();
    }

    // Le pasa a la vista los datos del ranking ya listos para escribir.
    private void mostrar() {
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
    }
}