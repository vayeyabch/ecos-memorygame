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
import javax.swing.text.AbstractDocument; // Documento del campo, donde se puede poner un límite de letras.
import javax.swing.text.AttributeSet; // Atributos del texto que se inserta.
import javax.swing.text.BadLocationException; // Error que puede dar el documento.
import javax.swing.text.DocumentFilter; // Filtro que revisa el texto antes de que entre al campo.

public class CampoTextoPixel extends JTextField {

    private static final int BORDE = 4; // Grosor del borde, en píxeles.
    private final String ayuda; // Texto gris que aparece cuando el campo está vacío (placeholder).
    private final Runnable alCambiarTema = this::actualizarColores; // Acción que actualiza los colores cuando cambia el tema.

    public CampoTextoPixel(String ayuda, int columnas) { // Recibe el texto de ayuda y el ancho en columnas.
        super(columnas); // Le pasa el ancho al JTextField.
        this.ayuda = ayuda; // Guarda el texto de ayuda.
        setOpaque(false); // Lo hace transparente, porque el fondo se dibuja a mano.
        setFont(Fuentes.texto(22f)); // Usa la fuente de texto del juego en tamaño 22.
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14)); // Margen interno para que el texto no toque el borde.
        addKeyListener(new KeyAdapter() { // Escucha las teclas que se presionan.
            @Override // Indica que se reemplaza un método de KeyAdapter.
            public void keyTyped(KeyEvent e) { // Se ejecuta cada vez que se escribe un carácter.
                if (!e.isControlDown() && e.getKeyChar() >= 32) { // Solo suena con letras, números y símbolos (no con Enter ni Ctrl).
                    Sonido.efecto("teclear"); // Reproduce el sonido de teclear.
                }
            }
        });
        actualizarColores(); // Pone los colores iniciales.
    }

    // Pone un máximo de letras que se pueden escribir.
    public void setLimite(int maximo) { // Recibe el máximo de letras.
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() { // Filtro que revisa lo que entra.
            @Override // Indica que se reemplaza un método de DocumentFilter.
            public void replace(FilterBypass fb, int desde, int largo, String texto, AttributeSet atributos) throws BadLocationException { // Se ejecuta cuando se escribe o pega algo.
                String nuevo = texto == null ? "" : texto; // Texto que quiere entrar.
                int cabe = maximo - (fb.getDocument().getLength() - largo); // Cuántas letras caben todavía.
                if (cabe > 0) { // Si todavía cabe algo.
                    super.replace(fb, desde, largo, nuevo.length() > cabe ? nuevo.substring(0, cabe) : nuevo, atributos); // Lo mete (cortado si es largo).
                }
            }
        });
    }

    @Override // Indica que se reemplaza un método de JTextField.
    public void addNotify() { // Swing lo llama cuando el campo entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
        actualizarColores(); // Se pone al día con el tema actual.
    }

    @Override // Indica que se reemplaza un método de JTextField.
    public void removeNotify() { // Swing lo llama cuando el campo sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    private void actualizarColores() { // Método interno: ajusta los colores según el tema.
        setForeground(Tema.texto()); // Color de las letras que se escriben.
        setCaretColor(Tema.texto()); // Color del cursor que parpadea.
        repaint(); // Repinta para que se vea el cambio.
    }

    @Override // Indica que se reemplaza el método de dibujo de JTextField.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el campo.
        Graphics2D g2 = (Graphics2D) g.create(); // Crea una copia del pincel para dibujar sin afectar al original.
        g2.setColor(hasFocus() ? Tema.acento() : Tema.borde()); // Si el campo está seleccionado el borde usa el color de acento; si no, el normal.
        g2.fillRect(0, 0, getWidth(), getHeight()); // Dibuja el rectángulo del borde.
        g2.setColor(Tema.caja()); // Elige el color del relleno (el color de caja del tema).
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2); // Dibuja el relleno dejando visible el borde.
        if (getText().isEmpty() && ayuda != null) { // Si el campo está vacío y hay texto de ayuda.
            g2.setFont(getFont()); // Usa la misma fuente del campo.
            Color c = Tema.textoSuave(); // Toma el color de texto suave del tema.
            g2.setColor(c); // Lo usa para el texto de ayuda.
            g2.drawString(ayuda, getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2); // Dibuja el texto de ayuda a la izquierda y centrado vertical.
        }
        g2.dispose(); // Libera el pincel.
        super.paintComponent(g); // Deja que JTextField dibuje el texto escrito y el cursor.
    }
}
