package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
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
// Librería gráfica AWT, BorderLayout para acomodar por zonas, Component para cualquier elemento de la pantalla,
// Dimension para los tamaños, FlowLayout para acomodar en fila, GridBagConstraints para las reglas de la cuadrícula
// flexible, GridBagLayout para acomodar en una cuadrícula flexible y Insets para los márgenes.
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
// Acciones de Java, BiConsumer para una acción que recibe dos datos y IntConsumer para una acción que recibe un número
// entero.
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna y JPanel para los paneles.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

// Vista del modo clásico. No guarda el estado del juego, muestra lo que el controlador pide.
// Pantalla de la partida clásica.
public class PantallaClasico extends Pantalla {

    // Barra de arriba con el turno y el tiempo, con la mascota del jugador que tiene el turno y el texto "Turno de X".
    private final PanelPixel hud = new PanelPixel(new BorderLayout(14, 0));
    private final JPanel quien = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final ImagenPixel mascotaTurno = new ImagenPixel(Sprites.mascota(2, 34));
    private final Etiqueta textoTurno = Etiqueta.titulo("Turno de Jugador 1", 11f, Etiqueta.Rol.TEXTO);
    // Barra y reloj de la jugada, con los segundos que quedan, el panel del jugador en turno y el contenedor principal de la
    // pantalla.
    private final BarraTiempo barra = new BarraTiempo(false);
    private final Etiqueta reloj = Etiqueta.titulo("10.0s", 20f, Etiqueta.Rol.TEXTO);
    private final JPanel zona = new JPanel();
    private final JPanel contenedor = new JPanel(new GridBagLayout());
    // Una caja por jugador con sus datos (intentos, parejas, monedas) y su tablero, y las últimas monedas mostradas de cada
    // uno (-1 si todavía no se mostró ninguna).
    private final PanelPixel[] lados = new PanelPixel[2];
    private final TableroJugador[] tableros = new TableroJugador[2];
    private final ChipPixel[][] chips = new ChipPixel[2][3];
    private final int[] monedasAntes = {-1, -1};
    // Botón del Ojo espía, número de mascota y nombre de cada jugador, y cartas por lado.
    private final BotonPoder[] ojos = new BotonPoder[2];
    private final int[] mascotas = {2, 7};
    private final String[] nombres = {"Jugador 1", "Invitado"};
    private int n = 6;
    // Precio del Ojo espía en monedas, y qué hacer al tocar una carta y al tocar el botón del Ojo espía, ambos puestos por
    // el controlador.
    private int precioOjo = 30;
    private BiConsumer<Integer, Integer> alClicCarta = (p, i) -> { };
    private IntConsumer alClicOjo = p -> { };

