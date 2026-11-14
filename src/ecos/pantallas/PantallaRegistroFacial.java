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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

// Captura del rostro para terminar el registro. La conecta ControladorRegistroFacial, que abre la cámara y guarda el
// rostro.
public class PantallaRegistroFacial extends Pantalla {
    private static final String TEXTO_INICIAL = "Colocá tu rostro dentro del marco.";

    // Escáner con la mascota, texto de lo que pasa, botón que inicia la captura y lista de lo que hay que hacer (mirar
    // al frente, con un check al terminar).
    private final VisorEscaner visor = new VisorEscaner();
    private final ListaReglas pasos = new ListaReglas(300, "Mirá al frente");
    private final Etiqueta estado = Etiqueta.texto("", 18f, Etiqueta.Rol.SUAVE);
    private final BotonMenu capturar = new BotonMenu("Registrar mi rostro", true);
    private Runnable accionCapturar = () -> { };
    // El rostro es opcional: este botón crea la cuenta sin rostro, o cancela si la cuenta ya existía.
    private final BotonMenu omitir = new BotonMenu("Omitir por ahora", BotonMenu.Estilo.SECUNDARIO);
    private Runnable accionOmitir = () -> irA("acceso");

    public PantallaRegistroFacial() {
        super("registro_facial", "acceso");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16));
        estado.setHorizontalAlignment(SwingConstants.CENTER);
        setTextoEstado(TEXTO_INICIAL);

        capturar.addActionListener(e -> { Sonido.efecto("click"); accionCapturar.run(); });

        omitir.addActionListener(e -> { Sonido.efecto("click"); accionOmitir.run(); });

        visor.setAlignmentX(Component.CENTER_ALIGNMENT);
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel columnaVisor = new JPanel();
        columnaVisor.setOpaque(false);
        columnaVisor.setLayout(new BoxLayout(columnaVisor, BoxLayout.Y_AXIS));
        columnaVisor.add(visor);
        columnaVisor.add(Box.createVerticalStrut(10));
        columnaVisor.add(estado);

        JPanel columnaPoses = new JPanel();
        columnaPoses.setOpaque(false);
        columnaPoses.setLayout(new BoxLayout(columnaPoses, BoxLayout.Y_AXIS));
        columnaPoses.add(alinear(Etiqueta.titulo("LA CAPTURA TIENE 1 PASO", 10f, Etiqueta.Rol.SUAVE)));
        columnaPoses.add(Box.createVerticalStrut(12));
        columnaPoses.add(alinear(pasos));
        columnaPoses.add(Box.createVerticalStrut(14));
        columnaPoses.add(alinear(Etiqueta.texto("<html><body style='width:300px'>Es opcional: si lo omitís, entrás con tu contraseña y podés registrar tu rostro después.</body></html>", 17f, Etiqueta.Rol.SUAVE)));

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 0, 30);
        cuerpo.add(columnaVisor, c);
        c.insets = new Insets(0, 0, 0, 0);
        cuerpo.add(columnaPoses, c);

        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        titulo.add(Etiqueta.texto("Registrá tu rostro", 32f, Etiqueta.Rol.TEXTO), BorderLayout.WEST);
        titulo.add(Etiqueta.titulo("PASO 2 DE 2 · OPCIONAL", 10f, Etiqueta.Rol.SUAVE), BorderLayout.EAST);
        titulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, titulo.getPreferredSize().height));

        PanelPixel tarjeta = new PanelPixel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(22);
        tarjeta.add(alinear(titulo));
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(alinear(cuerpo));
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(alinear(capturar.anchoCompleto()));
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(alinear(omitir.anchoCompleto()));
        add(tarjeta);

    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        capturar.setEnabled(true);
        omitir.setEnabled(true);
        pasos.setMarcada(0, false);
        visor.setEstado(VisorEscaner.Estado.NORMAL);
        visor.setDesplazamiento(0);
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
    public BotonMenu getBotonCapturar() { return capturar; }
    public BotonMenu getBotonOmitir() { return omitir; }

    public void setAccionOmitir(Runnable accion) { accionOmitir = accion; }

    public void marcarFrente() { pasos.setMarcada(0, true); }

    public void setAccionCapturar(Runnable accion) { accionCapturar = accion; }

    public void irAAcceso() { irA("acceso"); }

    // Cambia el mensaje de debajo del visor, envuelto en html para que parta las líneas.
    public void setTextoEstado(String texto) {
        estado.setTextoHtml(texto, "text-align:center;width:250px", false);
    }

    private static JComponent alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
}
