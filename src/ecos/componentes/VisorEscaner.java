package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Color para los colores, Dimension para los tamaños, FontMetrics para medir el texto, Graphics
// para dibujar y Graphics2D para dibujar con más opciones.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Librería Swing, JPanel para los paneles y Timer para los relojes.
import javax.swing.JPanel;
import javax.swing.Timer;

// Visor del reconocimiento facial, muestra la mascota con una línea de escáner animada y tres estados (normal,
// escaneando y listo).
public class VisorEscaner extends JPanel {

    // Los estados posibles del visor.
    public enum Estado { NORMAL, ESCANEANDO, OK, ERROR }

    // Lado del cuadro del visor, alto de la sombra dura de abajo, tamaño de la mascota dentro del visor y cuántas mascotas
    // hay.
    private static final int LADO = 260;
    private static final int SOMBRA = 5;
    private static final int MASCOTA = 168;
    private static final int MASCOTAS = 9;
    // Cada cuántos pasos (de 100 ms) cambia la mascota, altura del salto en 8 pasos y colores, violeta oscuro del visor
    // (igual en los dos temas) y rosa de las esquinas y el haz.
    private static final int CAMBIO_MASCOTA_TICS = 13;
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25};
    private static final Color FONDO = new Color(0x1C1327);
    private static final Color ROSA = new Color(0xFCA5AB);

    // Reloj de las animaciones, estado actual, número de la mascota que se ve y cuenta de los pasos de la animación.
    private final Timer reloj = new Timer(100, e -> alTic());
    private Estado estado = Estado.NORMAL;
    private int indice = 1;
    private int tic = 0;
    // Cuánto está corrida la mascota hacia los lados ahora. Cuánto debería estar corrida.
    private double desplazamiento = 0;
    private int destino = 0;

    // Constructor sin parámetros.
    public VisorEscaner() {
        // Es transparente (el dibujo se hace a mano). Tamaño del visor más su sombra.
        setOpaque(false);
        setPreferredSize(new Dimension(LADO, LADO + SOMBRA));
    }

    // Cambia el estado del visor.
    public void setEstado(Estado estado) {
        // Guarda el estado.
        this.estado = estado;
        repaint();
    }

    // Corre la mascota hacia un lado (para simular que gira la cabeza).
    public void setDesplazamiento(int px) {
        // Guarda hasta dónde debe llegar.
        this.destino = px * 2;
    }

    // Empieza las animaciones.
    public void iniciar() {
        // Arranca el reloj.
        reloj.start();
    }

    // Detiene las animaciones.
    public void detener() {
        // Detiene el reloj.
        reloj.stop();
    }

    // Se ejecuta en cada paso de la animación.
    private void alTic() {
        // Cuenta un paso más. Si no está escaneando y toca cambiar. Pasa a la siguiente mascota.
        tic++;
        if (estado != Estado.ESCANEANDO && tic % CAMBIO_MASCOTA_TICS == 0) {
            indice = indice % MASCOTAS + 1;
        }
        // Se acerca poco a poco al destino.
        desplazamiento += (destino - desplazamiento) / 2.5;
        repaint();
    }

    // Swing lo llama cada vez que hay que dibujar.
    @Override
    protected void paintComponent(Graphics g) {
        // Usa una copia del pincel, elige el color normal del marco y, si salió bien, el marco es verde.
        Graphics2D g2 = (Graphics2D) g.create();
        Color marco = Tema.borde();
        if (estado == Estado.OK) {
            marco = Tema.acierto();
        // El marco es rojo.
        } else if (estado == Estado.ERROR) {
            marco = Tema.peligro();
        }
        // Color de la sombra dura. Dibuja la sombra. Color del marco. Dibuja el marco.
        g2.setColor(Tema.sombra());
        g2.fillRect(0, SOMBRA, LADO, LADO);
        g2.setColor(marco);
        g2.fillRect(0, 0, LADO, LADO);
        // Color del fondo del visor. Dibuja el fondo.
        g2.setColor(FONDO);
        g2.fillRect(4, 4, LADO - 8, LADO - 8);

        // Todo lo que sigue se recorta dentro del visor. Altura del salto de la mascota. Posición horizontal de la mascota.
        // Posición vertical de la mascota.
        g2.setClip(4, 4, LADO - 8, LADO - 8);
        int salto = (int) Math.round(ESCALONES[tic % ESCALONES.length] * 16);
        int x = (LADO - MASCOTA) / 2 + (int) Math.round(desplazamiento);
        int y = (LADO - MASCOTA) / 2 - salto;
        // Dibuja la mascota.
        g2.drawImage(Sprites.mascota(indice, MASCOTA), x, y, null);

        // Color de las esquinas. Distancia de las esquinas al borde. Largo de cada línea de la esquina. Grosor de las esquinas.
        g2.setColor(ROSA);
        int m = 12;
        int l = 32;
        int t = 5;
        // Esquina de arriba a la izquierda. Esquina de arriba a la derecha. Esquina de abajo a la izquierda. Esquina de abajo
        // a la derecha.
        g2.fillRect(m, m, l, t); g2.fillRect(m, m, t, l);
        g2.fillRect(LADO - m - l, m, l, t); g2.fillRect(LADO - m - t, m, t, l);
        g2.fillRect(m, LADO - m - t, l, t); g2.fillRect(m, LADO - m - l, t, l);
        g2.fillRect(LADO - m - l, LADO - m - t, l, t); g2.fillRect(LADO - m - t, LADO - m - l, t, l);

        // Si está escaneando. Cuánto baja y sube el haz. Posición dentro del ciclo de ida y vuelta (16 escalones cada lado).
        if (estado == Estado.ESCANEANDO) {
            int recorrido = LADO - 8 - 20;
            int paso = tic % 32;
            int arriba = paso < 16 ? paso : 32 - paso;
            // Calcula la posición vertical del haz, elige sus colores (incluido el del borde interior) y lo dibuja.
            int hy = 10 + arriba * recorrido / 16;
            g2.setColor(ROSA);
            g2.fillRect(12, hy, LADO - 24, 8);
            g2.setColor(new Color(0xFFF0E4));
            // Dibuja el contorno claro del haz.
            g2.drawRect(12 + 1, hy + 1, LADO - 24 - 3, 5);
        }

        // Si salió bien. Negro semitransparente. Oscurece el visor.
        if (estado == Estado.OK) {
            g2.setColor(new Color(11, 7, 16, 184));
            g2.fillRect(4, 4, LADO - 8, LADO - 8);
            g2.setFont(Fuentes.titulo(32f));
            // Medidas de la fuente. Escribe OK en el centro.
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Tema.acierto());
            g2.drawString("OK", (LADO - fm.stringWidth("OK")) / 2, (LADO - fm.getHeight()) / 2 + fm.getAscent());
        }
        // Libera el pincel.
        g2.dispose();
    }

    // Tamaño mínimo del componente.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo del componente.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }
}
