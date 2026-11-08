package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Sprites;
// Librería gráfica AWT, AlphaComposite para la transparencia, Color para los colores, Cursor para el cursor del mouse,
// Dimension para los tamaños, Graphics para dibujar, Graphics2D para dibujar con más opciones y Image para las imágenes.
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse y MouseEvent para los datos de un clic.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Librería Swing, JComponent para la base de los componentes y Timer para los relojes.
import javax.swing.JComponent;
import javax.swing.Timer;

// Una carta del memory, se voltea con animación, cae desde arriba, se marca al encontrar su pareja y reacciona al mouse.
// Carta del memory.
public class CartaPixel extends JComponent {

    // Datos de la carta, su tamaño, su cara ya escalada (null si no tiene icono) y sus estados, si se ve la cara y si ya es
    // pareja encontrada.
    private final int lado;
    private Image cara = null;
    private boolean arriba = false;
    private boolean hecha = false;
    // Si el tablero está en turno, si el mouse está sobre la carta y si la está apretando.
    private boolean activa = false;
    private boolean encima = false;
    private boolean presionada = false;
    // Datos de las animaciones, el giro, la caída y el destello dorado. En el giro la escala va de 1 a 0 y vuelve a 1, y se
    // guarda la cara que queda al terminar y la fase, que es 0 si está quieta, 1 si está cerrando y 2 si está abriendo. En
    // la caída, 0 es recién cae y 1 ya en su lugar.
    private double escalaX = 1;
    private boolean cambiaraArriba = false;
    private int faseGiro = 0;
    private double caida = 1;
    // Cuándo empieza a caer, el momento en que se programó la caída, la cuenta del destello dorado (0 es apagado) y qué pasa
    // al tocar la carta.
    private long retardoCaida = 0;
    private long inicioCaida = 0;
    private int flash = 0;
    private Runnable alClic = () -> { };
    // Reloj que avanza las animaciones cada 28 ms. Repinta la carta cuando cambia el tema.
    private final Timer reloj = new Timer(28, e -> avanzar());
    private final Runnable alCambiarTema = this::repaint;

    // Recibe el tamaño; el mouse marca los estados y el clic solo se avisa si la carta se puede tocar.
    // Recibe el tamaño de la carta.
    public CartaPixel(int lado) {
        // Guarda el tamaño. Sin fondo propio.
        this.lado = lado;
        setOpaque(false);
        // Maneja el mouse, marca si está encima o apretada, y el clic solo avisa si la carta se puede tocar.
        // Escucha lo que hace el mouse sobre la carta.
        MouseAdapter raton = new MouseAdapter() {
            // Cuando el mouse entra a la carta.
            @Override
            public void mouseEntered(MouseEvent e) {
                // Marca que está encima. Redibuja la carta.
                encima = true;
                repaint();
            }

            // Cuando el mouse sale de la carta.
            @Override
            public void mouseExited(MouseEvent e) {
                // Ya no está encima. Tampoco está apretada. Redibuja la carta.
                encima = false;
                presionada = false;
                repaint();
            }

            // Cuando se aprieta el botón del mouse.
            @Override
            public void mousePressed(MouseEvent e) {
                // Marca que está apretada. Redibuja la carta.
                presionada = true;
                repaint();
            }

            // Cuando se suelta el botón del mouse.
            @Override
            public void mouseReleased(MouseEvent e) {
                // Ya no está apretada. Redibuja la carta.
                presionada = false;
                repaint();
            }

            // Cuando se hace clic.
            @Override
            public void mouseClicked(MouseEvent e) {
                // Solo si la carta se puede tocar. Avisa que la tocaron.
                if (clicable()) {
                    alClic.run();
                }
            }
        };
        // Conecta el mouse con la carta.
        addMouseListener(raton);
    }

    // Se puede tocar solo si el tablero está en turno, no es pareja ya encontrada y no se está moviendo.
    // Dice si la carta se puede tocar.
    private boolean clicable() {
        // Tiene que estar activa, sin ser pareja y sin moverse ni caer.
        return activa && !hecha && faseGiro == 0 && caida >= 1;
    }

    // Estos métodos los usa el controlador, conectar el clic, poner la cara y activar o desactivar la carta.
    // Guarda qué hacer al tocar la carta.
    public void setAlClic(Runnable accion) {
        // Guarda la acción.
        this.alClic = accion;
    }

