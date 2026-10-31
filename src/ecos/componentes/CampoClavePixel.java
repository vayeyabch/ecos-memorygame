package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema.
import ecos.recursos.Fuentes; // Para la fuente del texto.
import ecos.recursos.Sonido; // Para el sonido al teclear.
import java.awt.Graphics; // Pincel básico para dibujar.
import java.awt.Graphics2D; // Pincel avanzado para dibujar.
import java.awt.event.KeyAdapter; // Escucha del teclado sin implementar todos los métodos.
import java.awt.event.KeyEvent; // Datos de una tecla.
import javax.swing.BorderFactory; // Fábrica para crear bordes de Swing.
import javax.swing.JPasswordField; // Campo de texto que esconde lo que se escribe con puntitos.

// Igual que CampoTextoPixel, pero esconde lo que se escribe (para la contraseña).
public class CampoClavePixel extends JPasswordField {

    private static final int BORDE = 4; // Grosor del borde.
    private final String ayuda; // Texto gris que se ve mientras el campo está vacío.
    private final Runnable alCambiarTema = this::actualizarColores; // Acción que se ejecuta cuando cambia el tema.

    public CampoClavePixel(String ayuda, int columnas) { // Recibe el texto de ayuda y el ancho en columnas.
        super(columnas); // Crea el campo con ese ancho.
        this.ayuda = ayuda; // Guarda el texto de ayuda.
        setOpaque(false); // Transparente: el fondo se dibuja a mano.
        setFont(Fuentes.texto(22f)); // Fuente del texto.
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14)); // Margen interno.
        addKeyListener(new KeyAdapter() { // Escucha el teclado.
            @Override // Indica que se reemplaza un método de KeyAdapter.
            public void keyTyped(KeyEvent e) { // Se ejecuta al escribir una letra.
                if (!e.isControlDown() && e.getKeyChar() >= 32) { // Si es un carácter normal.
                    Sonido.efecto("teclear"); // Suena la tecla.
                }
            }
        });
        actualizarColores(); // Pone los colores del tema actual.
    }

    @Override // Indica que se reemplaza un método de JPasswordField.
    public void addNotify() { // Swing lo llama cuando el campo entra a la ventana.
        super.addNotify(); // Deja que Swing haga lo suyo.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
        actualizarColores(); // Actualiza los colores.
    }

    @Override // Indica que se reemplaza un método de JPasswordField.
    public void removeNotify() { // Swing lo llama cuando el campo sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    private void actualizarColores() { // Ajusta los colores según el tema.
        setForeground(Tema.texto()); // Color de los puntitos.
        setCaretColor(Tema.texto()); // Color del cursor.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza el método de dibujo.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el campo.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel.
        g2.setColor(hasFocus() ? Tema.acento() : Tema.borde()); // Borde de acento si está seleccionado.
        g2.fillRect(0, 0, getWidth(), getHeight()); // Dibuja el borde.
        g2.setColor(Tema.caja()); // Color del relleno.
        g2.fillRect(BORDE, BORDE, getWidth() - BORDE * 2, getHeight() - BORDE * 2); // Dibuja el relleno.
        if (getPassword().length == 0 && ayuda != null) { // Si está vacío y hay texto de ayuda.
            g2.setFont(getFont()); // Usa la misma fuente.
            g2.setColor(Tema.textoSuave()); // Color suave.
            g2.drawString(ayuda, getInsets().left, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2); // Dibuja la ayuda centrada.
        }
        g2.dispose(); // Libera el pincel.
        super.paintComponent(g); // Dibuja los puntitos y el cursor.
    }
}
