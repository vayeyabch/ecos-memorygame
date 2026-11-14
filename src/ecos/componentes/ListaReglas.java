package ecos.componentes;

import ecos.app.Idioma;
import ecos.app.Tema;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Lista de reglas con una viñeta cuadrada por regla; el texto se parte en varias líneas si no cabe.
public class ListaReglas extends JPanel {

    // Textos de las reglas, su versión en español para traducirlos al cambiar el idioma, y ancho del texto en píxeles.
    private final Etiqueta[] etiquetas;
    private final String[] claves;
    private final Runnable alCambiarIdioma = this::traducir;
    private final int ancho;
    private final float tamano;
    // Cuadraditos de cada regla y cuáles tienen check.
    private final JComponent[] cuadros;
    private final boolean[] marcadas;

    // Lista con la letra de siempre (tamaño 20).
    public ListaReglas(int ancho, String... reglas) {
        this(ancho, 20f, reglas);
    }

    // Lista con otro tamaño de letra, para cuando hay poco espacio.
    public ListaReglas(int ancho, float tamano, String... reglas) {
        this.ancho = ancho;
        this.tamano = tamano;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        etiquetas = new Etiqueta[reglas.length];
        claves = new String[reglas.length];
        cuadros = new JComponent[reglas.length];
        marcadas = new boolean[reglas.length];
        for (int i = 0; i < reglas.length; i++) {
            etiquetas[i] = Etiqueta.texto("", tamano, Etiqueta.Rol.TEXTO);
            setRegla(i, reglas[i]);
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setOpaque(false);
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            final int posicion = i;
            JComponent cuadro = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(Tema.borde());
                    g.fillRect(2, 9, 12, 12);
                    g.setColor(marcadas[posicion] ? Tema.acierto() : Tema.rosa());
                    g.fillRect(0, 7, 12, 12);
                    // Si está marcado dibuja el check, una palomita hecha de cuadritos.
                    if (marcadas[posicion]) {
                        g.setColor(Tema.borde());
                        g.fillRect(2, 12, 2, 2);
                        g.fillRect(4, 14, 2, 2);
                        g.fillRect(6, 12, 2, 2);
                        g.fillRect(8, 10, 2, 2);
                    }
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(16, 26);
                }
            };
            cuadros[i] = cuadro;
            fila.add(cuadro, BorderLayout.WEST);
            fila.add(etiquetas[i], BorderLayout.CENTER);
            add(fila);
            add(Box.createVerticalStrut(8));
        }
    }

    // Pone o quita el check de una regla.
    public void setMarcada(int indice, boolean marcada) {
        marcadas[indice] = marcada;
        cuadros[indice].repaint();
    }

    // Cambia el texto de una regla, por ejemplo para mostrar el tamaño del tablero elegido.
    public void setRegla(int indice, String texto) {
        claves[indice] = texto;
        mostrar(indice);
    }

    private void mostrar(int indice) {
        // Usa HTML con ancho fijo para que el texto se parta en varias líneas.
        String traducido = Idioma.t(claves[indice]);
        etiquetas[indice].setText("<html><div style='width:" + ancho + "px'>" + ecos.recursos.Fuentes.conNumeros(traducido, tamano) + "</div></html>");
    }

    private void traducir() {
        for (int i = 0; i < claves.length; i++) {
            mostrar(i);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Idioma.alCambiar(alCambiarIdioma);
        traducir();
    }

    @Override
    public void removeNotify() {
        Idioma.quitar(alCambiarIdioma);
        super.removeNotify();
    }
}
