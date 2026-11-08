package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.componentes.VisorEscaner;
import ecos.recursos.Sonido;

// Librería gráfica AWT, GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.GridBagLayout;

// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna, JComponent para la base de los componentes, JPanel para los paneles, SwingConstants para las alineaciones y
// Timer para los relojes.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
// Librería gráfica AWT, BorderLayout para acomodar por zonas, Component para cualquier elemento de la pantalla,
// Dimension para los tamaños, GridBagConstraints para las reglas de la cuadrícula flexible y Insets para los márgenes.
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;


// Captura del rostro para terminar el registro.
// A cargo de Valeria. Pantalla lista; falta conectarla a su controlador y al modelo.
public class PantallaRegistroFacial extends Pantalla {
     // Cuánto se corre la mascota en cada pose, al frente, izquierda, derecha. Mensaje normal del visor.
     private static final int[] POSES = {0, -22, 22};
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco.";

    // El escáner con la mascota, el texto que dice qué está pasando, el botón que inicia la captura y el reloj que pasa de
    // una pose a la siguiente.
    private final VisorEscaner visor = new VisorEscaner();
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE);
    private final BotonMenu capturar = new BotonMenu("Registrar mi rostro", true);
    private final Timer reloj = new Timer(1100, e -> avanzarCaptura());
    // En qué pose va la captura. True mientras la captura está corriendo.
    private int paso = 0;
    private boolean ocupado = false;


    public PantallaRegistroFacial() {
        // Id de la pantalla y música que usa.
        super("registro_facial", "acceso");
        setLayout(new GridBagLayout());
        // Deja espacio para la barra de arriba. El mensaje queda centrado debajo del visor. Pone el mensaje inicial.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        estado.setHorizontalAlignment(SwingConstants.CENTER);
        setTextoEstado(TEXTO_INICIAL);

        // Al apretar, empieza la captura.
        capturar.addActionListener(e -> { Sonido.efecto("click"); simularCaptura(); });

        // Visor al centro de su columna. Mensaje al centro de su columna. Columna izquierda, el visor y su mensaje.
        visor.setAlignmentX(Component.CENTER_ALIGNMENT);
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel columnaVisor = new JPanel();
        columnaVisor.setOpaque(false);
        // Apila en vertical.
        columnaVisor.setLayout(new BoxLayout(columnaVisor, BoxLayout.Y_AXIS));
        columnaVisor.add(visor);
        columnaVisor.add(Box.createVerticalStrut(10));
        columnaVisor.add(estado);

        // Columna derecha, las poses y una nota. Apila en vertical.
        JPanel columnaPoses = new JPanel();
        columnaPoses.setOpaque(false);
        columnaPoses.setLayout(new BoxLayout(columnaPoses, BoxLayout.Y_AXIS));
        columnaPoses.add(alinear(Etiqueta.titulo("LA CAPTURA TIENE 3 POSES", 10f, Etiqueta.Rol.SUAVE)));
        columnaPoses.add(Box.createVerticalStrut(12));
        columnaPoses.add(alinear(new ListaReglas(300, "Mirá al frente", "Girá un poco a la izquierda", "Girá un poco a la derecha")));
        columnaPoses.add(Box.createVerticalStrut(14));
        columnaPoses.add(alinear(Etiqueta.texto("<html><body style='width:300px'>Solo te registrás una vez. Después entrás con tu cara las veces que quieras.</body></html>", 17f, Etiqueta.Rol.SUAVE)));

        // Las dos columnas lado a lado. Reglas de posición.
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        // Centradas en vertical. 30 px entre las dos columnas. Columna del visor. Sin espacio a la derecha.
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 0, 30);
        cuerpo.add(columnaVisor, c);
        c.insets = new Insets(0, 0, 0, 0);
        // Columna de poses.
        cuerpo.add(columnaPoses, c);

        JPanel titulo = new JPanel(new BorderLayout());
        // Indicador de paso. No se estira hacia abajo.
        titulo.setOpaque(false);
        titulo.add(Etiqueta.texto("Registrá tu rostro", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST);
        titulo.add(Etiqueta.titulo("PASO 2 DE 2", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST);
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height));

        // Tarjeta de la pantalla que apila todo en vertical, con margen interno.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(alinear(titulo));
        // Visor y poses.
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(alinear(cuerpo));
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(alinear(capturar.anchoCompleto()));
        // Agrega la tarjeta a la pantalla.
        add(tarjeta);

    }
     // Simulación visual de la captura, temporal hasta que exista OpenCV.

    // Muestra las 3 poses y termina en OK.
    private void simularCaptura() {
        // Si ya está capturando.
        if (ocupado) {
            return;
        }
        // Marca que empezó. Desactiva el botón mientras corre. Empieza en la primera pose. Suena el escáner.
        ocupado = true;
        capturar.setEnabled(false);
        paso = 0;
        Sonido.efecto("escaneo");
        // El visor empieza a escanear. Primera pose, al frente. Arranca el reloj de las poses.
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        visor.setDesplazamiento(POSES[0]);
        setTextoEstado("Capturando rostro…");
        reloj.start();
    }

    // Se ejecuta cada 1,1 segundos mientras corre la captura.
    private void avanzarCaptura() {
        // Pasa al siguiente paso. Si todavía quedan poses. La mascota gira a la siguiente pose.
        paso++;
        if (paso < POSES.length) {
            Sonido.efecto("tick");
            visor.setDesplazamiento(POSES[paso]);
        // Cuando terminaron las poses. La mascota vuelve al centro. El visor se pone en OK.
        } else if (paso == POSES.length) {
            visor.setDesplazamiento(0);
            visor.setEstado(VisorEscaner.Estado.OK);
            setTextoEstado("¡Rostro registrado! Ya podés iniciar sesión.");
            // Suena el éxito. Un momento después de mostrar el OK. Detiene el reloj. Vuelve al acceso para iniciar sesión.
            Sonido.efecto("registro_ok");
        } else {
            reloj.stop();
            irA("acceso");
        }
    }


    // Se ejecuta cada vez que se entra a esta pantalla.
    @Override
    public void alEntrar() {
        // Pone la música. Por si quedó algo corriendo. Libera la captura. Vuelve a la primera pose.
        super.alEntrar();
        reloj.stop();
        ocupado = false;
        paso = 0;
        // Activa el botón. El visor vuelve a su estado normal. La mascota vuelve al centro. Mensaje inicial.
        capturar.setEnabled(true);
        visor.setEstado(VisorEscaner.Estado.NORMAL);
        visor.setDesplazamiento(0);
        setTextoEstado(TEXTO_INICIAL);
        // Empieza la animación del visor.
        visor.iniciar();
    }

    // Se ejecuta cuando se sale de esta pantalla.
    @Override
    public void alSalir() {
        // Detiene el reloj. Detiene la animación del visor. Cierra ventanas abiertas y avisa al controlador.
        reloj.stop();
        visor.detener();
        super.alSalir();
    }

    // Métodos que usa el controlador (ControladorRegistroFacial).

    // Entrega el visor para que el controlador cambie su estado. Entrega el botón por si el controlador necesita su propio evento.
    public VisorEscaner getVisor() { return visor; }
    public BotonMenu getBotonCapturar() { return capturar; }

    // Cambia el mensaje de debajo del visor (con salto de línea automático).
    public void setTextoEstado(String texto) {
        // Lo envuelve en html para que parta las líneas.
        estado.setText("<html><div style='text-align:center;width:250px'>" + texto + "</div></html>");
    }

    // Alinea un componente a la izquierda.
    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
}
