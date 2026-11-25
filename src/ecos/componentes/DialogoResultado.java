package ecos.componentes;

// Tema: colores de las celdas (fondo, borde y dorado del ganador).
import ecos.app.Tema;
// Color: guarda colores.
import java.awt.Color;
// Component: tipo base de cualquier elemento visual; se usa para su alineación.
import java.awt.Component;
// Dimension: ancho y alto; se usa para fijar tamaños.
import java.awt.Dimension;
// Graphics: el "pincel" para dibujar las celdas.
import java.awt.Graphics;
// GridLayout: acomoda componentes en una cuadrícula; aquí 1 fila y 2 columnas.
import java.awt.GridLayout;
// Image: imagen de la mascota.
import java.awt.Image;
// Box: da espacios y "pegamento" entre componentes.
import javax.swing.Box;
// BoxLayout: acomoda componentes en una columna o una fila.
import javax.swing.BoxLayout;
// JComponent: tipo base de los componentes Swing.
import javax.swing.JComponent;
// JPanel: panel contenedor.
import javax.swing.JPanel;

// Ventana de resultado: victoria del clásico o fin del modo patrones.
public class DialogoResultado {

    // La capa oscura con el panel al centro; se guarda para poder mostrarla y cerrarla.
    private final CapaModal capa;

    // Textos y acciones van en pareja; el primero es el botón principal.
    public DialogoResultado(String titulo, String texto, JComponent cuerpo, String premio, String[] textos, Runnable[] acciones) {
        // Clase anónima: un PanelPixel con ancho mínimo de 480 px.
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
        // Los elementos van uno debajo del otro.
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(14);
        // Título, texto, cuerpo y premio, los que no sean null.
        panel.add(centrar(Etiqueta.titulo(titulo, 16f, Etiqueta.Rol.TEXTO)));
        if (texto != null) {
            panel.add(Box.createVerticalStrut(12));
            // El texto se traduce, se le resaltan los números y se centra con HTML.
            panel.add(centrar(Etiqueta.texto("<html><div style='text-align:center'>" + ecos.recursos.Fuentes.conNumeros(ecos.app.Idioma.t(texto)) + "</div></html>", 18f, Etiqueta.Rol.SUAVE)));
        }
        if (cuerpo != null) {
            panel.add(Box.createVerticalStrut(12));
            panel.add(centrar(cuerpo));
        }
        if (premio != null) {
            panel.add(Box.createVerticalStrut(12));
            panel.add(centrar(new ChipPixel(premio, 11f, true, ChipPixel.Modo.DURAZNO, 4)));
        }
        // Un botón por cada texto; el primero es el principal (rosa) y todos cierran la ventana antes de su acción.
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        capa = new CapaModal(panel);
        for (int i = 0; i < textos.length; i++) {
            BotonMenu b = new BotonMenu(textos[i], i == 0 ? BotonMenu.Estilo.ROSA : BotonMenu.Estilo.SECUNDARIO);
            // Se guarda la acción de este botón en una variable local para usarla dentro de la lambda.
            Runnable accion = acciones[i];
            b.addActionListener(e -> { capa.cerrar(); accion.run(); });
            botones.add(b);
        }
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(botones));
    }

    // Centra un componente horizontalmente dentro de un BoxLayout vertical.
    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }

    // Muestra el diálogo encima de la pantalla actual.
    public void mostrar(Component origen) {
        capa.mostrar(origen);
    }

    // Dos cajitas lado a lado, jugador 1 y jugador 2, con mascota, nombre e intentos.
    public static JComponent filaJugadores(Image m1, String n1, int a1, boolean gana1, Image m2, String n2, int a2) {
        // GridLayout(1, 2, 10, 0): una fila, dos columnas y 10 px entre ellas.
        JPanel fila = new JPanel(new GridLayout(1, 2, 10, 0));
        fila.setOpaque(false);
        // El ganador se marca con true; el otro recibe lo contrario (!gana1).
        fila.add(new Celda(m1, n1, "" + a1, "intentos", gana1));
        fila.add(new Celda(m2, n2, "" + a2, "intentos", !gana1));
        fila.setPreferredSize(new Dimension(420, 130));
        return fila;
    }

    // Dos cajitas con un dato cada una, por ejemplo niveles superados.
    public static JComponent filaDatos(String et1, String v1, String et2, String v2) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 10, 0));
        fila.setOpaque(false);
        // Sin mascota ni nombre (null) y sin ganador (false).
        fila.add(new Celda(null, null, v1, et1, false));
        fila.add(new Celda(null, null, v2, et2, false));
        fila.setPreferredSize(new Dimension(420, 90));
        return fila;
    }

    // Una cajita con mascota, nombre, valor y etiqueta; la del ganador se pinta dorada.
    // Clase anidada static: solo la usa DialogoResultado.
    private static class Celda extends JPanel {
        private final boolean ganador;

        Celda(Image mascota, String nombre, String valor, String etiqueta, boolean ganador) {
            this.ganador = ganador;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
            // Glue: espacio flexible arriba y abajo para que el contenido quede centrado verticalmente.
            add(Box.createVerticalGlue());
            if (mascota != null) {
                add(centrar(new ImagenPixel(mascota)));
                add(centrar(Etiqueta.titulo(nombre, 9f, Etiqueta.Rol.TEXTO)));
            }
            add(centrar(Etiqueta.titulo(valor, 18f, Etiqueta.Rol.TEXTO)));
            add(centrar(Etiqueta.texto(etiqueta, 15f, Etiqueta.Rol.SUAVE)));
            add(Box.createVerticalGlue());
        }

        // Dibuja el borde y el fondo; si es el ganador, se mezcla el fondo con el color de alerta (dorado).
        @Override
        protected void paintComponent(Graphics g) {
            Color fondo = Tema.caja();
            Color borde = Tema.borde();
            if (ganador) {
                borde = Tema.alerta();
                Color a = Tema.alerta();
                // Mezcla 30% del color de alerta con 70% del fondo, canal por canal (rojo, verde, azul).
                fondo = new Color((a.getRed() * 30 + fondo.getRed() * 70) / 100, (a.getGreen() * 30 + fondo.getGreen() * 70) / 100, (a.getBlue() * 30 + fondo.getBlue() * 70) / 100);
            }
            g.setColor(borde);
            g.fillRect(0, 0, getWidth(), getHeight());
            // El fondo se dibuja 3 px más adentro, dejando el borde visible.
            g.setColor(fondo);
            g.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
        }
    }
}