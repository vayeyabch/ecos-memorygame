package ecos.componentes;

// Navegador se usa para volver atrás, para saber si hay a dónde volver y para enterarse de cada cambio de pantalla.
import ecos.app.Navegador;
// Tema da los colores y alterna entre modo claro y oscuro. La barra se repinta cuando cambia.
import ecos.app.Tema;
// Sonido reproduce los efectos de los botones y silencia o reactiva la música.
import ecos.recursos.Sonido;
// Sprites da las imágenes del sol y la luna para el botón de tema.
import ecos.recursos.Sprites;
// BorderLayout acomoda piezas en el oeste y el este de un panel. Aquí deja atrás a la izquierda y los demás a la derecha.
import java.awt.BorderLayout;
// Color es el tipo de un color. Se usa en los dibujos de los íconos.
import java.awt.Color;
// Component es el tipo general de cualquier pieza visual. Se usa en contains para recorrer lo que hay dentro de la barra.
import java.awt.Component;
// FlowLayout acomoda las piezas una al lado de otra, alineadas a un lado.
import java.awt.FlowLayout;
// Graphics2D es el pincel con el que se dibujan los íconos.
import java.awt.Graphics2D;
// Rectangle es un rectángulo con posición y tamaño. Marca el espacio donde puede dibujar cada ícono.
import java.awt.Rectangle;
// BorderFactory crea bordes. Aquí crea un borde vacío, que sirve de margen.
import javax.swing.BorderFactory;
// JPanel es un panel, un contenedor donde se meten otras piezas. La barra hereda de él.
import javax.swing.JPanel;
// Timer ejecuta una acción cada cierto tiempo. Aquí mueve las barritas del botón de música.
import javax.swing.Timer;

// Es la barra de arriba con los botones que deben estar a mano en casi todas las pantallas: atrás, idioma, tema y música.
// Hereda de JPanel. Se queda encima de todas las pantallas, y se oculta en la pantalla intro.
public class BarraSuperior extends JPanel {

    // Botón de atrás, con texto. PRINCIPAL es su estilo de color.
    private final BotonMenu atras = new BotonMenu("< Atrás", BotonMenu.Estilo.PRINCIPAL);
    // Los tres botones cuadrados. Son el mismo tipo, BotonIcono, y cada uno recibe después su propio dibujo.
    private final BotonIcono idioma = new BotonIcono();
    private final BotonIcono tema = new BotonIcono();
    private final BotonIcono musica = new BotonIcono();
    // El Navegador, para saber si se puede volver atrás.
    private final Navegador navegador;
    // Reloj que cada 110 milisegundos repinta el botón de música, para que sus barritas se muevan.
    // La flecha es la función que se ejecuta en cada tic, y e es el aviso del reloj, que aquí no se usa.
    private final Timer animacion = new Timer(110, e -> musica.repaint());

    // Arma la barra, conecta cada botón con su acción y se suscribe al Navegador y a Tema.
    public BarraSuperior(Navegador navegador) {
        // super llama al constructor de JPanel y le pone un BorderLayout.
        super(new BorderLayout());
        this.navegador = navegador;
        // Transparente, para que se vea el fondo de la pantalla detrás.
        setOpaque(false);
        // Borde vacío que funciona como margen: 12 arriba, 14 a la izquierda, 0 abajo y 14 a la derecha.
        setBorder(BorderFactory.createEmptyBorder(12, 14, 0, 14));
        atras.setPequeno(true);

        // Panel de la derecha con los tres botones cuadrados, alineados a la derecha y separados 10 píxeles.
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(idioma);
        derecha.add(tema);
        derecha.add(musica);
        // Panel de la izquierda con el botón de atrás.
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        izquierda.add(atras);
        // WEST es el lado izquierdo y EAST el derecho.
        add(izquierda, BorderLayout.WEST);
        add(derecha, BorderLayout.EAST);

        // A cada botón cuadrado se le da el método que dibuja su ícono. this::dibujarIdioma significa
        // este método de esta clase, y encaja porque recibe el pincel y el rectángulo, que es lo que setDibujo espera.
        idioma.setDibujo(this::dibujarIdioma);
        tema.setDibujo(this::dibujarTema);
        musica.setDibujo(this::dibujarMusica);

        // Qué hace cada botón al hacer clic. La flecha es la función que se ejecuta, y e es el aviso del clic.
        // Atrás: suena y le pide al Navegador volver a la pantalla anterior.
        atras.addActionListener(e -> { Sonido.efecto("click"); navegador.atras(); });
        // Idioma: suena y abre la ventana de idiomas. this es la barra, y sirve para saber sobre qué ventana abrirla.
        idioma.addActionListener(e -> { Sonido.efecto("click"); VentanaIdiomas.mostrar(this); });
        // Tema: cambia entre claro y oscuro y suena.
        tema.addActionListener(e -> { Tema.alternar(); Sonido.efecto("cambio_tema"); });
        // Música: alternar devuelve true si el sonido quedó activo. Solo en ese caso suena el efecto,
        // porque si se acaba de silenciar no tendría sentido. Luego repinta el botón para mostrar su nuevo estado.
        musica.addActionListener(e -> { if (Sonido.alternar()) Sonido.efecto("musica_on_off"); musica.repaint(); });

        // Se suscribe al Navegador: cada vez que cambia la pantalla se ejecuta actualizar con el id nuevo.
        navegador.alCambiarPantalla(this::actualizar);
        // Se suscribe a Tema: cuando cambia el modo, se repinta con los colores nuevos.
        Tema.alCambiar(this::repaint);
        // Arranca el reloj de la animación de la música.
        animacion.start();
        // Empieza oculta, porque la primera pantalla es la intro y ahí no se muestra.
        setVisible(false);
    }

