package ecos.componentes;

import ecos.app.Navegador; // Para poder volver atrás y saber cuándo cambia la pantalla.
import ecos.app.Tema; // Para usar los colores del tema y cambiarlo.
import ecos.app.modos.JuegoC.BotonPixel; // Botón estilo pixel art hecho por Isa.
import ecos.app.modos.JuegoC.Paleta; // Colores fijos que usa el botón de Isa.
import ecos.recursos.Sonido; // Para reproducir efectos y silenciar la música.
import java.awt.BorderLayout; // Layout que divide en norte, sur, este, oeste y centro.
import java.awt.FlowLayout; // Layout que acomoda los componentes uno al lado del otro.
import java.awt.Graphics; // Herramienta básica para dibujar.
import javax.swing.BorderFactory; // Fábrica para crear bordes de Swing.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.

public class BarraSuperior extends JPanel {

    private final BotonPixel atras = new BotonPixel("< Atrás", Paleta.DORADO, Paleta.TEXTO_NARANJA, 14f); // Botón para volver a la pantalla anterior.
    private final BotonPixel tema = BotonPixel.crearLuna(); // Botón de la luna que cambia entre modo claro y oscuro.
    private final BotonPixel musica = BotonPixel.crearSinBarra(); // Botón que silencia o activa la música.
    private final Navegador navegador; // El navegador, para saber si se puede volver atrás.

    public BarraSuperior(Navegador navegador) { // Recibe el navegador de la ventana.
        super(new BorderLayout()); // Usa BorderLayout para poner botones a los lados.
        this.navegador = navegador; // Guarda el navegador.
        setBorder(BorderFactory.createEmptyBorder(8, 14, 12, 14)); // Margen interno de la barra.

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Panel para agrupar los botones de la derecha, alineados a la derecha.
        derecha.setOpaque(false); // Lo hace transparente para que se vea el fondo de la barra.
        derecha.add(tema); // Agrega el botón de tema.
        derecha.add(musica); // Agrega el botón de música.
        add(atras, BorderLayout.WEST); // Pone el botón Atrás a la izquierda.
        add(derecha, BorderLayout.EAST); // Pone el grupo de botones a la derecha.

        atras.addActionListener(e -> { Sonido.efecto("click"); navegador.atras(); }); // Al hacer clic suena el efecto y vuelve a la pantalla anterior.
        tema.addActionListener(e -> { Tema.alternar(); Sonido.efecto("cambio_tema"); }); // Al hacer clic cambia el tema y suena el efecto.
        musica.addActionListener(e -> { if (Sonido.alternar()) Sonido.efecto("musica_on_off"); }); // Al hacer clic silencia o activa; si queda activo suena un efecto.

        navegador.alCambiarPantalla(this::actualizar); // Pide que se le avise cada vez que cambie la pantalla.
        Tema.alCambiar(this::repaint); // Repinta la barra cuando cambie el tema.
        setVisible(false); // Empieza oculta hasta que se muestre la primera pantalla.
    }

    // Se llama sola cada vez que cambia la pantalla.
    private void actualizar(String idPantalla) { // Recibe el id de la pantalla nueva.
        setVisible(!"intro".equals(idPantalla)); // Oculta la barra en la intro y la muestra en las demás.
        atras.setVisible(navegador.puedeVolver()); // Muestra el botón Atrás solo si hay una pantalla anterior.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar la barra.
        g.setColor(Tema.panel()); // Elige el color de fondo del tema.
        g.fillRect(0, 0, getWidth(), getHeight()); // Pinta toda la barra.
        g.setColor(Tema.borde()); // Elige el color del borde del tema.
        g.fillRect(0, getHeight() - 4, getWidth(), 4); // Pinta una línea de 4 píxeles en la parte de abajo.
    }
}