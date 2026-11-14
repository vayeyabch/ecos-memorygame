package ecos.componentes;

import ecos.app.Navegador;
import ecos.app.Tema;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

// Barra de arriba con los botones de atrás, idioma, tema y música.
public class BarraSuperior extends JPanel {

    private final BotonMenu atras = new BotonMenu("< Atrás", BotonMenu.Estilo.PRINCIPAL);
    private final BotonIcono idioma = new BotonIcono();
    private final BotonIcono tema = new BotonIcono();
    private final BotonIcono musica = new BotonIcono();
    // Navegador, para saber si se puede volver atrás, y reloj que mueve las barritas del botón de música.
    private final Navegador navegador;
    private final Timer animacion = new Timer(110, e -> musica.repaint());

    public BarraSuperior(Navegador navegador) {
        super(new BorderLayout());
        this.navegador = navegador;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 14));
        atras.setPequeno(true);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(idioma);
        derecha.add(tema);
        derecha.add(musica);
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        izquierda.add(atras);
        add(izquierda, BorderLayout.WEST);
        add(derecha, BorderLayout.EAST);

        idioma.setDibujo(this::dibujarIdioma);
        tema.setDibujo(this::dibujarTema);
        musica.setDibujo(this::dibujarMusica);

        atras.addActionListener(e -> { Sonido.efecto("click"); navegador.atras(); });
        idioma.addActionListener(e -> { Sonido.efecto("click"); VentanaIdiomas.mostrar(this); });
        tema.addActionListener(e -> { Tema.alternar(); Sonido.efecto("cambio_tema"); });
        musica.addActionListener(e -> { if (Sonido.alternar()) Sonido.efecto("musica_on_off"); musica.repaint(); });

        // Se actualiza al cambiar de pantalla y se repinta al cambiar el tema. Empieza oculta.
        navegador.alCambiarPantalla(this::actualizar);
        Tema.alCambiar(this::repaint);
        animacion.start();
        setVisible(false);
    }

    private void actualizar(String idPantalla) {
        setVisible(!"intro".equals(idPantalla));
        atras.setVisible(navegador.puedeVolver());
    }

    // Dibuja el ícono de idioma: dos globos de diálogo, uno con una "A" y otro con "文".
    private void dibujarIdioma(Graphics2D g, Rectangle r) {
        // Cada letra es un color: # borde, a globo azul, b globo naranja, w letra oscura y punto vacío.
        String[] filas = {
            "#########.....",
            "#aaawaaa#.....",
            "#aawawaa#.....",
            "#awwwwwa#.....",
            "#awaaawa#.....",
            "#########.....",
            ".##..#########",
            ".....#bbbwbbb#",
            ".....#bwwwwwb#",
            ".....#bbwbwbb#",
            ".....#bwbbbwb#",
            ".....#########",
            "...........##."
        };
        // Cada celda mide 3 píxeles y el dibujo queda centrado.
        int celda = 3;
        int x0 = r.x + (r.width - 14 * celda) / 2;
        int y0 = r.y + (r.height - filas.length * celda) / 2;
        for (int y = 0; y < filas.length; y++) {
            for (int x = 0; x < 14; x++) {
                char c = filas[y].charAt(x);
                if (c == '.') continue;
                g.setColor(c == '#' ? Tema.borde() : c == 'a' ? new Color(0x7CC4F5) : c == 'b' ? new Color(0xFFB074) : new Color(0x5B2333));
                g.fillRect(x0 + x * celda, y0 + y * celda, celda, celda);
            }
        }
    }

    private void dibujarTema(Graphics2D g, Rectangle r) {
        int lado = 36;
        g.drawImage(Sprites.ui(Tema.esOscuro() ? "sun" : "moon", lado), r.x + (r.width - lado) / 2, r.y + (r.height - lado) / 2, null);
    }

    // Dibuja las barritas de música, que se mueven; si está silenciada quedan quietas con una raya roja.
    private void dibujarMusica(Graphics2D g, Rectangle r) {
        // Cada barrita sube y baja con su propio tiempo y desfase para que no se muevan igual.
        boolean activa = Sonido.estaActivo();
        double[] periodos = {550, 450, 650, 550};
        double[] desfases = {0, 0.36, 0.54, 0.18};
        Color[] colores = {Tema.naranja(), Tema.rosa(), Tema.naranja(), Tema.rosa()};
        int ancho = 6;
        int hueco = 4;
        int total = 4 * ancho + 3 * hueco;
        int x0 = r.x + (r.width - total) / 2;
        int base = r.y + r.height / 2 + 14;
        long ahora = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            int alto = 7;
            // Si la música está activa la barrita se mueve de ida y vuelta, en escalones como en pixel art.
            if (activa) {
                double fase = ((ahora / periodos[i]) + desfases[i]) % 2.0;
                double triangulo = fase < 1 ? fase : 2 - fase;
                alto = 7 + (int) (Math.floor(triangulo * 4) / 4 * 22);
            }
            int x = x0 + i * (ancho + hueco);
            g.setColor(Tema.borde());
            g.fillRect(x - 2, base - alto - 2, ancho + 4, alto + 4);
            g.setColor(activa ? colores[i] : new Color(140, 140, 140));
            g.fillRect(x, base - alto, ancho, alto);
        }
        if (!activa) {
            g.setColor(Tema.borde());
            java.awt.Polygon contorno = new java.awt.Polygon(new int[]{r.x + 5, r.x + 11, r.x + r.width - 5, r.x + r.width - 11}, new int[]{r.y + r.height - 11, r.y + r.height - 5, r.y + 11, r.y + 5}, 4);
            g.fillPolygon(contorno);
            g.setColor(Tema.peligro());
            g.setStroke(new java.awt.BasicStroke(4f));
            g.drawLine(r.x + 8, r.y + r.height - 8, r.x + r.width - 8, r.y + 8);
        }
    }

    // Solo los botones cuentan para el mouse, así la barra no tapa los clics de la pantalla de abajo.
    @Override
    public boolean contains(int x, int y) {
        for (Component c : getComponents()) {
            if (c.isVisible() && c instanceof java.awt.Container grupo) {
                for (Component boton : grupo.getComponents()) {
                    Rectangle area = javax.swing.SwingUtilities.convertRectangle(grupo, boton.getBounds(), this);
                    if (boton.isVisible() && area.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
