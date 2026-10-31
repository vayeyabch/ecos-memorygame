package ecos.componentes;

import ecos.app.Navegador; // Para saber en qué pantalla estamos y esconder el botón donde estorba.
import ecos.app.Tema; // Para usar los colores del tema.
import ecos.recursos.Sonido; // Para reproducir el clic.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Cursor; // Para poner la manito del mouse.
import java.awt.Dimension; // Ancho y alto.
import java.awt.Graphics; // Pincel básico para dibujar.
import java.awt.Graphics2D; // Pincel avanzado para dibujar.
import java.awt.event.MouseAdapter; // Escucha del mouse sin implementar todos los métodos.
import java.awt.event.MouseEvent; // Datos de un evento del mouse.
import java.util.Set; // Conjunto de valores sin repetir.
import javax.swing.JButton; // Botón de Swing: la clase de la que hereda esta.

// Botón cuadrado con una "i" que abre la ventana "Acerca de". Va en la esquina inferior derecha.
public class BotonAcercaDe extends JButton {

    private static final Set<String> OCULTO_EN = Set.of("intro", "clasico", "patrones"); // Pantallas donde no se muestra (la intro y los juegos).
    private static final int LADO = 40; // Lado del cuadrado, en píxeles.
    private static final int BORDE = 4; // Grosor del borde.
    private static final int SOMBRA = 4; // Alto de la sombra dura de abajo.

    private boolean apretado = false; // true = el botón está siendo apretado.
    private final Runnable alCambiarTema = this::repaint; // Acción que repinta el botón cuando cambia el tema.

    public BotonAcercaDe(Navegador navegador) { // Recibe el navegador para saber cuándo cambia la pantalla.
        setContentAreaFilled(false); // Quita el fondo normal de Swing.
        setBorderPainted(false); // Quita el borde normal de Swing.
        setFocusPainted(false); // Quita el recuadro de foco de Swing.
        setOpaque(false); // Es transparente: el dibujo se hace a mano.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Pone la manito al pasar el mouse.
        addMouseListener(new MouseAdapter() { // Escucha el mouse para animar el botón.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); } // Al apretar, el botón baja.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); } // Al soltar, vuelve a su lugar.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseExited(MouseEvent e) { apretado = false; repaint(); } // Al salir el mouse, vuelve a su lugar.
        });
        addActionListener(e -> { Sonido.efecto("click"); VentanaAcercaDe.mostrar(this); }); // Al hacer clic suena y abre la ventana "Acerca de".
        navegador.alCambiarPantalla(id -> setVisible(!OCULTO_EN.contains(id))); // Se esconde o se muestra según la pantalla.
        setVisible(false); // Empieza oculto hasta que se muestre la primera pantalla.
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
    public Dimension getPreferredSize() { // Tamaño que necesita el botón.
        return new Dimension(LADO, LADO + SOMBRA); // Un cuadrado más el espacio de la sombra.
    }

    @Override // Indica que se reemplaza el método de dibujo de JButton.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el botón.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        int y = apretado ? 3 : 0; // Baja 3 píxeles al apretar.
        int sombra = apretado ? 1 : SOMBRA; // La sombra se achica al apretar.
        g2.setColor(Tema.sombra()); // Color de la sombra dura.
        g2.fillRect(0, y + sombra, LADO, LADO); // Dibuja la sombra corrida hacia abajo.
        g2.setColor(Tema.borde()); // Color del borde.
        g2.fillRect(0, y, LADO, LADO); // Dibuja el cuadrado del borde.
        g2.setColor(Tema.rosa()); // Color amigable de relleno (el mismo rosa de los botones).
        g2.fillRect(BORDE, y + BORDE, LADO - BORDE * 2, LADO - BORDE * 2); // Dibuja el relleno.
        dibujarI(g2, y); // Dibuja la "i" encima.
        g2.dispose(); // Libera el pincel.
    }

    // Dibuja una "i" en pixel art, centrada en el cuadrado.
    private void dibujarI(Graphics2D g, int y) { // Recibe el pincel y cuánto bajó el botón.
        String[] filas = {"..#..", ".....", ".##..", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."}; // La "i" dibujada con # (celda pintada) y . (vacía).
        int celda = 3; // Cada celda mide 3 píxeles.
        int x0 = (LADO - 5 * celda) / 2; // Posición horizontal inicial (centrada).
        int y0 = y + (LADO - filas.length * celda) / 2; // Posición vertical inicial (centrada).
        g.setColor(new Color(91, 35, 51)); // Color de la "i": ciruela oscuro, igual en claro y oscuro.
        for (int f = 0; f < filas.length; f++) { // Recorre las filas.
            for (int c = 0; c < 5; c++) { // Recorre las columnas.
                if (filas[f].charAt(c) == '#') { // Si la celda está pintada.
                    g.fillRect(x0 + c * celda, y0 + f * celda, celda, celda); // Dibuja la celda.
                }
            }
        }
    }
}
