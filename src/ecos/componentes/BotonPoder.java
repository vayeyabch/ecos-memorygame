package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar las fuentes pixel del juego.
import ecos.recursos.Sprites; // Para cargar los iconitos (ojo y moneda).
import java.awt.AlphaComposite; // Permite dibujar con transparencia.
import java.awt.Cursor; // Forma del puntero del mouse.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.FontMetrics; // Medidas de una fuente.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import java.awt.event.MouseAdapter; // Clase para escuchar el mouse sin implementar todos los métodos.
import java.awt.event.MouseEvent; // Datos de un evento del mouse.
import javax.swing.JButton; // Botón de Swing: la clase de la que hereda esta.

public class BotonPoder extends JButton {

    private final String texto; // Nombre del comodín, por ejemplo "Ojo espía".
    private final int precio; // Cuántas monedas cuesta.
    private boolean encima = false; // true mientras el mouse está sobre el botón.
    private final Runnable alCambiarTema = this::repaint; // Acción que repinta el botón cuando cambia el tema.

    public BotonPoder(String texto, int precio) { // Constructor: recibe el nombre y el precio.
        this.texto = texto; // Guarda el nombre.
        this.precio = precio; // Guarda el precio.
        setContentAreaFilled(false); // Quita el fondo que Swing le pone a los botones.
        setBorderPainted(false); // Quita el borde normal de Swing.
        setFocusPainted(false); // Quita el cuadro de foco.
        setOpaque(false); // Lo hace transparente, porque el dibujo se hace a mano.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Pone la manito al pasar el mouse.
        addMouseListener(new MouseAdapter() { // Escucha cuando el mouse entra y sale.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseEntered(MouseEvent e) { // Cuando el mouse entra al botón.
                encima = true; // Anota que está encima.
                repaint(); // Repinta para que el botón se vea levantado.
            }

            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseExited(MouseEvent e) { // Cuando el mouse sale del botón.
                encima = false; // Anota que ya no está encima.
                repaint(); // Repinta para que vuelva a su lugar.
            }
        });
    }

    @Override // Indica que se reemplaza un método de JButton.
    public void addNotify() { // Swing lo llama cuando el botón entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public void removeNotify() { // Swing lo llama cuando el botón sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public Dimension getPreferredSize() { // Tamaño que pide el botón.
        return new Dimension(250, 52); // 250 de ancho y 52 de alto.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public Dimension getMinimumSize() { // Tamaño mínimo.
        return getPreferredSize(); // Igual al preferido, para que no se achique.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public Dimension getMaximumSize() { // Tamaño máximo.
        return getPreferredSize(); // Igual al preferido, para que no se estire.
    }

    @Override // Indica que se reemplaza el método de dibujo de JButton.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el botón.
        Graphics2D g2 = (Graphics2D) g.create(); // Crea una copia del pincel para dibujar sin afectar al original.
        int w = getWidth(); // Ancho total del botón.
        int h = getHeight() - 8; // Alto sin contar la sombra.
        int dy = encima && isEnabled() ? -2 : 0; // Si el mouse está encima y está activo, el botón sube 2 píxeles.
        if (!isEnabled()) { // Si el botón está desactivado (no alcanzan las monedas).
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f)); // Lo dibuja medio transparente.
        }
        g2.translate(0, dy + 2); // Mueve el dibujo para dejar lugar a la sombra y al levantado.
        g2.setColor(Tema.sombra()); // Elige el color de la sombra.
        g2.fillRect(0, 4, w, h); // Dibuja la sombra: el mismo rectángulo, corrido hacia abajo.
        g2.setColor(Tema.borde()); // Elige el color del borde.
        g2.fillRect(0, 0, w, h); // Dibuja el rectángulo del borde.
        g2.setColor(Tema.boton()); // Elige el color de relleno del botón.
        g2.fillRect(3, 3, w - 6, h - 6); // Dibuja el relleno dejando visible el borde.
        g2.drawImage(Sprites.ui("eye", 26), 9, (h - 26) / 2, null); // Dibuja el iconito del ojo a la izquierda.
        g2.setFont(Fuentes.titulo(9f)); // Elige la fuente pixel para el texto.
        FontMetrics fm = g2.getFontMetrics(); // Mide la fuente para poder centrar el texto.
        g2.setColor(Tema.textoBoton()); // Elige el color de las letras del botón.
        g2.drawString(texto, 44, h / 2 + fm.getAscent() / 2 - 1); // Dibuja el nombre del comodín, centrado en vertical.
        int pw = 66; // Ancho de la cajita del precio.
        int px = w - pw - 8; // Posición de la cajita, pegada a la derecha.
        g2.setColor(Tema.borde()); // Elige el color del borde de la cajita.
        g2.fillRect(px, 8, pw, h - 16); // Dibuja el borde de la cajita.
        g2.setColor(Tema.panel()); // Elige el color de relleno de la cajita.
        g2.fillRect(px + 2, 10, pw - 4, h - 20); // Dibuja el relleno de la cajita.
        g2.drawImage(Sprites.ui("coin", 14), px + 8, (h - 14) / 2, null); // Dibuja el iconito de la moneda.
        g2.setColor(Tema.texto()); // Elige el color de las letras normales.
        g2.drawString("" + precio, px + 28, h / 2 + fm.getAscent() / 2 - 1); // Dibuja el precio al lado de la moneda.
        g2.dispose(); // Libera el pincel.
    }
}