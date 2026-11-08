package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
// Librería gráfica AWT, Color para los colores, Component para cualquier elemento de la pantalla, Dimension para los
// tamaños, Graphics para dibujar, GridLayout para acomodar en una cuadrícula fija y Image para las imágenes.
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna, JComponent para la
// base de los componentes y JPanel para los paneles.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Ventana de resultado (victoria del clásico o fin del modo patrones).
public class DialogoResultado {

    // La capa oscura donde se muestra la ventana.
    private final CapaModal capa;

    // textos y acciones van en pareja, el primero es el botón principal, los demás son secundarios.
    public DialogoResultado(String titulo, String texto, JComponent cuerpo, String premio, String[] textos, Runnable[] acciones) {
        // El panel de la ventana, mínimo 480 px de ancho y margen adentro.
        PanelPixel panel = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(480, super.getPreferredSize().width), super.getPreferredSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(14);
        // Se agregan en orden el título, el texto, el cuerpo y el premio (los que no sean null).
        panel.add(centrar(Etiqueta.titulo(titulo, 16f, Etiqueta.Rol.TEXTO)));
        if (texto != null) {
            panel.add(Box.createVerticalStrut(12));
            // Con los números en la fuente clara.
            panel.add(centrar(Etiqueta.texto("<html><div style='text-align:center'>" + ecos.recursos.Fuentes.conNumeros(texto) + "</div></html>", 18f, Etiqueta.Rol.SUAVE)));
        }
        if (cuerpo != null) {
            panel.add(Box.createVerticalStrut(12));
            panel.add(centrar(cuerpo));
        }
        if (premio != null) {
            panel.add(Box.createVerticalStrut(12));
            panel.add(centrar(new ChipPixel(premio, 11f, true, ChipPixel.Modo.DURAZNO, 4)));
        }
        // Un botón por cada texto, el primero es el principal (rosa) y todos cierran la ventana antes de hacer su acción.
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        capa = new CapaModal(panel);
        for (int i = 0; i < textos.length; i++) {
            BotonMenu b = new BotonMenu(textos[i], i == 0 ? BotonMenu.Estilo.ROSA : BotonMenu.Estilo.SECUNDARIO);
            Runnable accion = acciones[i];
            b.addActionListener(e -> { capa.cerrar(); accion.run(); });
            botones.add(b);
        }
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(botones));
    }

    // Centra un componente dentro de la columna.
    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }

    // Muestra la ventana encima de la pantalla actual.
    public void mostrar(Component origen) {
        capa.mostrar(origen);
    }

    // Dos cajitas lado a lado (jugador 1 y jugador 2) con mascota, nombre e intentos.
    public static JComponent filaJugadores(Image m1, String n1, int a1, boolean gana1, Image m2, String n2, int a2) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 10, 0));
        fila.setOpaque(false);
        fila.add(new Celda(m1, n1, "" + a1, "intentos", gana1));
        fila.add(new Celda(m2, n2, "" + a2, "intentos", !gana1));
        fila.setPreferredSize(new Dimension(420, 130));
        return fila;
    }

    // Dos cajitas con un dato cada una (por ejemplo, niveles superados).
    public static JComponent filaDatos(String et1, String v1, String et2, String v2) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 10, 0));
        fila.setOpaque(false);
        fila.add(new Celda(null, null, v1, et1, false));
        fila.add(new Celda(null, null, v2, et2, false));
        fila.setPreferredSize(new Dimension(420, 90));
        return fila;
    }

    // Una cajita con mascota, nombre, valor y etiqueta; la del ganador se pinta con un tono dorado.
    private static class Celda extends JPanel {
        private final boolean ganador;

        // Arma la celda de arriba hacia abajo, mascota y nombre (si hay), el valor grande y su etiqueta.
        Celda(Image mascota, String nombre, String valor, String etiqueta, boolean ganador) {
            this.ganador = ganador;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(Box.createVerticalGlue());
            if (mascota != null) {
                add(centrar(new ImagenPixel(mascota)));
                add(centrar(Etiqueta.titulo(nombre, 9f, Etiqueta.Rol.TEXTO)));
            }
            add(centrar(Etiqueta.titulo(valor, 18f, Etiqueta.Rol.TEXTO)));
            add(centrar(Etiqueta.texto(etiqueta, 15f, Etiqueta.Rol.SUAVE)));
            add(Box.createVerticalGlue());
        }

        // Pinta el borde y el fondo de la cajita (mezcla el color de alerta con el fondo si es la ganadora).
        @Override
        protected void paintComponent(Graphics g) {
            Color fondo = Tema.caja();
            Color borde = Tema.borde();
            if (ganador) {
                borde = Tema.alerta();
                Color a = Tema.alerta();
                fondo = new Color((a.getRed() * 30 + fondo.getRed() * 70) / 100, (a.getGreen() * 30 + fondo.getGreen() * 70) / 100, (a.getBlue() * 30 + fondo.getBlue() * 70) / 100);
            }
            g.setColor(borde);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(fondo);
            g.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
        }
    }
}
