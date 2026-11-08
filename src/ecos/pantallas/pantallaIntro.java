package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.BarraCarga;
import ecos.componentes.BotonMenu;
import ecos.componentes.ImagenPixel;
import ecos.componentes.TextoMaquina;
import ecos.recursos.Fuentes;
import ecos.recursos.Sonido;
import ecos.recursos.Sprites;
// Librería gráfica AWT, BorderLayout para acomodar por zonas, Color para los colores, Component para cualquier elemento
// de la pantalla, Dimension para los tamaños, FlowLayout para acomodar en fila, Graphics para dibujar, Graphics2D para
// dibujar con más opciones, GridBagLayout para acomodar en una cuadrícula flexible y RadialGradientPaint para los
// degradados circulares.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RadialGradientPaint;
// Eventos del mouse y el teclado de AWT, ComponentAdapter para reaccionar a cambios de tamaño y ComponentEvent para los
// datos de esos cambios.
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
// Transformaciones geométricas de AWT, AffineTransform para transformar el dibujo.
import java.awt.geom.AffineTransform;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna, JComponent para la
// base de los componentes, JLabel para los textos, JPanel para los paneles y Timer para los relojes.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

// Pantalla de carga del inicio, aparecen los logos y los textos de los estudios, y después una barra de carga.
// Pantalla de carga con los logos y una barra.
public class pantallaIntro extends Pantalla {

    // Textos de la intro, uno por estudio, y el mensaje que se muestra mientras carga.
    private static final String TEXTO_1 = "De los creadores de AvatarsVsRooks";
    private static final String TEXTO_2 = "Una colaboración entre Gravita y Eva Solutions";
    private static final String MENSAJE = "Estamos despertando a las mascotas";

    // Los logos de los dos estudios, los textos que se escriben letra por letra, la barra de carga y el mensaje de carga.
    private final ImagenPixel logo1 = new ImagenPixel(Sprites.logo("logo_intro_1_x4"));
    private final ImagenPixel logo2 = new ImagenPixel(Sprites.logo("logo_intro_2_x4"));
    private final TextoMaquina texto1 = new TextoMaquina(16f);
    private final TextoMaquina texto2 = new TextoMaquina(16f);
    // Barra de carga, mensaje de carga y la caja que guarda ambos.
    private final BarraCarga barra = new BarraCarga();
    private final JLabel mensaje = new JLabel(MENSAJE);
    private final JPanel cajaCarga = new JPanel();
    // Tres relojes, uno para los pasos de la intro, otro para el fundido de los logos y otro para los puntitos del mensaje.
    // Reloj de los pasos de la intro (se reconfigura en cada paso). reloj del fundido de los logos (5 escalones). reloj
    // de los puntitos del mensaje de carga.
    private final Timer relojPasos = new Timer(1, null);
    private final Timer relojFundido = new Timer(180, null);
    private final Timer relojPuntos = new Timer(300, null);
    // Cuántos puntitos lleva el mensaje y si la intro ya terminó.
    private int puntos;
    private boolean terminado;

