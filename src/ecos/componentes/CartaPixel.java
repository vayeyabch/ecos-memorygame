package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Sprites;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.Timer;

public class CartaPixel extends JComponent {

    private final int lado; // tamaño de la carta
    private Image cara = null; // icono de la cara ya escalado (null = sin icono)
    private boolean arriba = false; // true = se ve la cara
    private boolean hecha = false; // pareja ya encontrada
    private boolean activa = false; // el tablero está en turno
    private boolean encima = false; // el mouse está sobre la carta
    private boolean presionada = false; // el mouse está apretando la carta
    private double escalaX = 1; // para el giro (1 -> 0 -> 1)
    private boolean cambiaraArriba = false; // cara que quedará al terminar el giro
    private int faseGiro = 0; // 0 = quieta, 1 = cerrando, 2 = abriendo
    private double caida = 1; // 0 = recién cae, 1 = ya en su lugar
    private long retardoCaida = 0; // cuándo empieza a caer
    private long inicioCaida = 0; // momento en que se programó la caída
    private int flash = 0; // cuenta del destello dorado (0 = apagado)
    private Runnable alClic = () -> { }; // qué pasa al tocar la carta
    private final Timer reloj = new Timer(28, e -> avanzar());
    private final Runnable alCambiarTema = this::repaint;

    public CartaPixel(int lado) {
        this.lado = lado;
        setOpaque(false);
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

    private boolean clicable() {
        return activa && !hecha && faseGiro == 0 && caida >= 1;
    }

    public void setAlClic(Runnable accion) {
        this.alClic = accion;
    }

    // Recibe el icono original y lo deja escalado una sola vez.
    public void setCara(Image original) {
        int ic = (int) ((lado - 8 - 6) * 0.72);
        this.cara = original == null ? null : Sprites.escalar(original, ic, ic);
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
        setCursor(Cursor.getPredefinedCursor(activa && !hecha ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    public boolean estaArriba() {
        return arriba;
    }

    public boolean estaHecha() {
        return hecha;
    }

    // Voltea con animación (cierra, cambia la cara y abre).
    public void voltear(boolean haciaArriba) {
        if (arriba == haciaArriba && faseGiro == 0) {
            return;
        }
        cambiaraArriba = haciaArriba;
        faseGiro = 1;
        reloj.start();
    }

    // Pone la cara sin animar.
    public void setArriba(boolean arriba) {
        this.arriba = arriba;
        repaint();
    }

    // Marca la carta como pareja encontrada (queda opaca con borde amarillo).
    public void marcarHecha() {
        hecha = true;
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

    // La carta cae desde arriba creciendo, después de "retardo" ms.
    public void caer(long retardo) {
        caida = 0;
        retardoCaida = retardo;
        inicioCaida = System.currentTimeMillis();
        reloj.start();
        repaint();
    }

    private void avanzar() {
        boolean sigue = false;
        if (caida < 1) {
            long t = System.currentTimeMillis() - inicioCaida - retardoCaida;
            caida = t <= 0 ? 0 : Math.min(1, Math.floor(t / 550.0 * 6) / 6.0); // cae por pasos
            sigue = true;
        }
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
        if (flash > 0) {
            flash--;
            sigue = true;
        }
        if (!sigue) {
            reloj.stop();
        }
        repaint();
    }

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

    @Override
    protected void paintComponent(Graphics g) {
        if (caida <= 0 && System.currentTimeMillis() - inicioCaida < retardoCaida + 1) {
            return; // todavía no empieza a caer: no se ve
        }
        Graphics2D g2 = (Graphics2D) g.create();
        boolean alzada = encima && clicable();
        double esc = caida < 1 ? 0.5 + 0.5 * caida : (alzada ? (presionada ? 1.02 : 1.06) : 1);
        double dy = caida < 1 ? -70 * (1 - caida) : (alzada ? (presionada ? 2 : -2) : 0);
        float alfa = caida < 1 ? (float) caida : (hecha ? 0.55f : 1f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alfa))));
        g2.translate(getWidth() / 2.0, getHeight() / 2.0 + dy);
        if (caida < 1) {
            g2.rotate(Math.toRadians(-10 * (1 - caida)));
        }
        g2.scale(esc * escalaX, esc);
        int s = lado - 8; // deja espacio para la sombra y el zoom
        int x = -s / 2;
        int y = -s / 2 - 2;
        int sombra = alzada ? 8 : (hecha ? 2 : 4);
        g2.setColor(new Color(0, 0, 0, hecha ? 60 : 90));
        g2.fillRect(x, y + sombra, s, s);
        Color borde = Tema.borde();
        if (hecha) {
            borde = Tema.alerta();
        }
        if (flash > 0 && (flash / 3) % 2 == 0) {
            borde = Color.WHITE;
        }
        g2.setColor(borde);
        g2.fillRect(x, y, s, s);
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
        g2.dispose();
    }
}
