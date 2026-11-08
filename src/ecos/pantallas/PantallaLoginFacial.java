package ecos.pantallas;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.componentes.VisorEscaner;
import ecos.recursos.Sonido;

// Librería gráfica AWT, GridBagLayout para acomodar en una cuadrícula flexible.
import java.awt.GridBagLayout;

// Librería Swing, BorderFactory para los bordes, Box para los espacios entre componentes, BoxLayout para apilar en fila
// o columna, JComponent para la base de los componentes, SwingConstants para las alineaciones y Timer para los relojes.
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.Timer;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla.
import java.awt.Component;

// Escaneo del rostro para iniciar sesión con Face ID.
// A cargo de Valeria. Pantalla lista; falta conectarla a su controlador y al modelo.
public class PantallaLoginFacial extends Pantalla {
    // Mensaje normal del visor.
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco.";

    // El escáner con la mascota, el texto que dice qué está pasando, el botón que inicia el escaneo y el reloj que pasa de
    // escanear a OK y de OK al menú.
    private final VisorEscaner visor = new VisorEscaner();
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE);
    private final BotonMenu escanear = new BotonMenu("Escanear mi rostro", true);
    private final Timer reloj = new Timer(1900, e -> avanzarEscaneo());
    // En qué parte del escaneo va. True mientras el escaneo está corriendo.
    private int paso = 0;
    private boolean ocupado = false;

    public PantallaLoginFacial() {
        // Id de la pantalla y música que usa.
        super("login_facial", "acceso");
        setLayout(new GridBagLayout());
        // Deja espacio para la barra de arriba. El mensaje queda centrado en la tarjeta. Pone el mensaje inicial.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        estado.setHorizontalAlignment(SwingConstants.CENTER);
        setTextoEstado(TEXTO_INICIAL);

        // Al apretar, empieza el escaneo. Botón para volver al login normal. Regresa a la pantalla anterior (el acceso).
        escanear.addActionListener(e -> { Sonido.efecto("click"); simularEscaneo(); });
        BotonMenu conClave = new BotonMenu("Usar usuario y contraseña", BotonMenu.Estilo.SECUNDARIO);
        conClave.addActionListener(e -> { Sonido.efecto("click"); volver(); });

        // Visor al centro de la tarjeta. Mensaje al centro de la tarjeta. Título al centro.
        visor.setAlignmentX(Component.CENTER_ALIGNMENT);
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        Etiqueta titulo = Etiqueta.texto("Entrar con Face ID", 30f, Etiqueta.Rol.TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tarjeta de la pantalla que apila todo en vertical, con el mismo margen interno que PantallaAcceso.
        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(titulo);
        // Visor del escáner.
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(visor);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(estado);
        // Botón para usar contraseña.
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(centrar(escanear.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(centrar(conClave.anchoCompleto()));
        // Agrega la tarjeta a la pantalla.
        add(tarjeta);
    }
     // Simulación visual del escaneo, temporal hasta que exista OpenCV.

    // Escanea un momento, muestra OK y entra al menú.
    private void simularEscaneo() {
        // Si ya está escaneando.
        if (ocupado) {
            return;
        }
        // Marca que empezó. Desactiva el botón mientras corre. Empieza desde el principio. Suena el escáner.
        ocupado = true;
        escanear.setEnabled(false);
        paso = 0;
        Sonido.efecto("escaneo");
        // El visor empieza a escanear. El escaneo dura 1,9 segundos. Arranca el reloj.
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO);
        setTextoEstado("Escaneando…");
        reloj.setDelay(1900);
        reloj.start();
    }

    // Se ejecuta cuando termina cada parte del escaneo.
    private void avanzarEscaneo() {
        // Pasa al siguiente paso. Terminó de escanear. El visor se pone en OK.
        paso++;
        if (paso == 1) {
            visor.setEstado(VisorEscaner.Estado.OK);
            setTextoEstado("¡Rostro reconocido!");
            // Suena el éxito. Espera 1 segundo antes de entrar. Un momento después de mostrar el OK. Detiene el reloj.
            Sonido.efecto("registro_ok");
            reloj.setDelay(1000);
        } else {
            reloj.stop();
            // Entra al menú.
            irA("menu");
        }
    }

    // Ciclo de la pantalla.

    // Se ejecuta cada vez que se entra a esta pantalla.
    @Override
    public void alEntrar() {
        // Pone la música. Por si quedó algo corriendo. Libera el escaneo. Vuelve al principio.
        super.alEntrar();
        reloj.stop();
        ocupado = false;
        paso = 0;
        // Activa el botón. El visor vuelve a su estado normal. Mensaje inicial. Empieza la animación del visor.
        escanear.setEnabled(true);
        visor.setEstado(VisorEscaner.Estado.NORMAL);
        setTextoEstado(TEXTO_INICIAL);
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

    // Métodos que usa el controlador (ControladorLoginFacial).

    // Entrega el visor para que el controlador cambie su estado. Entrega el botón por si el controlador necesita su propio evento.
    public VisorEscaner getVisor() { return visor; }
    public BotonMenu getBotonEscanear() { return escanear; }

    // Cambia el mensaje de debajo del visor (con salto de línea automático).
    public void setTextoEstado(String texto) {
        // Lo envuelve en html para que parta las líneas.
        estado.setText("<html><div style='text-align:center;width:250px'>" + texto + "</div></html>");
    }

    // Alinea un componente al centro de la tarjeta (todos los de esta tarjeta van centrados).
    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}
