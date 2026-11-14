package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.modos.Patrones.AvisoDeslizante;
import ecos.app.modos.JuegoC.BarraTiempo;
import ecos.componentes.BotonPoder;
import ecos.componentes.ChipPixel;
import ecos.componentes.Confeti;
import ecos.componentes.DialogoComprarOjo;
import ecos.componentes.Etiqueta;
import ecos.componentes.ImagenPixel;
import ecos.componentes.PanelPixel;
import ecos.app.modos.JuegoC.TableroJugador;
import ecos.recursos.Sprites;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

// Vista del modo clásico, con un tablero por jugador. No guarda el estado del juego, solo muestra lo que pide el
// controlador.
public class PantallaClasico extends Pantalla {

    // Barra de arriba con el turno y el tiempo: mascota del jugador en turno y texto "Turno de X".
    private final PanelPixel hud = new PanelPixel(new BorderLayout(14, 0));
    private final JPanel quien = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final ImagenPixel mascotaTurno = new ImagenPixel(Sprites.mascota(2, 34));
    private final Etiqueta textoTurno = Etiqueta.titulo("Turno de Jugador 1", 11f, Etiqueta.Rol.TEXTO);
    // Barra y reloj de la jugada, segundos que quedan, panel del jugador en turno y contenedor principal.
    private final BarraTiempo barra = new BarraTiempo(false);
    private final Etiqueta reloj = Etiqueta.titulo("10.0s", 20f, Etiqueta.Rol.TEXTO);
    private final JPanel zona = new JPanel();
    private final JPanel contenedor = new JPanel(new GridBagLayout());
    // Una caja por jugador con sus datos y su tablero, y las últimas monedas mostradas (-1 si todavía no se mostró
    // ninguna).
    private final PanelPixel[] lados = new PanelPixel[2];
    private final TableroJugador[] tableros = new TableroJugador[2];
    private final ChipPixel[][] chips = new ChipPixel[2][3];
    private final int[] monedasAntes = {-1, -1};
    // Botón del Ojo espía, mascota y nombre de cada jugador y cartas por lado.
    private final BotonPoder[] ojos = new BotonPoder[2];
    private final int[] mascotas = {2, 7};
    private final String[] nombres = {"Jugador 1", "Invitado"};
    private int n = 6;
    // Precio del Ojo espía y acciones al tocar una carta o el Ojo espía, que pone el controlador.
    private int precioOjo = 30;
    private BiConsumer<Integer, Integer> alClicCarta = (p, i) -> { };
    private IntConsumer alClicOjo = p -> { };

