package ecos.componentes;

import ecos.app.Navegador; // Para poder volver atrás y saber cuándo cambia la pantalla.
import ecos.app.Tema; // Para usar los colores del tema y cambiarlo.
import ecos.recursos.Sonido; // Para reproducir efectos y silenciar la música.
import ecos.recursos.Sprites; // Para obtener los iconos de luna y sol.
import java.awt.BorderLayout; // Layout que divide en norte, sur, este, oeste y centro.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Component; // Tipo general de cualquier componente.
import java.awt.FlowLayout; // Layout que acomoda los componentes uno al lado del otro.
import java.awt.Graphics2D; // Herramienta para dibujar figuras.
import java.awt.Rectangle; // Rectángulo (x, y, ancho, alto).
import javax.swing.BorderFactory; // Fábrica para crear bordes de Swing.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.
import javax.swing.Timer; // Reloj que ejecuta una acción cada cierto tiempo.

public class BarraSuperior extends JPanel {

    private final BotonMenu atras = new BotonMenu("< Atrás", BotonMenu.Estilo.PRINCIPAL); // Botón para volver a la pantalla anterior.
    private final BotonIcono tema = new BotonIcono(); // Botón del sol/luna que cambia entre modo claro y oscuro.
    private final BotonIcono musica = new BotonIcono(); // Botón que silencia o activa la música.
    private final Navegador navegador; // El navegador, para saber si se puede volver atrás.
    private final Timer animacion = new Timer(110, e -> musica.repaint()); // Reloj que mueve las barritas del botón de música.

