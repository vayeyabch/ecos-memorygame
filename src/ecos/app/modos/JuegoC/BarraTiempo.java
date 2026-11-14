package ecos.app.modos.JuegoC;

import ecos.app.Tema;
import ecos.recursos.Fuentes;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.Timer;

// Barra de tiempo del modo clásico. Se vacía mientras corre el tiempo y muestra el bonus (+7s) al encontrar una pareja.
public class BarraTiempo extends JComponent {

    // Grosor del borde, alto total y cuánto se ve lleno frente a cuánto debe quedar lleno, de 0 a 1.
    private final int borde;
    private final int alto;
    private double valor = 1;
    private double destino = 1;
    // Segundos que quedan (definen el color), parpadeo del relleno rojo y texto que sube, por ejemplo "+7s".
    private double segundos = 10;
    private boolean parpadeo = false;
    private boolean apagado = false;
    private String bonus = null;
    // Altura del texto que sube, reloj de la animación, parpadeo, texto y cuenta de ticks.
    private double bonusY = 0;
    private final Timer reloj;
    private int cuenta = 0;
    private final Runnable alCambiarTema = this::repaint;

    public BarraTiempo(boolean delgada) {
        this.borde = delgada ? 3 : 4;
        this.alto = delgada ? 14 : 28;
        setOpaque(false);
        reloj = new Timer(40, e -> avanzar());
    }

    // Pone la barra a t segundos de max. Con animar el relleno sube por pasos, como una recarga.
    public void setTiempo(double t, double max, boolean animar) {
        segundos = t;
        // Parpadea si quedan 3 segundos o menos. Sin animación el valor pasa directo al destino.
        destino = Math.max(0, Math.min(1, t / max));
        parpadeo = t <= 3 && t > 0;
        if (!animar) {
            valor = destino; 
        }
        if (animar || parpadeo || bonus != null) {
            reloj.start();
        }
        repaint();
    }

    // Muestra un texto que sube y se desvanece, el "+7s".
    public void mostrarBonus(String texto) {
        bonus = texto;
        bonusY = 0;
        reloj.start();
    }

    private void avanzar() {
        cuenta++;
        // Sube de a poquito hacia el destino, como en pixel art.
        if (Math.abs(valor - destino) > 0.001) {
            double paso = 0.1;
            valor = valor < destino ? Math.min(destino, valor + paso) : Math.max(destino, valor - paso);
        }
        if (cuenta % 3 == 0) {
            apagado = parpadeo && !apagado;
        }
        if (bonus != null) {
            bonusY += 1.6;
            if (bonusY > 34) {
                bonus = null;
            }
        }
        // Si no queda nada que animar, apaga el reloj.
        if (!parpadeo && bonus == null && Math.abs(valor - destino) <= 0.001) {
            apagado = false;
            reloj.stop();
        }
        repaint();
    }

    // Al aparecer en pantalla se registra para cambiar de color con el tema.
    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Al desaparecer se quita del tema.
    @Override
    public void removeNotify() {
        reloj.stop();
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Tamaño preferido: ancho 200 y el alto del constructor.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, alto);
    }

    // Tamaño mínimo: ancho 40.
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(40, alto);
    }

    // Tamaño máximo: ancho libre.
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, alto);
    }

    private Color colorRelleno() {
        if (segundos > 5) {
            return Tema.acierto();
        }
        return segundos > 3 ? Tema.alerta() : Tema.peligro();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Pinta el borde.
        int w = getWidth();
        int h = getHeight();
        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, w, h);
        g2.setColor(Tema.caja());
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2);
        int iw = w - borde * 2;
        int ih = h - borde * 2;
        // Pinta el relleno según el valor actual, con brillo arriba, sombra abajo y rayitas cada 13 píxeles.
        int lleno = (int) Math.round(iw * valor);
        if (lleno > 0 && !apagado) {
            g2.setColor(colorRelleno());
            g2.fillRect(borde, borde, lleno, ih);
            g2.setColor(new Color(255, 255, 255, 115));
            g2.fillRect(borde, borde, lleno, Math.round(ih * 0.22f));
            g2.setColor(new Color(0, 0, 0, 51));
            int y0 = borde + Math.round(ih * 0.78f);
            g2.fillRect(borde, y0, lleno, borde + ih - y0);
        }
        g2.setColor(new Color(0, 0, 0, 51));
        for (int x = borde + 11; x < w - borde; x += 13) {
            g2.fillRect(x, borde, 2, ih);
        }
        // Si hay bonus, pinta el texto que sube.
        if (bonus != null) {
            Font f = Fuentes.titulo(12f);
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics();
            int x = w - 90 - fm.stringWidth(bonus);
            int y = (int) (h / 2 + fm.getAscent() / 2 - bonusY * 0.6);
            float a = (float) Math.max(0, 1 - bonusY / 34);
            g2.setColor(new Color(0, 0, 0, (int) (160 * a)));
            g2.drawString(bonus, x + 2, y + 2);
            Color v = Tema.acierto();
            g2.setColor(new Color(v.getRed(), v.getGreen(), v.getBlue(), (int) (255 * a)));
            g2.drawString(bonus, x, y);
        }
        g2.dispose();
    }
}
