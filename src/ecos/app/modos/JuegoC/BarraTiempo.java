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

public class BarraTiempo extends JComponent {

    private final int borde; // grosor del borde
    private final int alto; // alto total de la barra
    private double valor = 1; // lo que se ve lleno, de 0 a 1
    private double destino = 1; // lo que debe quedar lleno, de 0 a 1
    private double segundos = 10; // segundos que quedan (define el color)
    private boolean parpadeo = false; // el relleno rojo parpadea
    private boolean apagado = false; // fase del parpadeo
    private String bonus = null; // texto que sube, por ejemplo "+7s"
    private double bonusY = 0; // cuánto ha subido el texto
    private final Timer reloj; // anima la recarga, el parpadeo y el texto
    private int cuenta = 0; // cuenta los ticks para el parpadeo
    private final Runnable alCambiarTema = this::repaint;

    public BarraTiempo(boolean delgada) {
        this.borde = delgada ? 3 : 4; // if corto ?, si es delgada, borde 3, sino borde 4
        this.alto = delgada ? 14 : 28; // if corto ?, si es delgada, alto 14, sino alto 28
        setOpaque(false); //sin fondo 
        reloj = new Timer(40, e -> avanzar()); // cada 40 ms, llama a avanzar()
    }

    // Pone la barra: t segundos de max. Con animar=true el relleno sube por pasos (recarga).
    public void setTiempo(double t, double max, boolean animar) {
        segundos = t;
        destino = Math.max(0, Math.min(1, t / max)); //Con 5 de 10 da 0.5 o sea media barra  
        parpadeo = t <= 3 && t > 0; //Parpadea si quedan 3 segundos o menos
        if (!animar) { // si no hay animación, el valor se pone directamente al destino
            valor = destino; 
        }
        if (animar || parpadeo || bonus != null) { // si hay animación, parpadeo o bonus, se inicia el reloj
            reloj.start();
        }
        repaint(); //pinta la barra con el nuevo valor
    }

    // Muestra un texto que sube y se desvanece (el "+7s").
    public void mostrarBonus(String texto) {
        bonus = texto;
        bonusY = 0;
        reloj.start(); // inicia el reloj para animar el bonus  
    }

    private void avanzar() {
        cuenta++;
        if (Math.abs(valor - destino) > 0.001) { //distancia entre lo que se ve y lo que debe quedar lleno
            double paso = 0.1; // sube de a poquito, como en pixel art
            valor = valor < destino ? Math.min(destino, valor + paso) : Math.max(destino, valor - paso); // recarga la barra hacia el destino, subiendo o bajando según corresponda
        }
        if (cuenta % 3 == 0) {
            apagado = parpadeo && !apagado; // parpadea cada ~120 ms
        }
        if (bonus != null) { // si hay bonus, sube el texto
            bonusY += 1.6;
            if (bonusY > 34) {
                bonus = null;
            }
        }
        if (!parpadeo && bonus == null && Math.abs(valor - destino) <= 0.001) { // si no hay parpadeo, ni bonus, ni recarga, se apaga el reloj
            apagado = false;
            reloj.stop();
        }
        repaint();
    }

    @Override
    public void addNotify() {//Barra aparece en pantalla, se llama a addNotify() y se registra el listener de tema
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    @Override
    public void removeNotify() { //Barra desaparece de pantalla, se llama a removeNotify() y se quita el listener de tema
        reloj.stop();
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() { // Tamaño preferido de la barra, ancho 200 y alto según el constructor
        return new Dimension(200, alto);
    }

    @Override
    public Dimension getMinimumSize() { //Tamaño mínimo de la barra, ancho 40 y alto según el constructor
        return new Dimension(40, alto);
    }

    @Override
    public Dimension getMaximumSize() { //Tamaño máximo de la barra, ancho máximo y alto según el constructor
        return new Dimension(Short.MAX_VALUE, alto);
    }

    private Color colorRelleno() { // Devuelve el color del relleno según los segundos que quedan
        if (segundos > 5) {
            return Tema.acierto();
        }
        return segundos > 3 ? Tema.alerta() : Tema.peligro();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(); // ancho de la barra
        int h = getHeight(); // alto de la barra
        g2.setColor(Tema.borde()); // pinta el borde de la barra
        g2.fillRect(0, 0, w, h); // rellena todo el área con el color del borde
        g2.setColor(Tema.caja()); // pinta el fondo de la barra
        g2.fillRect(borde, borde, w - borde * 2, h - borde * 2); // rellena el área interna con el color de la caja
        int iw = w - borde * 2; //  ancho interno de la barra
        int ih = h - borde * 2; // alto interno de la barra
        int lleno = (int) Math.round(iw * valor); // calcula el ancho del relleno según el valor actual
        if (lleno > 0 && !apagado) { // si hay relleno y no está apagado, pinta el relleno
            g2.setColor(colorRelleno());
            g2.fillRect(borde, borde, lleno, ih);
            g2.setColor(new Color(255, 255, 255, 115)); // brillo de arriba
            g2.fillRect(borde, borde, lleno, Math.round(ih * 0.22f));
            g2.setColor(new Color(0, 0, 0, 51)); // sombra de abajo
            int y0 = borde + Math.round(ih * 0.78f);
            g2.fillRect(borde, y0, lleno, borde + ih - y0);
        }
        g2.setColor(new Color(0, 0, 0, 51)); // rayitas de segmentos
        for (int x = borde + 11; x < w - borde; x += 13) { // cada 13 píxeles, empezando a 11 píxeles del borde
            g2.fillRect(x, borde, 2, ih);
        }
        if (bonus != null) { // si hay bonus, pinta el texto que sube
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
        g2.dispose(); //TERMINA EL GRÁFICO
    }
}
