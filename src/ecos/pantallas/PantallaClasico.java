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

// Vista del modo clásico. No guarda el estado del juego: muestra lo que el controlador pide.
public class PantallaClasico extends Pantalla {

    private final PanelPixel hud = new PanelPixel(new BorderLayout(14, 0)); // barra de arriba con el turno y el tiempo
    private final JPanel quien = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // mascota y "Turno de X"
    private final ImagenPixel mascotaTurno = new ImagenPixel(Sprites.mascota(2, 34));
    private final Etiqueta textoTurno = Etiqueta.titulo("Turno de Jugador 1", 11f, Etiqueta.Rol.TEXTO);
    private final BarraTiempo barra = new BarraTiempo(false);
    private final Etiqueta reloj = Etiqueta.titulo("10.0s", 20f, Etiqueta.Rol.TEXTO);
    private final JPanel zona = new JPanel(); // aquí va el panel del jugador en turno
    private final JPanel contenedor = new JPanel(new GridBagLayout());
    private final PanelPixel[] lados = new PanelPixel[2];
    private final TableroJugador[] tableros = new TableroJugador[2];
    private final ChipPixel[][] chips = new ChipPixel[2][3]; // intentos, parejas, monedas
    private final int[] monedasAntes = {-1, -1}; // Últimas monedas de cada jugador (-1 = todavía no se mostró ninguna).
    private final BotonPoder[] ojos = new BotonPoder[2];
    private final int[] mascotas = {2, 7};
    private final String[] nombres = {"Jugador 1", "Invitado"};
    private int n = 6; // cartas por lado
    private int precioOjo = 20;
    private BiConsumer<Integer, Integer> alClicCarta = (p, i) -> { };
    private IntConsumer alClicOjo = p -> { }; // Qué hacer cuando tocan el botón del Ojo espía.

    public PantallaClasico() {
        super("clasico", "clasico");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR - 6, 16, 12, 16));

        // HUD
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

        GridBagConstraints raiz = new GridBagConstraints();
        raiz.anchor = GridBagConstraints.NORTH;
        raiz.weighty = 1;
        add(contenedor, raiz);
        hud.setMaximumSize(new Dimension(1120, 80));
        setLayoutAncho();
    }

    private void setLayoutAncho() {
        hud.setPreferredSize(new Dimension(900, hud.getPreferredSize().height));
    }

    // ---------- lo que el controlador pide ----------

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

    // Aviso cuando no alcanzan las monedas. sinCuenta = el jugador no tiene cuenta.
    public void avisarSinMonedas(int tiene, int precio, boolean sinCuenta) {
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

    // Arma los dos tableros de n x n y las cartas caen en cascada.
    public void configurar(int n, String nombre1, String nombre2, int mascota1, int mascota2, int precioOjo, int[][] caras) {
        monedasAntes[0] = -1; // Nueva partida: se olvidan las monedas anteriores.
        monedasAntes[1] = -1; // Igual para el otro jugador.
        this.n = n;
        this.precioOjo = precioOjo;
        nombres[0] = nombre1;
        nombres[1] = nombre2;
        mascotas[0] = mascota1;
        mascotas[1] = mascota2;
        zona.removeAll();
        int lado = tamanoCarta();
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
        revalidate();
        tableros[0].cascada(0);
        tableros[1].cascada(140);
    }

    private PanelPixel crearLado(int p, int parejas) {
        PanelPixel lado = new PanelPixel();
        lado.setLayout(new BorderLayout(14, 0)); // Columna de datos a la izquierda y el tablero a la derecha (así el tablero puede ser más grande).
        lado.setPadding(8);
        JPanel nm = new JPanel(); // Mascota grande con el nombre debajo.
        nm.setOpaque(false);
        nm.setLayout(new BoxLayout(nm, BoxLayout.Y_AXIS));
        ImagenPixel foto = new ImagenPixel(Sprites.mascota(mascotas[p], 64)); // Mascota del jugador, bien visible.
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
        JPanel datos = new JPanel(); // Columna con todo lo que antes estaba arriba y abajo del tablero.
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
        JPanel arriba = new JPanel(new BorderLayout()); // Pega la columna arriba (no la estira).
        arriba.setOpaque(false);
        arriba.add(datos, BorderLayout.NORTH);
        lado.add(arriba, BorderLayout.WEST);
        lado.add(tableros[p], BorderLayout.CENTER);
        return lado;
    }

    private static String recortar(String s) {
        return s.length() > 12 ? s.substring(0, 11) + "…" : s;
    }

    // Tamaño de carta según el espacio de la ventana.
    private int tamanoCarta() {
        int w = getWidth() > 0 ? getWidth() : 1280;
        int h = getHeight() > 0 ? getHeight() : 720;
        int porAncho = (int) ((w - 96 - 290) / (n + 1.2)); // 290 = ancho de la columna de datos.
        int porAlto = (int) ((h - 208) / (n + 0.6)); // 208 = lo que ocupan la barra de arriba y los márgenes.
        return Math.max(36, Math.min(n == 4 ? 170 : 124, Math.min(porAncho, porAlto)));
    }

    // Muestra el tablero del jugador en turno (p = 0 o 1) y lo deja jugable.
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

    // Congela los clics (por ejemplo mientras se esperan dos cartas distintas).
    public void bloquear(boolean bloqueado) {
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
        ecos.recursos.Sonido.efecto("par_encontrado"); // Suena cuando se encuentra una pareja.
        tableros[p].getCarta(i).marcarHecha();
        tableros[p].getCarta(j).marcarHecha();
    }

    // Voltea todas las cartas del tablero un momento (el comodín).
    public void ojoEspia(int p) {
        ecos.recursos.Sonido.efecto("ojo_espia"); // Suena cuando se usa el ojo espía.
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
        if (monedasAntes[p] >= 0 && monedas > monedasAntes[p]) { // Solo si las monedas SUBIERON (no al empezar ni al gastar).
            ecos.recursos.Sonido.efecto("moneda"); // Suena la moneda.
        }
        monedasAntes[p] = monedas; // Guarda el valor para comparar la próxima vez.
        chips[p][2].setTexto("" + monedas);
        ojos[p].setSinFondos(monedas < precioOjo); // Apagado si no alcanza, pero se puede tocar para ver el aviso.
    }

    public void mostrarBannerTurno(int p) {
        AvisoDeslizante.mostrar(this, "TURNO DE " + nombres[p].toUpperCase(), p == 0 ? AvisoDeslizante.Estilo.JUGADOR1 : AvisoDeslizante.Estilo.JUGADOR2, Sprites.mascota(mascotas[p], 44));
    }

    public void mostrarVictoria(int ganador, int intentos1, int intentos2, String premio, String extra, Runnable verPremios, Runnable jugarOtra) {
        ecos.recursos.Sonido.efecto("victoria"); // Suena cuando termina la partida.
        Confeti.lanzar(this, 190, 5200);
        ecos.app.modos.JuegoC.VentanaVictoria.mostrar(this, nombres[ganador], Sprites.mascota(mascotas[0], 32), nombres[0], intentos1,
                Sprites.mascota(mascotas[1], 32), nombres[1], intentos2, ganador == 0, premio, extra, verPremios, jugarOtra);
    }
}