    public BarraSuperior(Navegador navegador) { // Recibe el navegador de la ventana.
        super(new BorderLayout()); // Usa BorderLayout para poner botones a los lados.
        this.navegador = navegador; // Guarda el navegador.
        setOpaque(false); // La barra es transparente: se ve el fondo de la pantalla por detrás.
        setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 14)); // Margen interno de la barra.
        atras.setPequeno(true); // El botón Atrás es la versión chica.

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Panel para agrupar los botones de la derecha, alineados a la derecha.
        derecha.setOpaque(false); // Lo hace transparente.
        derecha.add(tema); // Agrega el botón de tema.
        derecha.add(musica); // Agrega el botón de música.
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Panel para el botón de la izquierda.
        izquierda.setOpaque(false); // Lo hace transparente.
        izquierda.add(atras); // Agrega el botón Atrás.
        add(izquierda, BorderLayout.WEST); // Pone el botón Atrás a la izquierda.
        add(derecha, BorderLayout.EAST); // Pone el grupo de botones a la derecha.

        tema.setDibujo(this::dibujarTema); // Le dice al botón de tema cómo dibujar su icono.
        musica.setDibujo(this::dibujarMusica); // Le dice al botón de música cómo dibujar su icono.

        atras.addActionListener(e -> { Sonido.efecto("click"); navegador.atras(); }); // Al hacer clic suena el efecto y vuelve a la pantalla anterior.
        tema.addActionListener(e -> { Tema.alternar(); Sonido.efecto("cambio_tema"); }); // Al hacer clic cambia el tema y suena el efecto.
        musica.addActionListener(e -> { if (Sonido.alternar()) Sonido.efecto("musica_on_off"); musica.repaint(); }); // Al hacer clic silencia o activa; si queda activo suena un efecto.

        navegador.alCambiarPantalla(this::actualizar); // Pide que se le avise cada vez que cambie la pantalla.
        Tema.alCambiar(this::repaint); // Repinta la barra cuando cambie el tema.
        animacion.start(); // Empieza a mover las barritas de música.
        setVisible(false); // Empieza oculta hasta que se muestre la primera pantalla.
    }

    // Se llama sola cada vez que cambia la pantalla.
    private void actualizar(String idPantalla) { // Recibe el id de la pantalla nueva.
        setVisible(!"intro".equals(idPantalla)); // Oculta la barra en la intro y la muestra en las demás.
        atras.setVisible(navegador.puedeVolver()); // Muestra el botón Atrás solo si hay una pantalla anterior.
    }

    // Dibuja la luna (modo claro) o el sol (modo oscuro) dentro del botón de tema.
    private void dibujarTema(Graphics2D g, Rectangle r) { // Recibe el pincel y el área disponible.
        int lado = 36; // Tamaño del icono.
        g.drawImage(Sprites.ui(Tema.esOscuro() ? "sun" : "moon", lado), r.x + (r.width - lado) / 2, r.y + (r.height - lado) / 2, null); // Dibuja el icono centrado.
    }

    // Dibuja las barritas de música que se mueven, o las barritas quietas con una raya roja si está silenciada.
    private void dibujarMusica(Graphics2D g, Rectangle r) { // Recibe el pincel y el área disponible.
        boolean activa = Sonido.estaActivo(); // Pregunta si la música está activa.
        double[] periodos = {550, 450, 650, 550}; // Cuánto tarda cada barrita en subir y bajar (milisegundos).
        double[] desfases = {0, 0.36, 0.54, 0.18}; // Desfase de cada barrita para que no se muevan igual.
        Color[] colores = {Tema.naranja(), Tema.rosa(), Tema.naranja(), Tema.rosa()}; // Color de cada barrita.
        int ancho = 6; // Ancho de cada barrita.
        int hueco = 4; // Espacio entre barritas.
        int total = 4 * ancho + 3 * hueco; // Ancho total de las 4 barritas juntas.
        int x0 = r.x + (r.width - total) / 2; // Posición de la primera barrita (centradas).
        int base = r.y + r.height / 2 + 14; // Línea donde se apoyan las barritas.
        long ahora = System.currentTimeMillis(); // Hora actual, para animar.
        for (int i = 0; i < 4; i++) { // Recorre las 4 barritas.
            int alto = 7; // Altura de la barrita cuando está quieta.
            if (activa) { // Si la música está activa, la barrita se mueve.
                double fase = ((ahora / periodos[i]) + desfases[i]) % 2.0; // Posición dentro del ciclo de ida y vuelta.
                double triangulo = fase < 1 ? fase : 2 - fase; // Sube de 0 a 1 y vuelve a bajar.
                alto = 7 + (int) (Math.floor(triangulo * 4) / 4 * 22); // Altura en escalones, como en pixel art.
            }
            int x = x0 + i * (ancho + hueco); // Posición horizontal de esta barrita.
            g.setColor(Tema.borde()); // Color del contorno.
            g.fillRect(x - 2, base - alto - 2, ancho + 4, alto + 4); // Dibuja el contorno.
            g.setColor(activa ? colores[i] : new Color(140, 140, 140)); // Color de la barrita (gris si está silenciada).
            g.fillRect(x, base - alto, ancho, alto); // Dibuja la barrita.
        }
        if (!activa) { // Si la música está silenciada.
            g.setColor(Tema.borde()); // Color del contorno de la raya.
            java.awt.Polygon contorno = new java.awt.Polygon(new int[]{r.x + 5, r.x + 11, r.x + r.width - 5, r.x + r.width - 11}, new int[]{r.y + r.height - 11, r.y + r.height - 5, r.y + 11, r.y + 5}, 4); // Raya diagonal ancha (el contorno).
            g.fillPolygon(contorno); // Dibuja el contorno de la raya.
            g.setColor(Tema.peligro()); // Color rojo de la raya.
            g.setStroke(new java.awt.BasicStroke(4f)); // Grosor de la raya.
            g.drawLine(r.x + 8, r.y + r.height - 8, r.x + r.width - 8, r.y + 8); // Dibuja la raya roja.
        }
    }

    // Solo "existen" para el mouse los botones: así la barra no tapa los clics de la pantalla de abajo.
    @Override // Indica que se reemplaza un método de JPanel.
    public boolean contains(int x, int y) { // Swing pregunta si el punto (x, y) pertenece a la barra.
        for (Component c : getComponents()) { // Recorre los grupos de la barra (izquierda y derecha).
            if (c.isVisible() && c instanceof java.awt.Container grupo) { // Si el grupo se ve.
                for (Component boton : grupo.getComponents()) { // Recorre los botones del grupo.
                    Rectangle area = javax.swing.SwingUtilities.convertRectangle(grupo, boton.getBounds(), this); // Posición del botón dentro de la barra.
                    if (boton.isVisible() && area.contains(x, y)) { // Si el punto cae sobre el botón.
                        return true; // Sí pertenece a la barra.
                    }
                }
            }
        }
        return false; // En cualquier otro lugar, el clic pasa a la pantalla de abajo.
    }
}