    // Arma la pantalla, textos, caja de carga (escondida al inicio), logos al centro y botón Saltar abajo.
    // Arma la pantalla.
    public pantallaIntro() {
        // Le pone id y música a la pantalla. La intro dibuja su propio fondo oscuro. Usa BorderLayout.
        super("intro", "intro");
        setConFondo(false);
        setLayout(new BorderLayout());

        // textos con altura fija (dos renglones) para que nada salte de lugar
        // Los textos tienen un alto fijo y el mensaje de carga va en color naranja claro.
        // Le da alto fijo al primer texto. Le da alto fijo al segundo texto.
        texto1.setPreferredSize(new Dimension(900, 40));
        texto2.setPreferredSize(new Dimension(900, 40));

        // Pone la fuente del mensaje. Pone el color naranja claro.
        mensaje.setFont(Fuentes.titulo(14f));
        mensaje.setForeground(new Color(0xFFCC80));

        // La caja de carga lleva la barra y el mensaje, y está escondida hasta que termina la parte de los logos.
        // La caja de carga no tiene fondo propio. Pone la barra y el mensaje uno debajo del otro. Centra la barra. Centra el mensaje.
        cajaCarga.setOpaque(false);
        cajaCarga.setLayout(new BoxLayout(cajaCarga, BoxLayout.Y_AXIS));
        centrar(barra);
        centrar(mensaje);
        // Agrega la barra. Deja un espacio. Agrega el mensaje. La caja empieza escondida.
        cajaCarga.add(barra);
        cajaCarga.add(Box.createVerticalStrut(12));
        cajaCarga.add(mensaje);
        cajaCarga.setVisible(false);

        // Panel sin fondo propio con los logos y los textos, uno debajo del otro.
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        // Se centran todas las piezas y se agregan a la columna con un espacio entre ellas.
        // Recorre las piezas en orden. Centra la pieza. La agrega a la columna. Deja un espacio.
        for (JComponent c : new JComponent[]{logo1, texto1, logo2, texto2, cajaCarga}) {
            centrar(c);
            centro.add(c);
            centro.add(Box.createVerticalStrut(10));
        }
        // Panel sin fondo propio para centrar todo, que agrega la columna al centro de la pantalla.
        JPanel medio = new JPanel(new GridBagLayout());
        medio.setOpaque(false);
        medio.add(centro);
        add(medio, BorderLayout.CENTER);

        // Botón Saltar abajo a la derecha, pequeño, que al tocarlo suena y termina la intro, dentro de una fila.
        BotonMenu saltar = new BotonMenu("Saltar >>", BotonMenu.Estilo.SECUNDARIO);
        saltar.setPequeno(true);
        saltar.addActionListener(e -> { Sonido.efecto("click"); terminar(); });
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        // Sin fondo propio. Agrega el botón. La fila va abajo a la derecha.
        abajo.setOpaque(false);
        abajo.add(saltar);
        add(abajo, BorderLayout.SOUTH);

        // los logos se achican o agrandan según la altura de la ventana (entre 70 y 150 px)
        // Escucha cuando cambia el tamaño de la ventana.
        addComponentListener(new ComponentAdapter() {
            // Cuando cambia el tamaño.
            @Override
            public void componentResized(ComponentEvent e) {
                // Calcula el alto de los logos y cambia el de cada uno.
                int alto = Math.max(70, Math.min(150, (int) (getHeight() * 0.16)));
                logo1.setAlto(alto);
                logo2.setAlto(alto);
            }
        });
        // Alto inicial del primer logo. Alto inicial del segundo logo.
        logo1.setAlto(120);
        logo2.setAlto(120);

        // Cada 300 ms el mensaje cambia la cantidad de puntitos, de 0 a 3.
        // Suma un puntito y vuelve a cero después de tres. Escribe el mensaje con sus puntitos.
        relojPuntos.addActionListener(e -> {
            puntos = (puntos + 1) % 4;
            mensaje.setText(MENSAJE + ".".repeat(puntos));
        });
    }