    // Arma la pantalla.
    public PantallaClasico() {
        // Le pone id y música a la pantalla. Usa GridBagLayout para centrar todo. Deja el margen de la pantalla.
        super("clasico", "clasico");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR - 6, 16, 12, 16));

        // HUD
        // Arma la barra de arriba, quién juega a la izquierda, la barra de tiempo al centro y el reloj a la derecha.
        // La barra de arriba tiene tres partes, quién juega (izquierda), la barra de tiempo (centro) y el reloj (derecha).
        // La parte de quién juega no tiene fondo propio. Agrega la mascota del turno. Agrega el texto del turno. El reloj va
        // pegado a la derecha.
        quien.setOpaque(false);
        quien.add(mascotaTurno);
        quien.add(textoTurno);
        reloj.setHorizontalAlignment(Etiqueta.RIGHT);
        // Le da un tamaño fijo al reloj. Deja un margen adentro de la barra de arriba. Quién juega va a la izquierda. Panel
        // para centrar la barra de tiempo.
        reloj.setPreferredSize(new Dimension(130, 28));
        hud.setPadding(6);
        hud.add(quien, BorderLayout.WEST);
        JPanel centroBarra = new JPanel(new GridBagLayout());
        // Sin fondo propio. Reglas de acomodo para la barra. La barra puede estirarse a lo ancho. Se estira solo en horizontal.
        centroBarra.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        // Agrega la barra de tiempo. La barra de tiempo va al centro. El reloj va a la derecha.
        centroBarra.add(barra, g);
        hud.add(centroBarra, BorderLayout.CENTER);
        hud.add(reloj, BorderLayout.EAST);

        // La zona donde van los dos tableros, uno al lado del otro.
        // La zona de los tableros no tiene fondo propio. Pone los tableros uno debajo del otro.
        zona.setOpaque(false);
        zona.setLayout(new BoxLayout(zona, BoxLayout.Y_AXIS));

        // El contenedor pone la barra de arriba y debajo la zona de los tableros.
        // El contenedor no tiene fondo propio. Reglas de acomodo del contenedor. Empieza en la primera columna. Se puede
        // estirar a lo ancho.
        contenedor.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        // Se estira solo en horizontal. Primera fila, para la barra de arriba. Agrega la barra de arriba. Segunda fila, para
        // los tableros.
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        contenedor.add(hud, c);
        c.gridy = 1;
        // Deja un espacio arriba de los tableros. Los tableros no se estiran. Agrega la zona de los tableros.
        c.insets = new Insets(14, 0, 0, 0);
        c.fill = GridBagConstraints.NONE;
        contenedor.add(zona, c);

        // Se centra todo arriba y se limita el ancho de la barra, con las reglas de acomodo de toda la pantalla y el espacio
        // sobrante abajo. Después se agrega el contenedor.
        GridBagConstraints raiz = new GridBagConstraints();
        raiz.anchor = GridBagConstraints.NORTH;
        raiz.weighty = 1;
        add(contenedor, raiz);
        // Limita el tamaño máximo de la barra de arriba. Ajusta el ancho de la barra de arriba.
        hud.setMaximumSize(new Dimension(1120, 80));
        setLayoutAncho();
    }

    // Calcula el ancho de la barra de arriba.
    private void setLayoutAncho() {
        // Ancho de 900 y el alto que ya tenía.
        hud.setPreferredSize(new Dimension(900, hud.getPreferredSize().height));
    }

    // Lo que pide el controlador.

    // Guarda qué hacer al tocar una carta.
    public void setAlClicCarta(BiConsumer<Integer, Integer> accion) {
        // Guarda la acción que manda el controlador.
        this.alClicCarta = accion;
    }

    // Guarda qué hacer al tocar el Ojo espía.
    public void setAlClicOjo(IntConsumer accion) {
        // Guarda la acción que manda el controlador.
        this.alClicOjo = accion;
    }

    // Pregunta si quiere comprar el Ojo espía; si acepta se ejecuta alComprar.
    // Abre la ventana para confirmar la compra del Ojo.
    public void confirmarCompraOjo(int precio, Runnable alComprar) {
        // Crea la ventana con el precio y la muestra.
        new DialogoComprarOjo(precio, alComprar).mostrar(this);
    }

    // Aviso cuando no alcanzan las monedas. sinCuenta = el jugador no tiene cuenta.
    // Avisa que no alcanzan las monedas para el Ojo.
    public void avisarSinMonedas(int tiene, int precio, boolean sinCuenta) {
        // Suena el sonido de sin monedas. Elige el mensaje según si juega con cuenta o no. Si juega sin cuenta, le explica
        // cómo conseguir monedas. Si tiene cuenta, le dice cuántas tiene y cuánto cuesta el Ojo.
        ecos.recursos.Sonido.efecto("sin_monedas");
        String mensaje = sinCuenta
                ? "Estás jugando sin cuenta, así que no tenés monedas. Iniciá sesión en la próxima partida para ganarlas y comprar el Ojo espía."
                : "Tenés <b>" + tiene + "</b> monedas y el Ojo espía cuesta <b>" + precio + "</b>. ¡Seguí jugando y ganá partidas para conseguir más!";
        // Muestra el aviso con el ícono de la moneda.
        new ecos.componentes.DialogoAviso("¡Te faltan monedas!", mensaje, Sprites.ui("coin", 48)).mostrar(this);
    }

    // Aviso cuando el Ojo espía no se puede usar en este momento.
    // Avisa que el Ojo no se puede usar en este momento.
    public void avisarOjoNoDisponible() {
        // Suena el sonido de error. Muestra el aviso con el ícono del ojo.
        ecos.recursos.Sonido.efecto("error");
        new ecos.componentes.DialogoAviso("Ojo espía", "Solo se puede usar en tu turno y antes de voltear una carta.", Sprites.ui("eye", 48)).mostrar(this);
    }

    // Arma los dos tableros de n x n y las cartas caen en cascada.
    // Prepara la pantalla con los datos de una partida nueva.
    public void configurar(int n, String nombre1, String nombre2, int mascota1, int mascota2, int precioOjo, int[][] caras) {
        // Nueva partida, se olvidan las monedas anteriores. Igual para el otro jugador.
        monedasAntes[0] = -1;
        monedasAntes[1] = -1;
        // Guarda los datos de la partida (tamaño, nombres, mascotas y precio del ojo) y arma los dos tableros con las caras
        // que mandó el controlador.
        // Se guardan los datos de la partida y se deja la zona lista para armar los tableros de nuevo.
        // Guarda el tamaño del tablero. Guarda el precio del Ojo. Guarda el nombre del jugador 1. Guarda el nombre del jugador 2.
        this.n = n;
        this.precioOjo = precioOjo;
        nombres[0] = nombre1;
        nombres[1] = nombre2;
        // Guarda la mascota del jugador 1. Guarda la mascota del jugador 2. Quita los tableros de la partida anterior.
        // Calcula qué tan grandes deben ser las cartas.
        mascotas[0] = mascota1;
        mascotas[1] = mascota2;
        zona.removeAll();
        int lado = tamanoCarta();
        // Por cada jugador se crea su tablero, se le pone la cara a cada carta y se conecta el clic con el controlador.
        // Se repite una vez por jugador.
        for (int p = 0; p < 2; p++) {
            // Guarda el número del jugador para usarlo en el clic.
            final int jugador = p;
            // Crea el tablero del jugador. Recorre todas las cartas del tablero.
            tableros[p] = new TableroJugador(n, lado);
            for (int i = 0; i < n * n; i++) {
                // Guarda el número de la carta para usarlo en el clic.
                final int carta = i;
                // Le pone a la carta su imagen. Al tocar la carta avisa al controlador cuál fue.
                tableros[p].getCarta(i).setCara(Sprites.icono(caras[p][i] + 1, 128));
                tableros[p].getCarta(i).setAlClic(() -> alClicCarta.accept(jugador, carta));
            }
            // Crea la caja del jugador con sus datos y su tablero. La agrega a la zona. Al inicio ninguna caja se ve.
            lados[p] = crearLado(p, n * n / 2);
            zona.add(lados[p]);
            lados[p].setVisible(false);
        }
        // Se muestran los tableros y las cartas caen en cascada, primero las del jugador 1 y después las del 2.
        // Vuelve a calcular el acomodo. Las cartas del jugador 1 caen enseguida. Las del jugador 2 caen un poco después.
        revalidate();
        tableros[0].cascada(0);
        tableros[1].cascada(140);
    }

    // Arma la caja de un jugador con sus datos y su tablero.
    private PanelPixel crearLado(int p, int parejas) {
        // Caja del jugador con la columna de datos a la izquierda y el tablero a la derecha, así el tablero puede ser más
        // grande, con margen adentro y la mascota grande con el nombre debajo.
        PanelPixel lado = new PanelPixel();
        lado.setLayout(new BorderLayout(14, 0));
        lado.setPadding(8);
        JPanel nm = new JPanel();
        // Panel con la foto y el nombre. Pone la foto y el nombre uno debajo del otro. Mascota del jugador, bien visible.
        nm.setOpaque(false);
        nm.setLayout(new BoxLayout(nm, BoxLayout.Y_AXIS));
        ImagenPixel foto = new ImagenPixel(Sprites.mascota(mascotas[p], 64));
        // Foto de la mascota, nombre del jugador y sus tres chips, intentos, parejas y monedas.
        // Alinea la foto a la izquierda. Agrega la foto. Deja un espacio debajo de la foto. Texto con el nombre (recortado si
        // es muy largo).
        foto.setAlignmentX(Component.LEFT_ALIGNMENT);
        nm.add(foto);
        nm.add(Box.createVerticalStrut(8));
        Etiqueta nombre = Etiqueta.titulo(recortar(nombres[p]), 13f, Etiqueta.Rol.TEXTO);
        // Alinea el nombre a la izquierda. Agrega el nombre. Chip de los intentos. Chip de las parejas encontradas.
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        nm.add(nombre);
        chips[p][0] = new ChipPixel("Intentos 0", 11f, true, ChipPixel.Modo.CAJA, 3);
        chips[p][1] = new ChipPixel("Parejas 0/" + parejas, 11f, true, ChipPixel.Modo.CAJA, 3);
        // Chip de las monedas. Le pone el ícono de la moneda.
        chips[p][2] = new ChipPixel("0", 11f, true, ChipPixel.Modo.CAJA, 3);
        chips[p][2].setIcono(Sprites.ui("coin", 20));
        // El botón del Ojo espía avisa al controlador qué jugador lo tocó.
        // Botón del Ojo espía de este jugador. Al tocarlo avisa al controlador qué jugador fue. Columna con todo lo que antes
        // estaba arriba y abajo del tablero.
        ojos[p] = new BotonPoder("Ojo espía", precioOjo);
        ojos[p].addActionListener(e -> alClicOjo.accept(p));
        JPanel datos = new JPanel();
        // Se agrupan nombre, chips y botón del ojo uno debajo del otro, alineados a la izquierda.
        // Panel con todos los datos del jugador. Los pone uno debajo del otro. Alinea el nombre a la izquierda. Agrega la
        // foto y el nombre.
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        nm.setAlignmentX(Component.LEFT_ALIGNMENT);
        datos.add(nm);
        // Deja un espacio. Recorre los tres chips. Fila que contiene un chip. Sin fondo propio.
        datos.add(Box.createVerticalStrut(18));
        for (ChipPixel ch : chips[p]) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            fila.setOpaque(false);
            // Agrega el chip a la fila. Alinea la fila a la izquierda. Agrega la fila a los datos. Deja un espacio entre los chips.
            fila.add(ch);
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            datos.add(fila);
            datos.add(Box.createVerticalStrut(12));
        }
        // Deja un espacio antes del botón. Fila para el botón del Ojo. Sin fondo propio. Agrega el botón del Ojo.
        datos.add(Box.createVerticalStrut(10));
        JPanel poderes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        poderes.setOpaque(false);
        poderes.add(ojos[p]);
        // Alinea la fila a la izquierda. Agrega la fila a los datos. Pega la columna arriba (no la estira). El panel de datos
        // no tiene fondo propio.
        poderes.setAlignmentX(Component.LEFT_ALIGNMENT);
        datos.add(poderes);
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        // Los datos van arriba. Los datos van a la izquierda de la caja. El tablero va al centro de la caja. Entrega la caja ya armada.
        arriba.add(datos, BorderLayout.NORTH);
        lado.add(arriba, BorderLayout.WEST);
        lado.add(tableros[p], BorderLayout.CENTER);
        return lado;
    }

    // Recorta nombres largos para que quepan.
    private static String recortar(String s) {
        // Si pasa de 12 letras, deja 11 y agrega puntos suspensivos.
        return s.length() > 12 ? s.substring(0, 11) + "…" : s;
    }

    // Tamaño de carta según el espacio de la ventana.
    // Calcula el tamaño de cada carta según la ventana.
    private int tamanoCarta() {
        // Ancho de la ventana (o 1280 si todavía no tiene). Alto de la ventana (o 720 si todavía no tiene). 290 = ancho de la
        // columna de datos. 208 = lo que ocupan la barra de arriba y los márgenes.
        int w = getWidth() > 0 ? getWidth() : 1280;
        int h = getHeight() > 0 ? getHeight() : 720;
        int porAncho = (int) ((w - 96 - 290) / (n + 1.2));
        int porAlto = (int) ((h - 208) / (n + 0.6));
        // Usa el tamaño más chico que quepa, con un mínimo y un máximo.
        return Math.max(36, Math.min(n == 4 ? 170 : 124, Math.min(porAncho, porAlto)));
    }

    // Muestra el tablero del jugador en turno (p = 0 o 1) y lo deja jugable.
    // Cambia de quién es el turno.
    public void setTurno(int p) {
        // Revisa las dos cajas. Solo si la caja ya existe. Muestra solo la caja del jugador con el turno. Le pone borde a la
        // caja de quien juega.
        for (int i = 0; i < 2; i++) {
            if (lados[i] != null) {
                lados[i].setVisible(i == p);
                lados[i].setColorBorde(i == p ? ecos.app.Tema.bordeExterior() : null);
                // Activa solo el tablero de quien juega.
                tableros[i].setActivo(i == p);
            }
        }
        // Cambia la mascota del turno. Cambia el texto del turno. Vuelve a calcular el acomodo. Vuelve a dibujar la pantalla.
        mascotaTurno.setImagen(Sprites.mascota(mascotas[p], 34));
        textoTurno.setText("Turno de " + recortar(nombres[p]));
        revalidate();
        repaint();
    }

    // Congela los clics (por ejemplo mientras se esperan dos cartas distintas).
    // Métodos que usa el controlador, bloquear los tableros, cambiar el tiempo y el bonus, voltear y marcar cartas.
    // Bloquea o desbloquea los tableros.
    public void bloquear(boolean bloqueado) {
        // Revisa los dos tableros. Solo si el tablero existe y se está viendo. Activa solo el tablero del jugador del turno y
        // solo si no está bloqueado.
        for (TableroJugador t : tableros) {
            if (t != null && t.isVisible()) {
                t.setActivo(!bloqueado && lados[0].isVisible() == (t == tableros[0]));
            }
        }
    }

    // Actualiza la barra y el reloj de tiempo.
    public void setTiempo(double t, double max, boolean animar) {
        // Le pasa el tiempo a la barra. Escribe los segundos con un decimal.
        barra.setTiempo(t, max, animar);
        reloj.setText(String.format(java.util.Locale.US, "%.1fs", t));
    }

    // Muestra el bonus de tiempo en la barra.
    public void mostrarBonus(String texto) {
        // Se lo pasa a la barra.
        barra.mostrarBonus(texto);
    }

    // Voltea una carta de un jugador.
    public void voltearCarta(int p, int i, boolean arriba) {
        // Busca la carta y la voltea.
        tableros[p].getCarta(i).voltear(arriba);
    }

    // Marca dos cartas como pareja encontrada.
    public void marcarPareja(int p, int i, int j) {
        // Suena cuando se encuentra una pareja. Marca la primera carta. Marca la segunda carta.
        ecos.recursos.Sonido.efecto("par_encontrado");
        tableros[p].getCarta(i).marcarHecha();
        tableros[p].getCarta(j).marcarHecha();
    }

    // Voltea todas las cartas del tablero un momento (el comodín).
    // Muestra todas las cartas por un momento.
    public void ojoEspia(int p) {
        // Suena cuando se usa el ojo espía. Voltea todas las cartas hacia arriba. Reloj que las vuelve a tapar después de 1.7
        // segundos. Se ejecuta una sola vez.
        ecos.recursos.Sonido.efecto("ojo_espia");
        tableros[p].voltearTodas(true);
        javax.swing.Timer t = new javax.swing.Timer(1700, e -> tableros[p].voltearTodas(false));
        t.setRepeats(false);
        t.start();
    }

    // Cambia el chip de intentos.
    public void setIntentos(int p, int intentos) {
        // Escribe la cantidad de intentos.
        chips[p][0].setTexto("Intentos " + intentos);
    }

    // Cambia el chip de parejas.
    public void setParejas(int p, int hechas) {
        // Escribe las parejas hechas y el total.
        chips[p][1].setTexto("Parejas " + hechas + "/" + n * n / 2);
    }

    // Cambia el chip de monedas.
    public void setMonedas(int p, int monedas) {
        // Solo si las monedas SUBIERON (no al empezar ni al gastar). Suena la moneda.
        if (monedasAntes[p] >= 0 && monedas > monedasAntes[p]) {
            ecos.recursos.Sonido.efecto("moneda");
        }
        // Guarda el valor para comparar la próxima vez. Escribe la cantidad de monedas. Apagado si no alcanza, pero se puede
        // tocar para ver el aviso.
        monedasAntes[p] = monedas;
        chips[p][2].setTexto("" + monedas);
        ojos[p].setSinFondos(monedas < precioOjo);
    }

    // Muestra el aviso de quién tiene el turno.
    public void mostrarBannerTurno(int p) {
        // Crea el aviso con el nombre en mayúscula.
        AvisoDeslizante.mostrar(this, "TURNO DE " + nombres[p].toUpperCase(), p == 0 ? AvisoDeslizante.Estilo.JUGADOR1 : AvisoDeslizante.Estilo.JUGADOR2, Sprites.mascota(mascotas[p], 44));
    }

    // Abre la ventana de victoria.
    public void mostrarVictoria(int ganador, int intentos1, int intentos2, String premio, String extra, Runnable verPremios, Runnable jugarOtra) {
        // Suena cuando termina la partida. Lanza el confeti. Muestra la ventana con el ganador y los datos de los dos.
        ecos.recursos.Sonido.efecto("victoria");
        Confeti.lanzar(this, 190, 5200);
        ecos.app.modos.JuegoC.VentanaVictoria.mostrar(this, nombres[ganador], Sprites.mascota(mascotas[0], 32), nombres[0], intentos1,
                Sprites.mascota(mascotas[1], 32), nombres[1], intentos2, ganador == 0, premio, extra, verPremios, jugarOtra);
    }
}