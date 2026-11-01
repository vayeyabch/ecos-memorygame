package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.Etiqueta;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.componentes.VisorEscaner;
import ecos.recursos.Sonido;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;


// Captura del rostro para terminar el registro.
// Responsable: Valeria. Esqueleto: solo muestra el título, falta armar la pantalla.
public class PantallaRegistroFacial extends Pantalla {
     private static final int[] POSES = {0, -22, 22}; // Cuánto se corre la mascota en cada pose: al frente, izquierda, derecha.
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco."; // Mensaje normal del visor.

    private final VisorEscaner visor = new VisorEscaner(); // El escáner con la mascota.
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE); // Texto que dice qué está pasando.
    private final BotonMenu capturar = new BotonMenu("Registrar mi rostro", true); // Botón que inicia la captura.
    private final Timer reloj = new Timer(1100, e -> avanzarCaptura()); // Reloj que pasa de una pose a la siguiente.
    private int paso = 0; // En qué pose va la captura.
    private boolean ocupado = false; // true mientras la captura está corriendo.


    public PantallaRegistroFacial() {
        super("registro_facial", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16)); // Deja espacio para la barra de arriba.
        estado.setHorizontalAlignment(SwingConstants.CENTER); // El mensaje queda centrado debajo del visor.
        setTextoEstado(TEXTO_INICIAL); // Pone el mensaje inicial.

        capturar.addActionListener(e -> { Sonido.efecto("click"); simularCaptura(); }); // Al apretar, empieza la captura.

        visor.setAlignmentX(Component.CENTER_ALIGNMENT); // Visor al centro de su columna.
        estado.setAlignmentX(Component.CENTER_ALIGNMENT); // Mensaje al centro de su columna.
        JPanel columnaVisor = new JPanel(); // Columna izquierda: el visor y su mensaje.
        columnaVisor.setOpaque(false); // Transparente.
        columnaVisor.setLayout(new BoxLayout(columnaVisor, BoxLayout.Y_AXIS)); // Apila en vertical.
        columnaVisor.add(visor); // Visor.
        columnaVisor.add(Box.createVerticalStrut(10)); // Espacio.
        columnaVisor.add(estado); // Mensaje.

        JPanel columnaPoses = new JPanel(); // Columna derecha: las poses y una nota.
        columnaPoses.setOpaque(false); // Transparente.
        columnaPoses.setLayout(new BoxLayout(columnaPoses, BoxLayout.Y_AXIS)); // Apila en vertical.
        columnaPoses.add(alinear(Etiqueta.titulo("LA CAPTURA TIENE 3 POSES", 10f, Etiqueta.Rol.SUAVE))); // Título chico.
        columnaPoses.add(Box.createVerticalStrut(12)); // Espacio.
        columnaPoses.add(alinear(new ListaReglas(300, "Mirá al frente", "Girá un poco a la izquierda", "Girá un poco a la derecha"))); // Las 3 poses.
        columnaPoses.add(Box.createVerticalStrut(14)); // Espacio.
        columnaPoses.add(alinear(Etiqueta.texto("<html><body style='width:300px'>Solo te registrás una vez. Después entrás con tu cara las veces que quieras.</body></html>", 17f, Etiqueta.Rol.SUAVE))); // Nota.

        JPanel cuerpo = new JPanel(new GridBagLayout()); // Las dos columnas lado a lado.
        cuerpo.setOpaque(false); // Transparente.
        GridBagConstraints c = new GridBagConstraints(); // Reglas de posición.
        c.gridy = 0; // Misma fila.
        c.anchor = GridBagConstraints.CENTER; // Centradas en vertical.
        c.insets = new Insets(0, 0, 0, 30); // 30 px entre las dos columnas.
        cuerpo.add(columnaVisor, c); // Columna del visor.
        c.insets = new Insets(0, 0, 0, 0); // Sin espacio a la derecha.
        cuerpo.add(columnaPoses, c); // Columna de poses.

        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false); // Transparente.
        titulo.add(Etiqueta.texto("Registrá tu rostro", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST); // Título.
        titulo.add(Etiqueta.titulo("PASO 2 DE 2", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST); // Indicador de paso.
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height)); // No se estira hacia abajo.

        PanelPixel tarjeta = new PanelPixel(); // Tarjeta de la pantalla.
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS)); // Apila todo en vertical.
        tarjeta.setPadding(22); // Margen interno.
        tarjeta.add(alinear(titulo)); // Título.
        tarjeta.add(Box.createVerticalStrut(14)); // Espacio.
        tarjeta.add(alinear(cuerpo)); // Visor y poses.
        tarjeta.add(Box.createVerticalStrut(18)); // Espacio.
        tarjeta.add(alinear(capturar.anchoCompleto())); // Botón de capturar.
        add(tarjeta); // Agrega la tarjeta a la pantalla.

    }
     // ---- Simulación visual de la captura (TEMPORAL: se reemplaza cuando exista OpenCV) ----

    private void simularCaptura() { // Muestra las 3 poses y termina en OK.
        if (ocupado) { // Si ya está capturando.
            return; // No hace nada.
        }
        ocupado = true; // Marca que empezó.
        capturar.setEnabled(false); // Desactiva el botón mientras corre.
        paso = 0; // Empieza en la primera pose.
        Sonido.efecto("escaneo"); // Suena el escáner.
        visor.setEstado(VisorEscaner.Estado.ESCANEANDO); // El visor empieza a escanear.
        visor.setDesplazamiento(POSES[0]); // Primera pose: al frente.
        setTextoEstado("Capturando rostro…"); // Avisa.
        reloj.start(); // Arranca el reloj de las poses.
    }

    private void avanzarCaptura() { // Se ejecuta cada 1,1 segundos mientras corre la captura.
        paso++; // Pasa al siguiente paso.
        if (paso < POSES.length) { // Si todavía quedan poses.
            Sonido.efecto("tick"); // Suena un tic.
            visor.setDesplazamiento(POSES[paso]); // La mascota gira a la siguiente pose.
        } else if (paso == POSES.length) { // Cuando terminaron las poses.
            visor.setDesplazamiento(0); // La mascota vuelve al centro.
            visor.setEstado(VisorEscaner.Estado.OK); // El visor se pone en OK.
            setTextoEstado("¡Rostro registrado! Ya podés iniciar sesión."); // Avisa.
            Sonido.efecto("registro_ok"); // Suena el éxito.
        } else { // Un momento después de mostrar el OK.
            reloj.stop(); // Detiene el reloj.
            irA("acceso"); // Vuelve al acceso para iniciar sesión.
        }
    }


    @Override // Indica que se reemplaza un método de Pantalla.
    public void alEntrar() { // Se ejecuta cada vez que se entra a esta pantalla.
        super.alEntrar(); // Pone la música.
        reloj.stop(); // Por si quedó algo corriendo.
        ocupado = false; // Libera la captura.
        paso = 0; // Vuelve a la primera pose.
        capturar.setEnabled(true); // Activa el botón.
        visor.setEstado(VisorEscaner.Estado.NORMAL); // El visor vuelve a su estado normal.
        visor.setDesplazamiento(0); // La mascota vuelve al centro.
        setTextoEstado(TEXTO_INICIAL); // Mensaje inicial.
        visor.iniciar(); // Empieza la animación del visor.
    }

    @Override
    public void alSalir() { // Se ejecuta cuando se sale de esta pantalla.
        reloj.stop(); // Detiene el reloj.
        visor.detener(); // Detiene la animación del visor.
        super.alSalir(); // Cierra ventanas abiertas y avisa al controlador.
    }

    // ---- Para el controlador (ControladorRegistroFacial) ----

    public VisorEscaner getVisor() { return visor; } // Entrega el visor para que el controlador cambie su estado.
    public BotonMenu getBotonCapturar() { return capturar; } // Entrega el botón por si el controlador necesita su propio evento.

    public void setTextoEstado(String texto) { // Cambia el mensaje de debajo del visor (con salto de línea automático).
        estado.setText("<html><div style='text-align:center;width:250px'>" + texto + "</div></html>"); // Lo envuelve en html para que parta las líneas.
    }

    private static JComponent alinear(JComponent c) { // Alinea un componente a la izquierda.
        c.setAlignmentX(Component.LEFT_ALIGNMENT); // Lo alinea.
        return c; // Lo entrega.
    }
}
