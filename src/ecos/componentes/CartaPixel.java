package ecos.componentes;

// Tema: colores de la carta (borde, fondo de la cara, dorso, borde dorado de pareja).
import ecos.app.Tema;
// Sprites: entrega el dorso de la carta y escala el ícono de la cara una sola vez.
import ecos.recursos.Sprites;
// AlphaComposite: permite dibujar con transparencia (la carta aparece poco a poco al caer).
import java.awt.AlphaComposite;
// Color: guarda colores.
import java.awt.Color;
// Cursor: cambia el puntero del mouse (manito) cuando la carta se puede tocar.
import java.awt.Cursor;
// Dimension: ancho y alto; se usa para el tamaño de la carta.
import java.awt.Dimension;
// Graphics: el "pincel" para dibujar.
import java.awt.Graphics;
// Graphics2D: versión del pincel con más opciones (mover, escalar, rotar, transparencia).
import java.awt.Graphics2D;
// Image: imagen de la cara de la carta.
import java.awt.Image;
// MouseAdapter: clase lista para escuchar el mouse y sobrescribir solo lo que se necesita.
import java.awt.event.MouseAdapter;
// MouseEvent: datos de un evento del mouse.
import java.awt.event.MouseEvent;
// JComponent: clase base de la que hereda CartaPixel para poder dibujarse sola.
import javax.swing.JComponent;
// Timer (de Swing): reloj que avanza las animaciones sin congelar la ventana.
import javax.swing.Timer;

// Una carta del memory: se voltea con animación, cae desde arriba, se marca al encontrar su pareja y reacciona al mouse.
public class CartaPixel extends JComponent {

    // Datos de la carta, tamaño, cara escalada y estados: si se ve la cara y si ya es pareja.
    private final int lado;
    private Image cara = null;
    private boolean arriba = false;
    private boolean hecha = false;
    // Si el tablero está en turno, si el mouse está sobre la carta y si la aprieta.
    private boolean activa = false;
    private boolean encima = false;
    private boolean presionada = false;
    // Animaciones. Giro: la escala va de 1 a 0 y vuelve a 1, con la cara que queda y la fase (0 quieta, 1 cerrando, 2
    // abriendo). Caída: 0 recién cae, 1 en su lugar. Más el destello dorado.
    private double escalaX = 1;
    private boolean cambiaraArriba = false;
    private int faseGiro = 0;
    private double caida = 1;
    // Cuándo empieza a caer, cuándo se programó la caída, cuenta del destello (0 apagado) y qué pasa al tocar.
    private long retardoCaida = 0;
    private long inicioCaida = 0;
    private int flash = 0;
    // Runnable vacío por defecto: así alClic.run() nunca falla aunque nadie lo configure.
    private Runnable alClic = () -> { };
    // Reloj que avanza las animaciones cada 28 ms y repintado al cambiar el tema.
    private final Timer reloj = new Timer(28, e -> avanzar());
    // Se guarda en un campo para poder des-suscribir exactamente el mismo en removeNotify.
    private final Runnable alCambiarTema = this::repaint;

