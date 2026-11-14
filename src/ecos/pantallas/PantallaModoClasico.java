package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Pantalla que sale al tocar Clásico en el menú: una tarjeta grande con dos opciones lado a lado, "Nueva partida
// clásica" (en esta computadora) y "Multijugador en red" (crear o unirse). Cada una muestra su mascota, su botón y una
// lista de cómo funciona. Solo muestra; ControladorModoClasico dice a dónde va cada botón.
public class PantallaModoClasico extends Pantalla {

    // Ancho del texto de cada lista y tamaño de su letra.
    private static final int ANCHO_LISTA = 250;
    private static final float LETRA_LISTA = 18f;

    private Runnable alLocal = () -> { };
    private Runnable alRed = () -> { };

    public PantallaModoClasico() {
        // Id de esta pantalla y a cuál vuelve (el menú).
        super("modo_clasico", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        BotonMenu botonLocal = new BotonMenu("Nueva partida clásica", BotonMenu.Estilo.ROSA);
        botonLocal.addActionListener(e -> { Sonido.efecto("click"); alLocal.run(); });
        BotonMenu botonRed = new BotonMenu("Multijugador en red", BotonMenu.Estilo.PRINCIPAL);
        botonRed.addActionListener(e -> { Sonido.efecto("click"); alRed.run(); });

        // La clásica usa la mascota 2 y la de red la 1. La letra es un poco más pequeña para que las dos columnas quepan
        // en una ventana chica.
        JPanel columnaLocal = crearColumna(Sprites.mascota(2, 64), botonLocal, "2 jugadores · una computadora",
                new ListaReglas(ANCHO_LISTA, LETRA_LISTA,
                        "Cada jugador tiene su tablero con las parejas en otro orden y juegan por turnos.",
                        "Gana quien encuentre todas las parejas con menos intentos.",
                        "Con cuenta ganás <b>premio en dinero</b>, entrás al <b>ranking</b> y sumás monedas."));
        JPanel columnaRed = crearColumna(Sprites.mascota(1, 64), botonRed, "2 jugadores · 2 computadoras",
                new ListaReglas(ANCHO_LISTA, LETRA_LISTA,
                        "Uno crea la partida y el otro se une con su IP, en la misma red.",
                        "Comparten un solo tablero y gana quien junte más parejas.",
                        "No hay <b>Ojo espía</b>, porque los dos ven el mismo tablero.",
                        "Ganar suma <b>monedas</b> para la partida clásica, pero no cuenta para el ranking ni para el premio."));

        JPanel columnas = new JPanel(new GridLayout(1, 2, 18, 0));
        columnas.setOpaque(false);
        columnas.add(columnaLocal);
        columnas.add(columnaRed);

        Etiqueta titulo = Etiqueta.titulo("Clásico", 22f, Etiqueta.Rol.TEXTO);
        Etiqueta subtitulo = Etiqueta.texto("Elegí cómo querés jugar", 18f, Etiqueta.Rol.SUAVE);

        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(16);
        for (JComponent c : new JComponent[]{titulo, subtitulo, columnas}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(8));
        }
        add(tarjeta);
    }

    // Una columna: mascota, botón, texto corto y lista de cómo funciona.
    private JPanel crearColumna(Image mascota, BotonMenu boton, String detalle, ListaReglas lista) {
        PanelPixel columna = new PanelPixel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setPadding(12);
        ImagenPixel imagen = new ImagenPixel(mascota);
        imagen.setAncho(64);
        Etiqueta texto = Etiqueta.titulo(detalle, 10f, Etiqueta.Rol.SUAVE);
        // Lo de arriba va centrado y la lista a la izquierda.
        for (JComponent c : new JComponent[]{imagen, boton, texto}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            columna.add(c);
            columna.add(Box.createVerticalStrut(8));
        }
        // La lista va pegada arriba en un panel, así el espacio que sobra queda abajo y no entre las reglas.
        JPanel cajaLista = new JPanel(new BorderLayout());
        cajaLista.setOpaque(false);
        cajaLista.add(lista, BorderLayout.NORTH);
        cajaLista.setAlignmentX(Component.CENTER_ALIGNMENT);
        columna.add(cajaLista);
        return columna;
    }

    public void setAlLocal(Runnable accion) { alLocal = accion; }

    public void setAlRed(Runnable accion) { alRed = accion; }
}
