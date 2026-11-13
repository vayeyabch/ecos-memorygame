package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;                    // pantalla base (margen superior, volver, etc.)
import ecos.componentes.BotonMenu;           // botones del juego
import ecos.componentes.Etiqueta;            // textos con la fuente del juego
import ecos.componentes.ImagenPixel;         // para mostrar la mascota
import ecos.componentes.ListaReglas;         // lista con viñetas
import ecos.componentes.PanelPixel;          // cuadro con el borde del juego
import ecos.recursos.Sonido;                 // sonido de click
import ecos.recursos.Sprites;                // imágenes (mascotas)
// Librería gráfica AWT, para alinear y acomodar.
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
// Librería Swing, para bordes, espacios, paneles y alineaciones.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Pantalla que sale al tocar Clásico en el menú. Una tarjeta grande con dos opciones lado a lado, "Nueva partida clásica"
// (en esta computadora, con las reglas de siempre) y "Multijugador en red" (crear o unirse a una partida). Cada opción
// muestra su mascota, su botón y una lista corta de cómo funciona. Solo muestra, ControladorModoClasico dice a dónde va
// cada botón.
public class PantallaModoClasico extends Pantalla {

    // Qué hace cada botón, lo pone el controlador.
    private Runnable alLocal = () -> { };
    private Runnable alRed = () -> { };

    public PantallaModoClasico() {
        // Id de esta pantalla y a cuál vuelve (el menú).
        super("modo_clasico", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        // Botón de la opción local (rosa) y de la opción en red (naranja). Solo suenan y avisan al controlador.
        BotonMenu botonLocal = new BotonMenu("Nueva partida clásica", BotonMenu.Estilo.ROSA);
        botonLocal.addActionListener(e -> { Sonido.efecto("click"); alLocal.run(); });
        BotonMenu botonRed = new BotonMenu("Multijugador en red", BotonMenu.Estilo.PRINCIPAL);
        botonRed.addActionListener(e -> { Sonido.efecto("click"); alRed.run(); });

        // Las dos columnas. La del multijugador usa la mascota 1 (perro) y la clásica la 2 (gato).
        JPanel columnaLocal = crearColumna(Sprites.mascota(2, 80), botonLocal, "2 jugadores · una computadora",
                new ListaReglas(380,
                        "Cada jugador tiene su tablero con las parejas en otro orden y juegan por turnos.",
                        "Gana quien encuentre todas las parejas con menos intentos.",
                        "Con cuenta ganás <b>premio en dinero</b>, entrás al <b>ranking</b> y sumás monedas."));
        JPanel columnaRed = crearColumna(Sprites.mascota(1, 80), botonRed, "2 jugadores · 2 computadoras",
                new ListaReglas(380,
                        "Uno crea la partida y el otro se une con su IP. Deben estar en la misma red.",
                        "Comparten un solo tablero: gana quien junte más parejas.",
                        "Cada victoria suma <b>monedas</b>, pero no cuenta para el ranking ni para el premio en dinero."));

        // Las dos columnas lado a lado con un espacio en medio.
        JPanel columnas = new JPanel(new GridLayout(1, 2, 18, 0));
        columnas.setOpaque(false);
        columnas.add(columnaLocal);
        columnas.add(columnaRed);

        // Título y subtítulo de la tarjeta grande, centrados.
        Etiqueta titulo = Etiqueta.titulo("Clásico", 26f, Etiqueta.Rol.TEXTO);
        Etiqueta subtitulo = Etiqueta.texto("Elegí cómo querés jugar", 20f, Etiqueta.Rol.SUAVE);

        // La tarjeta grande con el título, el subtítulo y las dos columnas.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(18);
        for (JComponent c : new JComponent[]{titulo, subtitulo, columnas}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(10));
        }
        add(tarjeta);
    }

    // Arma una columna (cuadro con borde): mascota, botón, texto corto y lista de cómo funciona.
    private JPanel crearColumna(java.awt.Image mascota, BotonMenu boton, String detalle, ListaReglas lista) {
        PanelPixel columna = new PanelPixel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setPadding(14);
        ImagenPixel imagen = new ImagenPixel(mascota);
        imagen.setAncho(80);
        Etiqueta texto = Etiqueta.titulo(detalle, 10f, Etiqueta.Rol.SUAVE);
        // Lo de arriba (mascota, botón y texto) va centrado y la lista a la izquierda.
        for (JComponent c : new JComponent[]{imagen, boton, texto}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            columna.add(c);
            columna.add(Box.createVerticalStrut(10));
        }
        lista.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(lista);
        return columna;
    }

    // Métodos que usa el controlador (ControladorModoClasico).

    // Se ejecuta al tocar "Nueva partida clásica".
    public void setAlLocal(Runnable accion) { alLocal = accion; }

    // Se ejecuta al tocar "Multijugador en red".
    public void setAlRed(Runnable accion) { alRed = accion; }
}
