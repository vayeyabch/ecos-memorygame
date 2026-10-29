package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar la fuente pixel del juego.
import ecos.recursos.Sonido; // Para reproducir el sonido al teclear.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics para dibujar figuras y texto.
import java.awt.event.KeyAdapter; // Clase lista para escuchar el teclado sin implementar todos los métodos.
import java.awt.event.KeyEvent; // Datos de la tecla que se presionó.
import javax.swing.BorderFactory; // Fábrica para crear bordes de Swing.
import javax.swing.JTextField; // Campo de texto de Swing: la clase de la que hereda esta.

public class CampoTextoPixel extends JTextField {

    private static final int BORDE = 4; // Grosor del borde, en píxeles.
    private final String ayuda; // Texto gris que aparece cuando el campo está vacío (placeholder).

    public CampoTextoPixel(String ayuda, int columnas) { // Recibe el texto de ayuda y el ancho en columnas.
        super(columnas); // Le pasa el ancho al JTextField.
        this.ayuda = ayuda; // Guarda el texto de ayuda.
        setOpaque(false); // Lo hace transparente, porque el fondo se dibuja a mano.
        setFont(Fuentes.texto(20f)); // Usa la fuente de texto del juego en tamaño 20.
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14)); // Margen interno para que el texto no toque el borde.
        addKeyListener(new KeyAdapter() { // Escucha las teclas que se presionan.
            @Override // Indica que se reemplaza un método de KeyAdapter.
            public void keyTyped(KeyEvent e) { // Se ejecuta cada vez que se escribe un carácter.
                Sonido.efecto("teclear"); // Reproduce el sonido de teclear.
            }
        });
        Tema.alCambiar(this::actualizarColores); // Actualiza los colores cuando cambie el tema.
        actualizarColores(); // Pone los colores iniciales.
    }

    private void actualizarColores() { // Método interno: ajusta los colores según el tema.
        setForeground(Tema.texto()); // Color de las letras que se escriben.
        setCaretColor(Tema.texto()); // Color del cursor que parpadea.
        repaint(); // Repinta para que se vea el cambio.
    }

    @Override // Indica que se reemplaza el método de dibujo de JTextField.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el campo.
        Graphics2D g2 = (Graphics2D) g.create(); // Crea una copia del pincel para dibujar sin afectar al original.
        g2.setColor(hasFocus() ? Tema.boton() : Tema.borde()); // Si el campo está seleccionado el borde usa el color del botón; si no, el normal.
        g2.fillRect(0, 0, getWidth(), getHeight()); // Dibuja el rectángulo del borde.
        g2.setColor(Tema.esOscuro() ? Tema.caja() : Color.WHITE); // Elige el color del relleno: caja en oscuro, blanco en claro.
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2); // Dibuja el relleno dejando visible el borde.
        if (getText().isEmpty() && ayuda != null) { // Si el campo está vacío y hay texto de ayuda.
            g2.setFont(getFont()); // Usa la misma fuente del campo.
            Color c = Tema.texto(); // Toma el color de texto del tema.
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 120)); // Lo vuelve semitransparente para que se vea como ayuda.
            g2.drawString(ayuda, getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2); // Dibuja el texto de ayuda a la izquierda y centrado vertical.
        }
        g2.dispose(); // Libera el pincel.
        super.paintComponent(g); // Deja que JTextField dibuje el texto escrito y el cursor.
    }
}