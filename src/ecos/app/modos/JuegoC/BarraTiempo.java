package ecos.app.modos.JuegoC;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Fuentes;
// Librería gráfica AWT, Color para los colores, Dimension para los tamaños, Font para las fuentes, FontMetrics para
// medir el texto, Graphics para dibujar y Graphics2D para dibujar con más opciones.
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Librería Swing, JComponent para la base de los componentes y Timer para los relojes.
import javax.swing.JComponent;
import javax.swing.Timer;

// Barra de tiempo del modo clásico, se vacía mientras corre el tiempo y muestra el bonus (+7s) cuando se encuentra una pareja.
public class BarraTiempo extends JComponent {

    // Grosor del borde, alto total de la barra y lo que se ve lleno frente a lo que debe quedar lleno, ambos de 0 a 1.
    private final int borde;
    private final int alto;
    private double valor = 1;
    private double destino = 1;
    // Segundos que quedan, que definen el color, y el parpadeo del relleno rojo con su fase. También el texto que sube, por
    // ejemplo "+7s".
    private double segundos = 10;
    private boolean parpadeo = false;
    private boolean apagado = false;
    private String bonus = null;
    // Cuánto ha subido el texto, el reloj que anima la recarga, el parpadeo y el texto, y la cuenta de ticks para el
    // parpadeo.
    private double bonusY = 0;
    private final Timer reloj;
    private int cuenta = 0;
    private final Runnable alCambiarTema = this::repaint;

    public BarraTiempo(boolean delgada) {
        // If corto ?, si es delgada, borde 3, sino borde 4. If corto ?, si es delgada, alto 14, sino alto 28. Cada 40 ms,
        // llama a avanzar().
        this.borde = delgada ? 3 : 4;
        this.alto = delgada ? 14 : 28;
        setOpaque(false);
        reloj = new Timer(40, e -> avanzar());
    }

    // Pone la barra, t segundos de max. Con animar=true el relleno sube por pasos (recarga).
    public void setTiempo(double t, double max, boolean animar) {
        segundos = t;
        // Con 5 de 10 da 0.5 o sea media barra. Parpadea si quedan 3 segundos o menos. si no hay animación, el valor se pone
        // directamente al destino.
        destino = Math.max(0, Math.min(1, t / max));
        parpadeo = t <= 3 && t > 0;
        if (!animar) {
            valor = destino; 
        }
        // Si hay animación, parpadeo o bonus, se inicia el reloj.
        if (animar || parpadeo || bonus != null) {
            reloj.start();
        }
        // Pinta la barra con el nuevo valor.
        repaint();
    }

    // Muestra un texto que sube y se desvanece (el "+7s").
    public void mostrarBonus(String texto) {
        bonus = texto;
        bonusY = 0;
        // Inicia el reloj para animar el bonus.
        reloj.start();
    }

    private void avanzar() {
        cuenta++;
        // Distancia entre lo que se ve y lo que debe quedar lleno. Sube de a poquito, como en pixel art. Recarga la barra
        // hacia el destino, subiendo o bajando según corresponda.
        if (Math.abs(valor - destino) > 0.001) {
            double paso = 0.1;
            valor = valor < destino ? Math.min(destino, valor + paso) : Math.max(destino, valor - paso);
        }
        if (cuenta % 3 == 0) {
            // Parpadea cada ~120 ms.
            apagado = parpadeo && !apagado;
        }
        // Si hay bonus, sube el texto.
        if (bonus != null) {
            bonusY += 1.6;
            if (bonusY > 34) {
                bonus = null;
            }
        }
        // Si no hay parpadeo, ni bonus, ni recarga, se apaga el reloj.
        if (!parpadeo && bonus == null && Math.abs(valor - destino) <= 0.001) {
            apagado = false;
            reloj.stop();
        }
        repaint();
    }

    // Barra aparece en pantalla, se llama a addNotify() y se registra el listener de tema.
    @Override
    public void addNotify() {
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Barra desaparece de pantalla, se llama a removeNotify() y se quita el listener de tema.
    @Override
    public void removeNotify() {
        reloj.stop();
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Tamaño preferido de la barra, ancho 200 y alto según el constructor.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, alto);
    }

    // Tamaño mínimo de la barra, ancho 40 y alto según el constructor.
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(40, alto);
    }

    // Tamaño máximo de la barra, ancho máximo y alto según el constructor.
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, alto);
    }

    // Devuelve el color del relleno según los segundos que quedan.
    private Color colorRelleno() {
        if (segundos > 5) {
            return Tema.acierto();
        }
        return segundos > 3 ? Tema.alerta() : Tema.peligro();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Toma el ancho y el alto de la barra, pinta el borde y rellena toda el área con el color del borde.
        int w = getWidth();
        int h = getHeight();
        g2.setColor(Tema.borde());
        g2.fillRect(0, 0, w, h);
        // Pinta el fondo de la barra. Rellena el área interna con el color de la caja. Ancho interno de la barra. Alto
        // interno de la barra.
        g2.setColor(Tema.caja());
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2);
        int iw = w - borde * 2;
        int ih = h - borde * 2;
        // Calcula el ancho del relleno según el valor actual. Si hay relleno y no está apagado, pinta el relleno.
        int lleno = (int) Math.round(iw * valor);
        if (lleno > 0 && !apagado) {
            g2.setColor(colorRelleno());
            g2.fillRect(borde, borde, lleno, ih);
            // Brillo de arriba.
            g2.setColor(new Color(255, 255, 255, 115));
            g2.fillRect(borde, borde, lleno, Math.round(ih * 0.22f));
            // Sombra de abajo.
            g2.setColor(new Color(0, 0, 0, 51));
            int y0 = borde + Math.round(ih * 0.78f);
            g2.fillRect(borde, y0, lleno, borde + ih - y0);
        }
        // Rayitas de segmentos. cada 13 píxeles, empezando a 11 píxeles del borde.
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
        // TERMINA EL GRÁFICO.
        g2.dispose();
    }
}