    // lado: tamaño en píxeles del cuadrado de la carta.
    public CartaPixel(int lado) {
        this.lado = lado;
        setOpaque(false);
        // El mouse marca si está encima o apretada, y el clic solo avisa si la carta se puede tocar.
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                presionada = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                presionada = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                presionada = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (clicable()) {
                    alClic.run();
                }
            }
        };
        addMouseListener(raton);
    }

    // Se puede tocar si el tablero está en turno, no es pareja y no se está moviendo.
    private boolean clicable() {
        return activa && !hecha && faseGiro == 0 && caida >= 1;
    }

    // El tablero deja aquí lo que debe pasar al tocar la carta.
    public void setAlClic(Runnable accion) {
        this.alClic = accion;
    }

    // Recibe el icono original y lo escala una sola vez.
    public void setCara(Image original) {
        // 92% del espacio interior (lado menos borde y margen).
        int ic = (int) ((lado - 8 - 6) * 0.92);
        this.cara = original == null ? null : Sprites.escalar(original, ic, ic);
    }

    // Activa o desactiva la carta (solo el tablero del turno está activo) y cambia el puntero.
    public void setActiva(boolean activa) {
        this.activa = activa;
        setCursor(Cursor.getPredefinedCursor(activa && !hecha ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    public boolean estaHecha() {
        return hecha;
    }

    // Voltea con animación: cierra, cambia la cara y abre.
    public void voltear(boolean haciaArriba) {
        // Si ya está así y quieta, no hace nada.
        if (arriba == haciaArriba && faseGiro == 0) {
            return;
        }
        cambiaraArriba = haciaArriba;
        // Fase 1: empieza a cerrarse.
        faseGiro = 1;
        reloj.start();
    }

    // Marca la carta como pareja: queda opaca con borde amarillo.
    public void marcarHecha() {
        hecha = true;
        // Destello: 18 cuadros de animación.
        flash = 18;
        reloj.start();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    // Reinicia la carta para una partida nueva.
    public void reiniciar() {
        arriba = false;
        hecha = false;
        faseGiro = 0;
        escalaX = 1;
        flash = 0;
    }

    // La carta cae desde arriba creciendo, después de retardo ms.
    public void caer(long retardo) {
        caida = 0;
        retardoCaida = retardo;
        inicioCaida = System.currentTimeMillis();
        reloj.start();
        repaint();
    }

    // Avanza un cuadro de las animaciones y detiene el reloj cuando no queda ninguna.
    private void avanzar() {
        // Si todavía está cayendo, calcula cuánto tiempo pasó desde que empezó.
        boolean sigue = false;
        if (caida < 1) {
            long t = System.currentTimeMillis() - inicioCaida - retardoCaida;
            // Si aún no toca, queda en 0; si no, avanza en 6 escalones durante 550 ms (efecto pixel art).
            caida = t <= 0 ? 0 : Math.min(1, Math.floor(t / 550.0 * 6) / 6.0);
            sigue = true;
        }
        // Giro: el ancho se achica hasta cero, se cambia la cara y se vuelve a abrir.
        if (faseGiro == 1) {
            escalaX -= 0.34;
            if (escalaX <= 0) {
                escalaX = 0;
                arriba = cambiaraArriba;
                faseGiro = 2;
            }
            sigue = true;
        } else if (faseGiro == 2) {
            escalaX += 0.34;
            if (escalaX >= 1) {
                escalaX = 1;
                faseGiro = 0;
            }
            sigue = true;
        }
        // El destello baja de a poco y el reloj se detiene cuando no queda nada que animar.
        if (flash > 0) {
            flash--;
            sigue = true;
        }
        if (!sigue) {
            reloj.stop();
        }
        repaint();
    }

    // Mientras está en pantalla se repinta al cambiar el tema; al quitarse detiene el reloj.
    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    @Override
    public void removeNotify() {
        reloj.stop();
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Tamaño fijo: un cuadrado de lado x lado (mínimo y máximo iguales, para que el layout no la estire).
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(lado, lado);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    // Dibuja sombra, borde y después la cara o el dorso, con el tamaño y la posición de la animación.
    @Override
    protected void paintComponent(Graphics g) {
        // Mientras espera su turno para caer no se dibuja nada.
        if (caida <= 0 && System.currentTimeMillis() - inicioCaida < retardoCaida + 1) {
            return;
        }
        // Calcula escala y desplazamiento según la caída y si el mouse la levanta.
        Graphics2D g2 = (Graphics2D) g.create();
        boolean alzada = encima && clicable();
        // esc: tamaño (crece al caer; con el mouse encima sube a 1.06, y a 1.02 si está apretada).
        double esc = caida < 1 ? 0.5 + 0.5 * caida : (alzada ? (presionada ? 1.02 : 1.06) : 1);
        // dy: desplazamiento vertical (viene de arriba al caer; con el mouse sube 2 px y apretada baja 2).
        double dy = caida < 1 ? -70 * (1 - caida) : (alzada ? (presionada ? 2 : -2) : 0);
        // La transparencia depende de la caída y de si ya es pareja.
        float alfa = caida < 1 ? (float) caida : (hecha ? 0.55f : 1f);
        // Math.max/min mantienen la transparencia entre 0 y 1.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alfa))));
        // Se mueve el origen al centro de la carta para escalar y rotar desde ahí.
        g2.translate(getWidth() / 2.0, getHeight() / 2.0 + dy);
        if (caida < 1) {
            // Al caer viene un poco inclinada.
            g2.rotate(Math.toRadians(-10 * (1 - caida)));
        }
        // escalaX achica solo el ancho: eso da el efecto de giro al voltear.
        g2.scale(esc * escalaX, esc);
        int s = lado - 8;
        // Sube con el mouse encima y la sombra cambia según el estado.
        int x = -s / 2;
        int y = -s / 2 - 2;
        int sombra = alzada ? 8 : (hecha ? 2 : 4);
        g2.setColor(new Color(0, 0, 0, hecha ? 60 : 90));
        g2.fillRect(x, y + sombra, s, s);
        // Borde normal, dorado si ya es pareja, o blanco en los parpadeos del destello.
        Color borde = Tema.borde();
        if (hecha) {
            borde = Tema.alerta();
        }
        // (flash / 3) % 2 == 0 alterna blanco y normal cada 3 cuadros.
        if (flash > 0 && (flash / 3) % 2 == 0) {
            borde = Color.WHITE;
        }
        g2.setColor(borde);
        g2.fillRect(x, y, s, s);
        // Arriba o ya pareja se pinta la cara con su ícono; si no, el dorso carmesí.
        if (arriba || hecha && cara != null) {
            g2.setColor(Tema.carta());
            g2.fillRect(x + 3, y + 3, s - 6, s - 6);
            if (cara != null) {
                int ic = cara.getWidth(null);
                g2.drawImage(cara, -ic / 2, y + (s - ic) / 2, null);
            }
        } else {
            g2.setColor(Tema.carmesi());
            g2.fillRect(x + 3, y + 3, s - 6, s - 6);
            g2.drawImage(Sprites.dorso(Math.max(8, s - 6)), x + 3, y + 3, s - 6, s - 6, null);
        }
        // Libera la copia del pincel.
        g2.dispose();
    }
}