    // Centra un componente dentro de la columna.
    // Centra un componente.
    private static void centrar(JComponent c) {
        // Lo alinea al centro.
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // Al entrar se reinicia todo (logos invisibles, textos vacíos y barra en cero) y se empieza con el fundido del primer logo.
    @Override
    // Al entrar se reinicia todo (logos invisibles, textos vacíos y barra en cero) y se empieza con el fundido del primer logo.
    // Se ejecuta al entrar a la pantalla.
    public void alEntrar() {
        // Hace lo normal de la clase base. La intro empieza de nuevo. Detiene cualquier reloj que siga. El primer logo empieza invisible.
        super.alEntrar();
        terminado = false;
        detener();
        logo1.setOpacidad(0f);
        // El segundo logo empieza invisible. Esconde la caja de carga. Borra el primer texto. Borra el segundo texto.
        logo2.setOpacidad(0f);
        cajaCarga.setVisible(false);
        texto1.reiniciar();
        texto2.reiniciar();
        // La barra empieza vacía. Deja el mensaje sin puntitos. Espera medio segundo y hace aparecer el primer logo.
        barra.setEncendidos(0);
        mensaje.setText(MENSAJE);
        despues(500, () -> fundir(logo1, () -> despues(1000 - 900, this::paso1)));
    }

    // Al salir se detienen todos los relojes.
    // Se ejecuta al salir de la pantalla.
    @Override
    public void alSalir() {
        // Detiene todos los relojes.
        detener();
    }

    // Los pasos de la intro, en orden, texto 1 y segundo logo, texto 2 y después la carga.
    // Primer paso de la intro.
    private void paso1() {
        // Escribe el primer texto y después aparece el segundo logo.
        texto1.escribir(TEXTO_1, () -> despues(800, () -> fundir(logo2, () -> despues(100, this::paso2))));
    }

    // Segundo paso de la intro.
    private void paso2() {
        // Escribe el segundo texto y sigue con la carga.
        texto2.escribir(TEXTO_2, () -> despues(700, this::paso3));
    }

    // Tercer paso de la intro.
    private void paso3() {
        // Muestra la barra y el mensaje. Empieza a animar los puntitos. Empieza a llenar la barra.
        cajaCarga.setVisible(true);
        relojPuntos.start();
        llenarBarra(0);
    }

    // Llena la barra un segmento a la vez (más lento al final) y al llenarse termina la intro.
    // Llena la barra un cuadrito a la vez.
    private void llenarBarra(int i) {
        // Si ya se llenó. Espera un momento y termina la intro. Termina el método.
        if (i >= BarraCarga.SEGMENTOS) {
            despues(600, this::terminar);
            return;
        }
        // Enciende un cuadrito más. Espera y sigue con el siguiente.
        barra.setEncendidos(i + 1);
        despues(i < 18 ? 100 : 180, () -> llenarBarra(i + 1));
    }

    // Hace aparecer un logo en 5 escalones (como el fundido del diseño) y después ejecuta la acción.
    // Hace aparecer un logo poco a poco.
    private void fundir(ImagenPixel logo, Runnable alTerminar) {
        // Contador de pasos del fundido, y quita las acciones anteriores del reloj una por una.
        int[] paso = {0};
        for (var l : relojFundido.getActionListeners()) {
            relojFundido.removeActionListener(l);
        }
        // Cada paso del fundido. Sube la opacidad del logo. Cuando llega al último paso.
        relojFundido.addActionListener(e -> {
            paso[0]++;
            logo.setOpacidad(paso[0] / 5f);
            if (paso[0] >= 5) {
                // Detiene el reloj. Ejecuta lo que sigue.
                relojFundido.stop();
                alTerminar.run();
            }
        });
        // Arranca el fundido.
        relojFundido.start();
    }

    // Qué hacer al terminar la intro (lo pone el controlador).
    private Runnable alTerminar = () -> { };

    // Para el controlador.
    public void setAlTerminar(Runnable accion) { alTerminar = accion; }

    // Termina la intro una sola vez, ya sea porque se llenó la barra o porque tocaron Saltar.
    // Termina la intro.
    private void terminar() {
        // Si ya había terminado. No hace nada más.
        if (terminado) {
            return;
        }
        // Marca que ya terminó. Detiene los relojes. Avisa al controlador, que lleva al inicio.
        terminado = true;
        detener();
        alTerminar.run();
    }

    // Espera unos milisegundos y ejecuta la acción; solo hay un reloj, así que cada paso reemplaza al anterior.
    // Hace algo después de unos milisegundos.
    private void despues(int ms, Runnable accion) {
        // Quita las acciones anteriores del reloj. Las elimina una por una.
        for (var l : relojPasos.getActionListeners()) {
            relojPasos.removeActionListener(l);
        }
        // Define cuánto espera. Se ejecuta una sola vez. Guarda lo que debe hacer. Arranca el reloj.
        relojPasos.setInitialDelay(ms);
        relojPasos.setRepeats(false);
        relojPasos.addActionListener(e -> accion.run());
        relojPasos.restart();
    }

    // Detiene todos los relojes y los textos.
    // Detiene todo.
    private void detener() {
        // Detiene el reloj de los pasos. Detiene el reloj del fundido. Detiene el reloj de los puntitos. Detiene el primer texto.
        relojPasos.stop();
        relojFundido.stop();
        relojPuntos.stop();
        texto1.detener();
        // Detiene el segundo texto.
        texto2.detener();
    }

    // Fondo, degradado radial oscuro (violeta al centro, casi negro en las orillas).
    // Dibuja el fondo de la intro.
    @Override
    protected void paintComponent(Graphics g) {
        // Hace lo normal de la clase base. Copia del pincel. Ancho de la pantalla. Alto de la pantalla.
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        double w = getWidth();
        double h = getHeight();
        // Transformación que estira el degradado, mueve su centro al medio de la pantalla y lo estira para que cubra todo,
        // con su radio.
        AffineTransform estirar = new AffineTransform();
        estirar.translate(w / 2, h / 2);
        estirar.scale(w / h, 1);
        float radio = (float) (Math.hypot(w / 2, h / 2) * 0.85);
        // Pone el degradado de violeta a casi negro. Colores del degradado. Aplica la transformación. Pinta toda la pantalla.
        g2.setPaint(new RadialGradientPaint(new java.awt.geom.Point2D.Double(0, 0), radio,
                new float[]{0f, 1f}, new Color[]{new Color(0x33203F), new Color(0x0B0710)}));
        g2.transform(estirar);
        g2.fillRect((int) -w, (int) -h, (int) (w * 2), (int) (h * 2));
        // Libera el pincel.
        g2.dispose();
    }
}
