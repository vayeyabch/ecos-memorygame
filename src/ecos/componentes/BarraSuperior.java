package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Navegador;
import ecos.app.Tema;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, BorderLayout para acomodar por zonas, Color para los colores, Component para cualquier elemento
// de la pantalla, FlowLayout para acomodar en fila, Graphics2D para dibujar con más opciones y Rectangle para los
// rectángulos.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
// Librería Swing, BorderFactory para los bordes, JPanel para los paneles y Timer para los relojes.
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

// Barra de arriba con los botones de atrás, idioma, tema y música.
public class BarraSuperior extends JPanel {

    // Botones de la barra, volver a la pantalla anterior, idioma (español o inglés), tema (sol o luna,
    // claro u oscuro) y música (silenciar o activar).
    private final BotonMenu atras = new BotonMenu("< Atrás", BotonMenu.Estilo.PRINCIPAL);
    private final BotonIcono idioma = new BotonIcono();
    private final BotonIcono tema = new BotonIcono();
    private final BotonIcono musica = new BotonIcono();
    // El navegador, para saber si se puede volver atrás. Reloj que mueve las barritas del botón de música.
    private final Navegador navegador;
    private final Timer animacion = new Timer(110, e -> musica.repaint());

    // Recibe el navegador de la ventana.
    public BarraSuperior(Navegador navegador) {
        // Usa BorderLayout para poner botones a los lados. Guarda el navegador. La barra es transparente, se ve el fondo de
        // la pantalla por detrás. Margen interno de la barra.
        super(new BorderLayout());
        this.navegador = navegador;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 14));
        // El botón Atrás es la versión chica.
        atras.setPequeno(true);

        // Panel transparente que agrupa los botones de la derecha, alineados a la derecha, con el botón de idioma y el de
        // tema.
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(idioma);
        derecha.add(tema);
        // Agrega el botón de música. Panel para el botón de la izquierda. Lo hace transparente. Agrega el botón Atrás.
        derecha.add(musica);
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        izquierda.add(atras);
        // Pone el botón Atrás a la izquierda. Pone el grupo de botones a la derecha.
        add(izquierda, BorderLayout.WEST);
        add(derecha, BorderLayout.EAST);

        // Le dice al botón de idioma cómo dibujar su icono. Le dice al botón de tema cómo dibujar su icono. Le dice al botón
        // de música cómo dibujar su icono.
        idioma.setDibujo(this::dibujarIdioma);
        tema.setDibujo(this::dibujarTema);
        musica.setDibujo(this::dibujarMusica);

        // Al hacer clic suena el efecto y vuelve a la pantalla anterior. Suena y abre la ventana de idiomas. Al hacer clic cambia el tema y suena el efecto. Al hacer clic silencia o activa; si queda activo suena un
        // efecto.
        atras.addActionListener(e -> { Sonido.efecto("click"); navegador.atras(); });
        idioma.addActionListener(e -> { Sonido.efecto("click"); VentanaIdiomas.mostrar(this); });
        tema.addActionListener(e -> { Tema.alternar(); Sonido.efecto("cambio_tema"); });
        musica.addActionListener(e -> { if (Sonido.alternar()) Sonido.efecto("musica_on_off"); musica.repaint(); });

        // Pide que se le avise cada vez que cambie la pantalla. Repinta la barra cuando cambie el tema. Empieza a mover las
        // barritas de música. Empieza oculta hasta que se muestre la primera pantalla.
        navegador.alCambiarPantalla(this::actualizar);
        Tema.alCambiar(this::repaint);
        animacion.start();
        setVisible(false);
    }

    // Se llama sola cada vez que cambia la pantalla.
    // Recibe el id de la pantalla nueva.
    private void actualizar(String idPantalla) {
        // Oculta la barra en la intro y la muestra en las demás. Muestra el botón Atrás solo si hay una pantalla anterior.
        setVisible(!"intro".equals(idPantalla));
        atras.setVisible(navegador.puedeVolver());
    }

    // Dibuja el icono de idioma, dos globos de diálogo, uno con una "A" y otro con un carácter ("文"), como el icono de traducción.
    // Recibe el pincel y el área disponible.
    private void dibujarIdioma(Graphics2D g, Rectangle r) {
        // El icono dibujado con letras, # es el borde, a el globo azul, b el globo naranja, w la letra oscura y el punto vacío.
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
        // Cada celda mide 3 píxeles. Se calcula la posición inicial, centrada en horizontal y vertical, y se recorren las
        // filas.
        int celda = 3;
        int x0 = r.x + (r.width - 14 * celda) / 2;
        int y0 = r.y + (r.height - filas.length * celda) / 2;
        for (int y = 0; y < filas.length; y++) {
            // Recorre las columnas. Qué hay en esta celda. Vacía, no se dibuja nada. Color según la letra (globos claros, letras oscuras).
            for (int x = 0; x < 14; x++) {
                char c = filas[y].charAt(x);
                if (c == '.') continue;
                g.setColor(c == '#' ? Tema.borde() : c == 'a' ? new Color(0x7CC4F5) : c == 'b' ? new Color(0xFFB074) : new Color(0x5B2333));
                // Dibuja la celda.
                g.fillRect(x0 + x * celda, y0 + y * celda, celda, celda);
            }
        }
    }

    // Dibuja la luna (modo claro) o el sol (modo oscuro) dentro del botón de tema.
    // Recibe el pincel y el área disponible.
    private void dibujarTema(Graphics2D g, Rectangle r) {
        // Tamaño del icono. Dibuja el icono centrado.
        int lado = 36;
        g.drawImage(Sprites.ui(Tema.esOscuro() ? "sun" : "moon", lado), r.x + (r.width - lado) / 2, r.y + (r.height - lado) / 2, null);
    }

    // Dibuja las barritas de música que se mueven, o las barritas quietas con una raya roja si está silenciada.
    // Recibe el pincel y el área disponible.
    private void dibujarMusica(Graphics2D g, Rectangle r) {
        // Pregunta si la música está activa y define cuánto tarda cada barrita en subir y bajar (milisegundos), su desfase
        // para que no se muevan igual y su color.
        boolean activa = Sonido.estaActivo();
        double[] periodos = {550, 450, 650, 550};
        double[] desfases = {0, 0.36, 0.54, 0.18};
        Color[] colores = {Tema.naranja(), Tema.rosa(), Tema.naranja(), Tema.rosa()};
        // Calcula el ancho de cada barrita, el espacio entre ellas, el ancho total de las 4 y la posición de la primera,
        // para que queden centradas.
        int ancho = 6;
        int hueco = 4;
        int total = 4 * ancho + 3 * hueco;
        int x0 = r.x + (r.width - total) / 2;
        // Define la línea donde se apoyan las barritas, toma la hora actual para animar, recorre las 4 y calcula su altura
        // cuando están quietas.
        int base = r.y + r.height / 2 + 14;
        long ahora = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            int alto = 7;
            // Si la música está activa, la barrita se mueve. Posición dentro del ciclo de ida y vuelta. Sube de 0 a 1 y vuelve a
            // bajar. Altura en escalones, como en pixel art.
            if (activa) {
                double fase = ((ahora / periodos[i]) + desfases[i]) % 2.0;
                double triangulo = fase < 1 ? fase : 2 - fase;
                alto = 7 + (int) (Math.floor(triangulo * 4) / 4 * 22);
            }
            // Calcula la posición horizontal de la barrita, el color del contorno y el de la barrita (gris si está
            // silenciada) y las dibuja.
            int x = x0 + i * (ancho + hueco);
            g.setColor(Tema.borde());
            g.fillRect(x - 2, base - alto - 2, ancho + 4, alto + 4);
            g.setColor(activa ? colores[i] : new Color(140, 140, 140));
            // Dibuja la barrita.
            g.fillRect(x, base - alto, ancho, alto);
        }
        // Si la música está silenciada. Color del contorno de la raya. Raya diagonal ancha (el contorno). Dibuja el contorno de la raya.
        if (!activa) {
            g.setColor(Tema.borde());
            java.awt.Polygon contorno = new java.awt.Polygon(new int[]{r.x + 5, r.x + 11, r.x + r.width - 5, r.x + r.width - 11}, new int[]{r.y + r.height - 11, r.y + r.height - 5, r.y + 11, r.y + 5}, 4);
            g.fillPolygon(contorno);
            // Color rojo de la raya. Grosor de la raya. Dibuja la raya roja.
            g.setColor(Tema.peligro());
            g.setStroke(new java.awt.BasicStroke(4f));
            g.drawLine(r.x + 8, r.y + r.height - 8, r.x + r.width - 8, r.y + 8);
        }
    }

    // Solo "existen" para el mouse los botones, así la barra no tapa los clics de la pantalla de abajo.
    // Swing pregunta si el punto (x, y) pertenece a la barra.
    @Override
    public boolean contains(int x, int y) {
        // Recorre los grupos de la barra (izquierda y derecha). Si el grupo se ve. Recorre los botones del grupo. Posición
        // del botón dentro de la barra.
        for (Component c : getComponents()) {
            if (c.isVisible() && c instanceof java.awt.Container grupo) {
                for (Component boton : grupo.getComponents()) {
                    Rectangle area = javax.swing.SwingUtilities.convertRectangle(grupo, boton.getBounds(), this);
                    // Si el punto cae sobre el botón. Sí pertenece a la barra.
                    if (boton.isVisible() && area.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        // En cualquier otro lugar, el clic pasa a la pantalla de abajo.
        return false;
    }
}