    // Se ejecuta cada vez que cambia la pantalla. Decide qué mostrar de la barra.
    private void actualizar(String idPantalla) {
        // La barra se oculta solo en la pantalla "intro". El ! significa no.
        // Se escribe "intro".equals(...) con el texto fijo primero, para que no falle si idPantalla fuera null.
        setVisible(!"intro".equals(idPantalla));
        // El botón de atrás solo se ve si hay una pantalla a la cual volver.
        atras.setVisible(navegador.puedeVolver());
    }

    // Dibuja el ícono de idioma: dos globos de diálogo, uno con una "A" y otro con "文".
    // El dibujo está hecho con una matriz de texto: cada carácter es un cuadrito de color, como en pixel art.
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
        // Cada cuadrito mide 3 píxeles. La x y la y iniciales centran el dibujo dentro del rectángulo.
        // El dibujo mide 14 cuadritos de ancho, y tantos de alto como filas tenga la matriz.
        int celda = 3;
        int x0 = r.x + (r.width - 14 * celda) / 2;
        int y0 = r.y + (r.height - filas.length * celda) / 2;
        // El for de afuera recorre las filas y el de adentro las columnas de cada fila.
        for (int y = 0; y < filas.length; y++) {
            for (int x = 0; x < 14; x++) {
                // charAt saca el carácter de esa posición.
                char c = filas[y].charAt(x);
                // Si es un punto, no dibuja nada y pasa al siguiente. continue salta a la siguiente vuelta.
                if (c == '.') continue;
                // Elige el color con ifs cortos encadenados: # es el borde del tema, a es azul, b es naranja
                // y cualquier otro, o sea la w, es el color oscuro de la letra.
                g.setColor(c == '#' ? Tema.borde() : c == 'a' ? new Color(0x7CC4F5) : c == 'b' ? new Color(0xFFB074) : new Color(0x5B2333));
                // Dibuja el cuadradito en su lugar.
                g.fillRect(x0 + x * celda, y0 + y * celda, celda, celda);
            }
        }
    }

    // Dibuja el ícono de tema: el sol si está en modo oscuro y la luna si está en claro.
    // Muestra el modo al que se pasaría al tocar el botón.
    private void dibujarTema(Graphics2D g, Rectangle r) {
        int lado = 36;
        // Pide la imagen escalada a Sprites y la dibuja centrada en el rectángulo.
        g.drawImage(Sprites.ui(Tema.esOscuro() ? "sun" : "moon", lado), r.x + (r.width - lado) / 2, r.y + (r.height - lado) / 2, null);
    }

    // Dibuja las barritas de música, que se mueven. Si está silenciada quedan quietas, grises y con una raya roja encima.
    private void dibujarMusica(Graphics2D g, Rectangle r) {
        // Dice si el sonido está activo.
        boolean activa = Sonido.estaActivo();
        // Cada barrita sube y baja con su propio periodo, que es lo que tarda en un ciclo, y con su propio desfase.
        // Así no se mueven todas iguales.
        double[] periodos = {550, 450, 650, 550};
        double[] desfases = {0, 0.36, 0.54, 0.18};
        // Colores de las barritas, alternados entre naranja y rosa.
        Color[] colores = {Tema.naranja(), Tema.rosa(), Tema.naranja(), Tema.rosa()};
        // Ancho de cada barrita, hueco entre ellas, y ancho total del grupo para poder centrarlo.
        int ancho = 6;
        int hueco = 4;
        int total = 4 * ancho + 3 * hueco;
        int x0 = r.x + (r.width - total) / 2;
        // La línea de abajo, desde donde crecen las barritas hacia arriba.
        int base = r.y + r.height / 2 + 14;
        // La hora actual en milisegundos. La animación se calcula con el tiempo, no con un contador.
        long ahora = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            // Alto base de 7. Si la música está activa, se calcula uno nuevo.
            int alto = 7;
            if (activa) {
                // La fase avanza con el tiempo y va de 0 a 2. El % 2.0 hace que vuelva a empezar al llegar a 2.
                double fase = ((ahora / periodos[i]) + desfases[i]) % 2.0;
                // Convierte la fase en una onda de triángulo: sube de 0 a 1 y luego baja de 1 a 0.
                double triangulo = fase < 1 ? fase : 2 - fase;
                // Redondea hacia abajo a cuatro escalones para que se mueva a saltos, como en pixel art.
                // Multiplica por 22 para pasar a píxeles y le suma el alto base.
                alto = 7 + (int) (Math.floor(triangulo * 4) / 4 * 22);
            }
            // La posición horizontal de esta barrita.
            int x = x0 + i * (ancho + hueco);
            // Primero un rectángulo un poco más grande con el color del borde, que hace de contorno.
            g.setColor(Tema.borde());
            g.fillRect(x - 2, base - alto - 2, ancho + 4, alto + 4);
            // Luego la barrita encima. Es de su color si está activa y gris si no.
            g.setColor(activa ? colores[i] : new Color(140, 140, 140));
            g.fillRect(x, base - alto, ancho, alto);
        }
        // Si está silenciada, dibuja una raya diagonal roja con contorno, encima de las barritas.
        // java.awt.Polygon y java.awt.BasicStroke se escriben completos porque no están importados.
        if (!activa) {
            g.setColor(Tema.borde());
            // Un polígono de cuatro puntos que forma el contorno de la raya.
            java.awt.Polygon contorno = new java.awt.Polygon(new int[]{r.x + 5, r.x + 11, r.x + r.width - 5, r.x + r.width - 11}, new int[]{r.y + r.height - 11, r.y + r.height - 5, r.y + 11, r.y + 5}, 4);
            g.fillPolygon(contorno);
            // La raya roja, de 4 píxeles de grosor, va de abajo a la izquierda hasta arriba a la derecha.
            g.setColor(Tema.peligro());
            g.setStroke(new java.awt.BasicStroke(4f));
            g.drawLine(r.x + 8, r.y + r.height - 8, r.x + r.width - 8, r.y + 8);
        }
    }

    // Solo los botones cuentan para el mouse. Así la franja de la barra no tapa los clics de la pantalla de abajo.
    // Swing llama a contains para saber si un punto pertenece a la pieza. Si devuelve false, el clic pasa a lo que hay debajo.
    // java.awt.Container y javax.swing.SwingUtilities se escriben completos porque no están importados.
    @Override
    public boolean contains(int x, int y) {
        // Recorre lo que hay dentro de la barra: los dos paneles, izquierda y derecha.
        for (Component c : getComponents()) {
            // Si el panel está visible y es un contenedor, la variable grupo es ese mismo panel ya reconocido.
            if (c.isVisible() && c instanceof java.awt.Container grupo) {
                // Recorre los botones que hay dentro de ese panel.
                for (Component boton : grupo.getComponents()) {
                    // La posición del botón está medida desde su panel. convertRectangle la pasa a la medida desde la barra.
                    Rectangle area = javax.swing.SwingUtilities.convertRectangle(grupo, boton.getBounds(), this);
                    // Si el botón está visible y el punto cae dentro de su área, el mouse sí toca la barra.
                    if (boton.isVisible() && area.contains(x, y)) {
                        return true;
                    }
                }
            }
        }
        // Si el punto no cae sobre ningún botón visible, no cuenta, y el clic pasa a la pantalla de abajo.
        return false;
    }
}