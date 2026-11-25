package ecos.componentes;

// Sprites: la clase del equipo que carga las imágenes; de aquí se pide la imagen de cada mascota.
import ecos.recursos.Sprites;
// Dimension: guarda un ancho y un alto; se usa para decir qué tamaño tiene el componente.
import java.awt.Dimension;
// Graphics: el "pincel" con el que se dibuja cada mascota en paintComponent.
import java.awt.Graphics;
// JPanel: clase base de la que hereda DesfileMascotas; es un panel donde podemos dibujar.
import javax.swing.JPanel;
// Timer (de Swing): el reloj que avanza la animación cada 100 ms sin congelar la ventana.
import javax.swing.Timer;

// Mascotas que van pasando por la pantalla de acceso (también se usa en el registro). Cada una salta en un momento distinto.
public class DesfileMascotas extends JPanel {

    // Cuántas mascotas hay, su tamaño, el espacio entre ellas y cuánto sube cada una al saltar (todo en píxeles).
    private static final int MASCOTAS = 9;
    private static final int LADO = 80;
    private static final int SEPARACION = 8;
    private static final int SALTO = 16;
    // Altura del salto en 8 pasos, como pixel art. 0 es en el suelo y 1 es lo más alto; después baja.
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25};

    // Cuenta los pasos; cada 100 ms avanza uno y repinta.
    // La lambda "e -> { ... }" es lo que hace el reloj cada vez que se cumplen los 100 ms.
    private int tic = 0;
    private final Timer reloj = new Timer(100, e -> { tic++; repaint(); });

    // Constructor: deja el panel transparente y le fija el tamaño.
    public DesfileMascotas() {
        // Transparente, para que se vea el fondo de la pantalla.
        setOpaque(false);
        // Ancho: 9 mascotas más 8 espacios entre ellas. Alto: la mascota, lo que sube al saltar y un poquito de margen.
        setPreferredSize(new Dimension(MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION, LADO + SALTO + 8));
    }

    // Arranca el reloj. La pantalla lo llama en alEntrar.
    public void iniciar() {
        reloj.start();
    }

    // Detiene el reloj. La pantalla lo llama en alSalir, para no gastar recursos con la pantalla oculta.
    public void detener() {
        reloj.stop();
    }

    // Dibuja las mascotas. Swing lo llama cada vez que se repinta.
    @Override
    protected void paintComponent(Graphics g) {
        // Centra todas las mascotas y pone cada una en un punto distinto del salto.
        // total es el ancho de todo el desfile; x0 es dónde empieza para quedar centrado.
        int total = MASCOTAS * LADO + (MASCOTAS - 1) * SEPARACION;
        int x0 = (getWidth() - total) / 2;
        for (int i = 0; i < MASCOTAS; i++) {
            // El paso de cada mascota es el tic más un desfase de 3 por mascota, con % para volver a 0 al llegar a 8.
            // Por eso no saltan todas al mismo tiempo.
            int paso = (tic + i * 3) % ESCALONES.length;
            // Cuántos píxeles sube en este paso (redondeado a entero).
            int salto = (int) Math.round(ESCALONES[paso] * SALTO);
            // Dibuja la mascota número i+1: x va corriéndose a la derecha, y está pegada abajo menos lo que salta.
            g.drawImage(Sprites.mascota(i + 1, LADO), x0 + i * (LADO + SEPARACION), getHeight() - LADO - 4 - salto, null);
        }
    }

    // Mínimo y máximo iguales al tamaño preferido, para que los layouts no estiren ni encojan el desfile.
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}