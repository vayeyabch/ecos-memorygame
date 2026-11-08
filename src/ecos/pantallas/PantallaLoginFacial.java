package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.PanelPixel;
import ecos.componentes.VisorEscaner;
import ecos.recursos.Sonido;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Component;

// Escaneo del rostro para iniciar sesión con Face ID.
// Responsable: Valeria. Pantalla lista; falta conectarla a su controlador y al modelo.
public class PantallaLoginFacial extends Pantalla {
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco."; // Mensaje normal del visor.

    private final VisorEscaner visor = new VisorEscaner(); // El escáner con la mascota.
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE); // Texto que dice qué está pasando.
    private final BotonMenu escanear = new BotonMenu("Escanear mi rostro", true); // Botón que inicia el escaneo.
    private final Timer reloj = new Timer(1900, e -> avanzarEscaneo()); // Reloj que pasa de escanear a OK y de OK al menú.
    private int paso = 0; // En qué parte del escaneo va.
    private boolean ocupado = false; // true mientras el escaneo está corriendo.

    public PantallaLoginFacial() {
        super("login_facial", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16)); // Deja espacio para la barra de arriba.
        estado.setHorizontalAlignment(SwingConstants.CENTER); // El mensaje queda centrado en la tarjeta.
        setTextoEstado(TEXTO_INICIAL); // Pone el mensaje inicial.

        escanear.addActionListener(e -> { Sonido.efecto("click"); simularEscaneo(); }); // Al apretar, empieza el escaneo.
        BotonMenu conClave = new BotonMenu("Usar usuario y contraseña", BotonMenu.Estilo.SECUNDARIO); // Botón para volver al login normal.
        conClave.addActionListener(e -> { Sonido.efecto("click"); volver(); }); // Regresa a la pantalla anterior (el acceso).

        visor.setAlignmentX(Component.CENTER_ALIGNMENT); // Visor al centro de la tarjeta.
        estado.setAlignmentX(Component.CENTER_ALIGNMENT); // Mensaje al centro de la tarjeta.
        Etiqueta titulo = Etiqueta.texto("Entrar con Face ID", 30f, Etiqueta.Rol.TEXTO); // Título.
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Título al centro.

        PanelPixel tarjeta = new PanelPixel(); // Tarjeta de la pantalla.
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS)); // Apila todo en vertical.
        tarjeta.setPadding(22); // Margen interno (igual que en PantallaAcceso).
        tarjeta.add(titulo); // Título.
        tarjeta.add(Box.createVerticalStrut(16)); // Espacio.
        tarjeta.add(visor); // Visor del escáner.
        tarjeta.add(Box.createVerticalStrut(10)); // Espacio.
        tarjeta.add(estado); // Mensaje.
        tarjeta.add(Box.createVerticalStrut(14)); // Espacio.
        tarjeta.add(centrar(escanear.anchoCompleto())); // Botón de escanear.
        tarjeta.add(Box.createVerticalStrut(10)); // Espacio.
        tarjeta.add(centrar(conClave.anchoCompleto())); // Botón para usar contraseña.
        add(tarjeta); // Agrega la tarjeta a la pantalla.
    }
     // ---- Simulación visual del escaneo (TEMPORAL: se reemplaza cuando exista OpenCV) ----

    private void simularEscaneo() { // Escanea un momento, muestra OK y entra al menú.
        if (ocupado) { // Si ya está escaneando.
            return; // No hace nada.
        }
        ocupado = true; // Marca que empezó.
        escanear.setEnabled(false); // Desactiva el botón mientras corre.
        paso = 0; // Empieza desde el principio.
        Sonido.efecto("escaneo"); // Suena el escáner.
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO); // El visor empieza a escanear.
        setTextoEstado("Escaneando…"); // Avisa.
        reloj.setDelay(1900); // El escaneo dura 1,9 segundos.
        reloj.start(); // Arranca el reloj.
    }

    private void avanzarEscaneo() { // Se ejecuta cuando termina cada parte del escaneo.
        paso++; // Pasa al siguiente paso.
        if (paso == 1) { // Terminó de escanear.
            visor.setEstado(VisorEscaner.Estado.OK); // El visor se pone en OK.
            setTextoEstado("¡Rostro reconocido!"); // Avisa.
            Sonido.efecto("registro_ok"); // Suena el éxito.
            reloj.setDelay(1000); // Espera 1 segundo antes de entrar.
        } else { // Un momento después de mostrar el OK.
            reloj.stop(); // Detiene el reloj.
            irA("menu"); // Entra al menú.
        }
    }

    // ---- Ciclo de la pantalla ----

    @Override // Indica que se reemplaza un método de Pantalla.
    public void alEntrar() { // Se ejecuta cada vez que se entra a esta pantalla.
        super.alEntrar(); // Pone la música.
        reloj.stop(); // Por si quedó algo corriendo.
        ocupado = false; // Libera el escaneo.
        paso = 0; // Vuelve al principio.
        escanear.setEnabled(true); // Activa el botón.
        visor.setEstado(VisorEscaner.Estado.NORMAL); // El visor vuelve a su estado normal.
        setTextoEstado(TEXTO_INICIAL); // Mensaje inicial.
        visor.iniciar(); // Empieza la animación del visor.
    }

    @Override // Indica que se reemplaza un método de Pantalla.
    public void alSalir() { // Se ejecuta cuando se sale de esta pantalla.
        reloj.stop(); // Detiene el reloj.
        visor.detener(); // Detiene la animación del visor.
        super.alSalir(); // Cierra ventanas abiertas y avisa al controlador.
    }

    // ---- Para el controlador (ControladorLoginFacial) ----

    public VisorEscaner getVisor() { return visor; } // Entrega el visor para que el controlador cambie su estado.
    public BotonMenu getBotonEscanear() { return escanear; } // Entrega el botón por si el controlador necesita su propio evento.

    public void setTextoEstado(String texto) { // Cambia el mensaje de debajo del visor (con salto de línea automático).
        estado.setText("<html><div style='text-align:center;width:250px'>" + texto + "</div></html>"); // Lo envuelve en html para que parta las líneas.
    }

    private static JComponent centrar(JComponent c) { // Alinea un componente al centro de la tarjeta (todos los de esta tarjeta van centrados).
        c.setAlignmentX(Component.CENTER_ALIGNMENT); // Lo alinea.
        return c; // Lo entrega.
    }
}
