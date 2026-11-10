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
    // Mensaje normal del visor.
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco.";

    // El escáner con la mascota, el texto que dice qué está pasando, el botón que inicia la captura, el reloj de la simulación
    // y la lista de lo que hay que hacer (una sola casilla, mirar al frente, que se marca con un check al terminar).
    private final VisorEscaner visor = new VisorEscaner();
    private final ListaReglas pasos = new ListaReglas(300, "Mirá al frente");
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE);
    private final BotonMenu capturar = new BotonMenu("Registrar mi rostro", true);
    private final Timer reloj = new Timer(1100, e -> avanzarCaptura());
    // En qué paso va la simulación. True mientras la captura está corriendo.
    private int paso = 0;
    private boolean ocupado = false;
    // Lo que hace el botón cuando el controlador lo define (cámara real). Si no, se usa la simulación de abajo.
    private Runnable accionCapturar = null;
    // El rostro es opcional, este botón crea la cuenta sin rostro (o cancela si la cuenta ya existía).
    private final BotonMenu omitir = new BotonMenu("Omitir por ahora", BotonMenu.Estilo.SECUNDARIO);
    private Runnable accionOmitir = () -> irA("acceso");


    public PantallaRegistroFacial() {
        // Id de la pantalla y música que usa.
        super("registro_facial", "acceso");
        setLayout(new GridBagLayout());
        // Deja espacio para la barra de arriba. El mensaje queda centrado debajo del visor. Pone el mensaje inicial.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        estado.setHorizontalAlignment(SwingConstants.CENTER);
        setTextoEstado(TEXTO_INICIAL);

        // Al apretar, empieza la captura.
        capturar.addActionListener(e -> {
            Sonido.efecto("click");
            if (accionCapturar != null) {
                accionCapturar.run();
            } else {
                simularCaptura();
            }
        });

        omitir.addActionListener(e -> { Sonido.efecto("click"); accionOmitir.run(); });

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

        // Columna derecha, lo que hay que hacer y una nota. Apila en vertical.
        JPanel columnaPoses = new JPanel();
        columnaPoses.setOpaque(false);
        columnaPoses.setLayout(new BoxLayout(columnaPoses, BoxLayout.Y_AXIS));
        columnaPoses.add(alinear(Etiqueta.titulo("LA CAPTURA TIENE 1 PASO", 10f, Etiqueta.Rol.SUAVE)));
        columnaPoses.add(Box.createVerticalStrut(12));
        columnaPoses.add(alinear(pasos));
        columnaPoses.add(Box.createVerticalStrut(14));
        columnaPoses.add(alinear(Etiqueta.texto("<html><body style='width:300px'>Es opcional: si lo omitís, entrás con tu contraseña y podés registrar tu rostro después.</body></html>", 17f, Etiqueta.Rol.SUAVE)));

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
        titulo.add(Etiqueta.titulo("PASO 2 DE 2 · OPCIONAL", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST);
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
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(omitir.anchoCompleto()));
        // Agrega la tarjeta a la pantalla.
        add(tarjeta);

    }
     // Simulación visual de la captura, temporal hasta que exista OpenCV.

    // Simula mirar al frente y termina en OK.
    private void simularCaptura() {
        // Si ya está capturando.
        if (ocupado) {
            return;
        }
        // Marca que empezó. Desactiva el botón mientras corre. Suena el escáner.
        ocupado = true;
        capturar.setEnabled(false);
        paso = 0;
        Sonido.efecto("escaneo");
        // El visor empieza a escanear. Arranca el reloj.
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        visor.setDesplazamiento(0);
        setTextoEstado("Capturando rostro…");
        reloj.start();
    }

    // Se ejecuta cada 1,1 segundos mientras corre la captura.
    private void avanzarCaptura() {
        // Pasa al siguiente paso.
        paso++;
        // Primero se muestra el OK, con el check en "Mirá al frente" y el sonido de éxito.
        if (paso == 1) {
            visor.setDesplazamiento(0);
            visor.setEstado(VisorEscaner.Estado.OK);
            marcarFrente();
            setTextoEstado("¡Rostro registrado! Ya podés iniciar sesión.");
            Sonido.efecto(Sonido.EXITO);
        // Un momento después detiene el reloj y vuelve al acceso para iniciar sesión.
        } else {
            reloj.stop();
            irA("acceso");
        }
    }


    // Se ejecuta cada vez que se entra a esta pantalla.
    @Override
    public void alEntrar() {
        // Pone la música. Por si quedó algo corriendo. Libera la captura. Vuelve al primer paso.
        super.alEntrar();
        reloj.stop();
        ocupado = false;
        paso = 0;
        // Activa el botón. El visor vuelve a su estado normal. La mascota vuelve al centro. Mensaje inicial.
        capturar.setEnabled(true);
        omitir.setEnabled(true);
        pasos.setMarcada(0, false);
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
    public BotonMenu getBotonOmitir() { return omitir; }

    // El controlador define qué hace el botón "Omitir por ahora".
    public void setAccionOmitir(Runnable accion) { accionOmitir = accion; }

    // El controlador la llama cuando el rostro quedó registrado, marca con un check la casilla "Mirá al frente".
    public void marcarFrente() { pasos.setMarcada(0, true); }

    // El controlador define qué hace el botón "Registrar mi rostro".
    public void setAccionCapturar(Runnable accion) { accionCapturar = accion; }

    // El controlador la llama cuando el rostro quedó registrado, vuelve al acceso para iniciar sesión.
    public void irAAcceso() { irA("acceso"); }

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