    // Recibe el icono original y lo deja escalado una sola vez.
    // Pone la imagen de la cara de la carta.
    public void setCara(Image original) {
        // Icono más grande dentro de la carta (antes 0.72). Escala la imagen una sola vez.
        int ic = (int) ((lado - 8 - 6) * 0.92);
        this.cara = original == null ? null : Sprites.escalar(original, ic, ic);
    }

    // Activa o desactiva la carta.
    public void setActiva(boolean activa) {
        // Guarda el estado. Pone la manito solo si se puede tocar. Redibuja la carta.
        this.activa = activa;
        setCursor(Cursor.getPredefinedCursor(activa && !hecha ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    // Dice si la carta está mostrando su cara.
    public boolean estaArriba() {
        // Entrega el estado.
        return arriba;
    }

    // Dice si la carta ya es pareja.
    public boolean estaHecha() {
        // Entrega el estado.
        return hecha;
    }

    // Voltea con animación (cierra, cambia la cara y abre).
    // Voltea la carta.
    public void voltear(boolean haciaArriba) {
        // Si ya está así y quieta, no hace nada. Termina el método.
        if (arriba == haciaArriba && faseGiro == 0) {
            return;
        }
        // Guarda hacia dónde va a quedar. Empieza la fase de cerrar. Arranca el reloj de la animación.
        cambiaraArriba = haciaArriba;
        faseGiro = 1;
        reloj.start();
    }

    // Pone la cara sin animar.
    // Cambia la cara sin animar.
    public void setArriba(boolean arriba) {
        // Guarda el estado. Redibuja la carta.
        this.arriba = arriba;
        repaint();
    }

    // Marca la carta como pareja encontrada (queda opaca con borde amarillo).
    // Marca la carta como pareja.
    public void marcarHecha() {
        // Ahora es pareja. Inicia el destello. Arranca el reloj de la animación. Quita la manito.
        hecha = true;
        flash = 18;
        reloj.start();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    // Reinicia la carta para una partida nueva.
    // Deja la carta como nueva.
    public void reiniciar() {
        // Ya no es pareja. Con su ancho normal.
        arriba = false;
        hecha = false;
        faseGiro = 0;
        escalaX = 1;
        flash = 0;
    }

    // La carta cae desde arriba creciendo, después de "retardo" ms.
    // Hace caer la carta.
    public void caer(long retardo) {
        // Empieza arriba. Guarda cuánto debe esperar. Guarda el momento en que se pidió. Arranca el reloj de la animación.
        caida = 0;
        retardoCaida = retardo;
        inicioCaida = System.currentTimeMillis();
        reloj.start();
        // Redibuja la carta.
        repaint();
    }

    // Avanza un cuadro de las animaciones (caída, giro y destello) y se detiene el reloj cuando no queda ninguna.
    // Avanza las animaciones un cuadro.
    private void avanzar() {
        // Empieza suponiendo que no queda nada por animar y, si la carta todavía está cayendo, calcula cuánto tiempo pasó
        // desde que empezó. La caída es por pasos.
        boolean sigue = false;
        if (caida < 1) {
            long t = System.currentTimeMillis() - inicioCaida - retardoCaida;
            caida = t <= 0 ? 0 : Math.min(1, Math.floor(t / 550.0 * 6) / 6.0);
            // Todavía hay animación.
            sigue = true;
        }
        // Giro de la carta, primero se achica el ancho hasta cero, se cambia la cara y luego se vuelve a abrir.
        // Si está cerrando el giro. Achica el ancho. Cuando llega a cero. Se queda en cero.
        if (faseGiro == 1) {
            escalaX -= 0.34;
            if (escalaX <= 0) {
                escalaX = 0;
                // Cambia la cara.
                arriba = cambiaraArriba;
                faseGiro = 2;
            }
            // Todavía hay animación. Si está abriendo el giro. Agranda el ancho. Cuando llega a su tamaño normal.
            sigue = true;
        } else if (faseGiro == 2) {
            escalaX += 0.34;
            if (escalaX >= 1) {
                // Se queda en su tamaño. El giro termina.
                escalaX = 1;
                faseGiro = 0;
            }
            // Todavía hay animación.
            sigue = true;
        }
        // El destello baja de a poco y el reloj se detiene cuando ya no hay nada que animar.
        // Si hay destello. Baja el destello. Todavía hay animación.
        if (flash > 0) {
            flash--;
            sigue = true;
        }
        // Si no queda ninguna animación. Detiene el reloj.
        if (!sigue) {
            reloj.stop();
        }
        // Redibuja la carta.
        repaint();
    }

    // Se suscribe al cambio de tema mientras está en pantalla, y al quitarse detiene el reloj.
    // Cuando la carta entra a una ventana.
    @Override
    public void addNotify() {
        // Hace lo normal de Swing. Se suscribe al cambio de tema.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Cuando la carta sale de la ventana.
    @Override
    public void removeNotify() {
        // Detiene el reloj. Deja de escuchar el cambio de tema. Hace lo normal de Swing.
        reloj.stop();
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // La carta es cuadrada, del tamaño del lado.
    // Tamaño que necesita la carta.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(lado, lado);
    }

    // Tamaño mínimo de la carta.
    @Override
    public Dimension getMinimumSize() {
        // Es el mismo que el preferido.
        return getPreferredSize();
    }

    // Tamaño máximo de la carta.
    @Override
    public Dimension getMaximumSize() {
        // Es el mismo que el preferido.
        return getPreferredSize();
    }

    // Dibuja la carta, sombra, borde, y después la cara o el dorso, con el tamaño y la posición que dé la animación.
    // Dibuja la carta.
    @Override
    protected void paintComponent(Graphics g) {
        // Si todavía no empieza a caer, no se dibuja. Todavía no empieza a caer, no se ve.
        if (caida <= 0 && System.currentTimeMillis() - inicioCaida < retardoCaida + 1) {
            return;
        }
        // Usa una copia del pincel y dice si la carta está levantada por el mouse, para calcular la escala y el
        // desplazamiento vertical según la caída y el mouse.
        Graphics2D g2 = (Graphics2D) g.create();
        boolean alzada = encima && clicable();
        double esc = caida < 1 ? 0.5 + 0.5 * caida : (alzada ? (presionada ? 1.02 : 1.06) : 1);
        double dy = caida < 1 ? -70 * (1 - caida) : (alzada ? (presionada ? 2 : -2) : 0);
        // La transparencia depende de la caída y de si ya es pareja, se aplica y se mueve el centro de dibujo al centro de
        // la carta.
        float alfa = caida < 1 ? (float) caida : (hecha ? 0.55f : 1f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alfa))));
        g2.translate(getWidth() / 2.0, getHeight() / 2.0 + dy);
        if (caida < 1) {
            // La inclina un poco.
            g2.rotate(Math.toRadians(-10 * (1 - caida)));
        }
        // Aplica el tamaño y el giro. Deja espacio para la sombra y el zoom.
        g2.scale(esc * escalaX, esc);
        int s = lado - 8;
        // Posición y sombra, la carta sube si el mouse está encima y la sombra cambia según el estado. Toma la esquina de la
        // carta, el tamaño de la sombra y su color.
        int x = -s / 2;
        int y = -s / 2 - 2;
        int sombra = alzada ? 8 : (hecha ? 2 : 4);
        g2.setColor(new Color(0, 0, 0, hecha ? 60 : 90));
        // Dibuja la sombra.
        g2.fillRect(x, y + sombra, s, s);
        // El borde es normal, dorado si ya es pareja, o blanco en los parpadeos del destello.
        Color borde = Tema.borde();
        if (hecha) {
            borde = Tema.alerta();
        }
        // En los parpadeos del destello.
        if (flash > 0 && (flash / 3) % 2 == 0) {
            borde = Color.WHITE;
        }
        // Color del borde. Dibuja el borde.
        g2.setColor(borde);
        g2.fillRect(x, y, s, s);
        // Si está arriba o ya es pareja se pinta la cara con su ícono; si no, el dorso carmesí con su dibujo.
        // Si la carta muestra su cara. Color de fondo de la cara. Dibuja el fondo de la cara. Si tiene imagen.
        if (arriba || hecha && cara != null) {
            g2.setColor(Tema.carta());
            g2.fillRect(x + 3, y + 3, s - 6, s - 6);
            if (cara != null) {
                // Tamaño de la imagen. Dibuja la imagen centrada.
                int ic = cara.getWidth(null);
                g2.drawImage(cara, -ic / 2, y + (s - ic) / 2, null);
            }
        // Si está boca abajo. Color del dorso. Dibuja el fondo del dorso. Dibuja el dibujo del dorso.
        } else {
            g2.setColor(Tema.carmesi());
            g2.fillRect(x + 3, y + 3, s - 6, s - 6);
            g2.drawImage(Sprites.dorso(Math.max(8, s - 6)), x + 3, y + 3, s - 6, s - 6, null);
        }
        // Libera el pincel.
        g2.dispose();
    }
}
