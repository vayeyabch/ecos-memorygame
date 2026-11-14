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
import java.awt.Component;

// Escaneo del rostro para iniciar sesión con Face ID. La conecta ControladorLoginFacial, que abre la cámara y reconoce
// el rostro.
public class PantallaLoginFacial extends Pantalla {
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco.";

    // Escáner con la mascota, texto de lo que pasa y botón que inicia el escaneo.
    private final VisorEscaner visor = new VisorEscaner();
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE);
    private final BotonMenu escanear = new BotonMenu("Escanear mi rostro", true);
    private Runnable accionEscanear = () -> { };

    public PantallaLoginFacial() {
        super("login_facial", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        estado.setHorizontalAlignment(SwingConstants.CENTER);
        setTextoEstado(TEXTO_INICIAL);

        escanear.addActionListener(e -> { Sonido.efecto("click"); accionEscanear.run(); });
        BotonMenu conClave = new BotonMenu("Usar usuario y contraseña", BotonMenu.Estilo.SECUNDARIO);
        conClave.addActionListener(e -> { Sonido.efecto("click"); volver(); });

        visor.setAlignmentX(Component.CENTER_ALIGNMENT);
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        Etiqueta titulo = Etiqueta.texto("Entrar con Face ID", 30f, Etiqueta.Rol.TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(visor);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(estado);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(centrar(escanear.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(centrar(conClave.anchoCompleto()));
        add(tarjeta);
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        escanear.setEnabled(true);
        visor.setEstado(VisorEscaner.Estado.NORMAL);
        setTextoEstado(TEXTO_INICIAL);
        visor.iniciar();
    }

    @Override
    public void alSalir() {
        // Detiene la animación del visor, cierra ventanas abiertas y avisa al controlador.
        visor.detener();
        super.alSalir();
    }

    public VisorEscaner getVisor() { return visor; }
    public BotonMenu getBotonEscanear() { return escanear; }

    public void setAccionEscanear(Runnable accion) { accionEscanear = accion; }

    public void irAMenu() { irA("menu"); }

    // Cambia el mensaje de debajo del visor, envuelto en html para que parta las líneas.
    public void setTextoEstado(String texto) {
        estado.setTextoHtml(texto, "text-align:center;width:250px", false);
    }

    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }
}

