package ecos.componentes;

import ecos.app.Tema; // Para saber cuándo cambia el tema.
import ecos.recursos.Sprites; // Para obtener la imagen del fondo.
import java.awt.Graphics; // Herramienta básica para dibujar.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.

public class FondoAnimado extends JPanel {

    public FondoAnimado() { // Constructor: no recibe nada.
        Tema.alCambiar(this::repaint); // Repinta el panel cuando cambie el tema, para mostrar el otro fondo.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el panel.
        super.paintComponent(g); // Dibuja primero lo básico del panel.
        g.drawImage(Sprites.fondo().getImage(), 0, 0, getWidth(), getHeight(), this); // Dibuja el fondo del tema actual estirado para cubrir todo el panel.
    }
}