    public PantallaClasico() {
        super("clasico", "clasico");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR - 6, 16, 12, 16));

        // Barra de arriba: quién juega a la izquierda, tiempo al centro y reloj a la derecha.
        quien.setOpaque(false);
        quien.add(mascotaTurno);
        quien.add(textoTurno);
        reloj.setHorizontalAlignment(Etiqueta.RIGHT);
        reloj.setPreferredSize(new Dimension(130, 28));
        hud.setPadding(6);
        hud.add(quien, BorderLayout.WEST);
        JPanel centroBarra = new JPanel(new GridBagLayout());
        centroBarra.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        centroBarra.add(barra, g);
        hud.add(centroBarra, BorderLayout.CENTER);
        hud.add(reloj, BorderLayout.EAST);

        // Zona de los tableros; solo se ve el del jugador en turno.
        zona.setOpaque(false);
        zona.setLayout(new BoxLayout(zona, BoxLayout.Y_AXIS));

        contenedor.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        contenedor.add(hud, c);
        c.gridy = 1;
        c.insets = new Insets(14, 0, 0, 0);
        c.fill = GridBagConstraints.NONE;
        contenedor.add(zona, c);

        // Se centra arriba y se limita el ancho de la barra.
        GridBagConstraints raiz = new GridBagConstraints();
        raiz.anchor = GridBagConstraints.NORTH;
        raiz.weighty = 1;
        add(contenedor, raiz);
        hud.setMaximumSize(new Dimension(1120, 80));
        setLayoutAncho();
    }

    private void setLayoutAncho() {
        // Ancho de 900.
        hud.setPreferredSize(new Dimension(900, hud.getPreferredSize().height));
    }

    // Lo que pide el controlador.

    public void setAlClicCarta(BiConsumer<Integer, Integer> accion) {
        this.alClicCarta = accion;
    }

    public void setAlClicOjo(IntConsumer accion) {
        this.alClicOjo = accion;
    }

    // Pregunta si quiere comprar el Ojo espía; si acepta se ejecuta alComprar.
    public void confirmarCompraOjo(int precio, Runnable alComprar) {
        new DialogoComprarOjo(precio, alComprar).mostrar(this);
    }

    // Aviso cuando no alcanzan las monedas; sinCuenta es true si el jugador no tiene cuenta.
    public void avisarSinMonedas(int tiene, int precio, boolean sinCuenta) {
        // Sin cuenta se explica cómo conseguir monedas; con cuenta se dice cuántas tiene y cuánto cuesta.
        ecos.recursos.Sonido.efecto("sin_monedas");
        String mensaje = sinCuenta
                ? "Estás jugando sin cuenta, así que no tenés monedas. Iniciá sesión en la próxima partida para ganarlas y comprar el Ojo espía."
                : "Tenés <b>" + tiene + "</b> monedas y el Ojo espía cuesta <b>" + precio + "</b>. ¡Seguí jugando y ganá partidas para conseguir más!";
        new ecos.componentes.DialogoAviso("¡Te faltan monedas!", mensaje, Sprites.ui("coin", 48)).mostrar(this);
    }

    // Aviso cuando el Ojo espía no se puede usar en este momento.
    public void avisarOjoNoDisponible() {
        ecos.recursos.Sonido.efecto("error");
        new ecos.componentes.DialogoAviso("Ojo espía", "Solo se puede usar en tu turno y antes de voltear una carta.", Sprites.ui("eye", 48)).mostrar(this);
    }

    // Prepara la pantalla para una partida nueva con dos tableros de n x n.
    public void configurar(int n, String nombre1, String nombre2, int mascota1, int mascota2, int precioOjo, int[][] caras) {
        monedasAntes[0] = -1;
        monedasAntes[1] = -1;
        this.n = n;
        this.precioOjo = precioOjo;
        nombres[0] = nombre1;
        nombres[1] = nombre2;
        mascotas[0] = mascota1;
        mascotas[1] = mascota2;
        zona.removeAll();
        int lado = tamanoCarta();
        // Cada jugador tiene su tablero, con la cara de cada carta y el clic conectado al controlador.
        for (int p = 0; p < 2; p++) {
            final int jugador = p;
            tableros[p] = new TableroJugador(n, lado);
            for (int i = 0; i < n * n; i++) {
                final int carta = i;
                tableros[p].getCarta(i).setCara(Sprites.icono(caras[p][i] + 1, 128));
                tableros[p].getCarta(i).setAlClic(() -> alClicCarta.accept(jugador, carta));
            }
            lados[p] = crearLado(p, n * n / 2);
            zona.add(lados[p]);
            lados[p].setVisible(false);
        }
        // Las cartas del jugador 1 caen primero y las del jugador 2 un poco después.
        revalidate();
        tableros[0].cascada(0);
        tableros[1].cascada(140);
    }

    private PanelPixel crearLado(int p, int parejas) {
        // Caja del jugador: datos a la izquierda y tablero a la derecha, con la mascota grande y el nombre debajo.
        PanelPixel lado = new PanelPixel();
        lado.setLayout(new BorderLayout(14, 0));
        lado.setPadding(8);
        JPanel nm = new JPanel();
        nm.setOpaque(false);
        nm.setLayout(new BoxLayout(nm, BoxLayout.Y_AXIS));
        ImagenPixel foto = new ImagenPixel(Sprites.mascota(mascotas[p], 64));
        // Mascota, nombre del jugador y sus tres chips: intentos, parejas y monedas.
        foto.setAlignmentX(Component.LEFT_ALIGNMENT);
        nm.add(foto);
        nm.add(Box.createVerticalStrut(8));
        Etiqueta nombre = Etiqueta.titulo(recortar(nombres[p]), 13f, Etiqueta.Rol.TEXTO);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        nm.add(nombre);
        chips[p][0] = new ChipPixel("Intentos 0", 11f, true, ChipPixel.Modo.CAJA, 3);
        chips[p][1] = new ChipPixel("Parejas 0/" + parejas, 11f, true, ChipPixel.Modo.CAJA, 3);
        chips[p][2] = new ChipPixel("0", 11f, true, ChipPixel.Modo.CAJA, 3);
        chips[p][2].setIcono(Sprites.ui("coin", 20));
        ojos[p] = new BotonPoder("Ojo espía", precioOjo);
        ojos[p].addActionListener(e -> alClicOjo.accept(p));
        JPanel datos = new JPanel();
        // Nombre, chips y botón del ojo uno debajo del otro, alineados a la izquierda.
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        nm.setAlignmentX(Component.LEFT_ALIGNMENT);
        datos.add(nm);
        datos.add(Box.createVerticalStrut(18));
        for (ChipPixel ch : chips[p]) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            fila.setOpaque(false);
            fila.add(ch);
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            datos.add(fila);
            datos.add(Box.createVerticalStrut(12));
        }
        datos.add(Box.createVerticalStrut(10));
        JPanel poderes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        poderes.setOpaque(false);
        poderes.add(ojos[p]);
        poderes.setAlignmentX(Component.LEFT_ALIGNMENT);
        datos.add(poderes);
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(datos, BorderLayout.NORTH);
        lado.add(arriba, BorderLayout.WEST);
        lado.add(tableros[p], BorderLayout.CENTER);
        return lado;
    }

    private static String recortar(String s) {
        // Si pasa de 12 letras, deja 11 y agrega puntos suspensivos.
        return s.length() > 12 ? s.substring(0, 11) + "…" : s;
    }

    // Tamaño de carta según el espacio de la ventana.
    private int tamanoCarta() {
        // Usa 1280 x 720 si la ventana todavía no tiene tamaño. 290 es la columna de datos y 208 la barra de arriba y
        // los márgenes.
        int w = getWidth() > 0 ? getWidth() : 1280;
        int h = getHeight() > 0 ? getHeight() : 720;
        int porAncho = (int) ((w - 96 - 290) / (n + 1.2));
        int porAlto = (int) ((h - 208) / (n + 0.6));
        return Math.max(36, Math.min(n == 4 ? 170 : 124, Math.min(porAncho, porAlto)));
    }

    public void setTurno(int p) {
        for (int i = 0; i < 2; i++) {
            if (lados[i] != null) {
                lados[i].setVisible(i == p);
                lados[i].setColorBorde(i == p ? ecos.app.Tema.bordeExterior() : null);
                tableros[i].setActivo(i == p);
            }
        }
        mascotaTurno.setImagen(Sprites.mascota(mascotas[p], 34));
        textoTurno.setText("Turno de " + recortar(nombres[p]));
        revalidate();
        repaint();
    }

    public void bloquear(boolean bloqueado) {
        // Solo se puede jugar en el tablero del turno y si no está bloqueado.
        for (TableroJugador t : tableros) {
            if (t != null && t.isVisible()) {
                t.setActivo(!bloqueado && lados[0].isVisible() == (t == tableros[0]));
            }
        }
    }

    public void setTiempo(double t, double max, boolean animar) {
        barra.setTiempo(t, max, animar);
        reloj.setText(String.format(java.util.Locale.US, "%.1fs", t));
    }

    public void mostrarBonus(String texto) {
        barra.mostrarBonus(texto);
    }

    public void voltearCarta(int p, int i, boolean arriba) {
        tableros[p].getCarta(i).voltear(arriba);
    }

    public void marcarPareja(int p, int i, int j) {
        ecos.recursos.Sonido.efecto("par_encontrado");
        tableros[p].getCarta(i).marcarHecha();
        tableros[p].getCarta(j).marcarHecha();
    }

    // Muestra todas las cartas un momento, es el Ojo espía.
    public void ojoEspia(int p) {
        ecos.recursos.Sonido.efecto("ojo_espia");
        tableros[p].voltearTodas(true);
        javax.swing.Timer t = new javax.swing.Timer(1700, e -> tableros[p].voltearTodas(false));
        t.setRepeats(false);
        t.start();
    }

    public void setIntentos(int p, int intentos) {
        chips[p][0].setTexto("Intentos " + intentos);
    }

    public void setParejas(int p, int hechas) {
        chips[p][1].setTexto("Parejas " + hechas + "/" + n * n / 2);
    }

    public void setMonedas(int p, int monedas) {
        // Solo suena si las monedas subieron, no al empezar ni al gastar.
        if (monedasAntes[p] >= 0 && monedas > monedasAntes[p]) {
            ecos.recursos.Sonido.efecto("moneda");
        }
        // Guarda el valor para comparar la próxima vez. El Ojo se ve apagado si no alcanzan las monedas, pero se puede
        // tocar para ver el aviso.
        monedasAntes[p] = monedas;
        chips[p][2].setTexto("" + monedas);
        ojos[p].setSinFondos(monedas < precioOjo);
    }

    public void mostrarBannerTurno(int p) {
        AvisoDeslizante.mostrar(this, "TURNO DE " + nombres[p].toUpperCase(), p == 0 ? AvisoDeslizante.Estilo.JUGADOR1 : AvisoDeslizante.Estilo.JUGADOR2, Sprites.mascota(mascotas[p], 44));
    }

    public void mostrarVictoria(int ganador, int intentos1, int intentos2, String premio, String extra, Runnable verPremios, Runnable jugarOtra) {
        ecos.recursos.Sonido.efecto("victoria");
        Confeti.lanzar(this, 190, 5200);
        ecos.app.modos.JuegoC.VentanaVictoria.mostrar(this, nombres[ganador], Sprites.mascota(mascotas[0], 32), nombres[0], intentos1,
                Sprites.mascota(mascotas[1], 32), nombres[1], intentos2, ganador == 0, premio, extra, verPremios, jugarOtra);
    }
}
