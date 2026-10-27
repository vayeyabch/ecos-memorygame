package ecos.app.modos.JuegoC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Ventana de victoria (DECORATIVA, solo interfaz).
 * Muestra quién ganó y los intentos de cada jugador. El único botón que
 * funciona es "Cerrar"; "Ver premios" no hace nada por ahora.
 */
public class VentanaVictoria extends JDialog {

    private static final Color AMARILLO = new Color(233, 193, 72);
    private static final Color TARJETA_GANADOR = new Color(236, 212, 160);
    private static final Color TARJETA_NORMAL = new Color(237, 218, 198);
    private static final Color TEXTO_SUAVE = new Color(130, 95, 115);

    /**
     * @param ganador 1 o 2
     */
    public VentanaVictoria(JFrame padre, int ganador,
            String nombre1, int intentos1, String nombre2, int intentos2) {
        super(padre, true);
        setUndecorated(true);

        String nombreGanador = (ganador == 1) ? nombre1 : nombre2;

        // ----- Marco: guinda por fuera, línea blanca y fondo crema -----
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Paleta.GUINDA);
        raiz.setBorder(BorderFactory.createLineBorder(Paleta.GUINDA, 6));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Paleta.CREMA);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 3),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)));
        raiz.add(contenido, BorderLayout.CENTER);

        // ----- Título -----
        JLabel titulo = new JLabel("¡Ganó " + nombreGanador + "!", JLabel.CENTER);
        titulo.setFont(Utilidades.cargarFuentePixel(34f).deriveFont(Font.BOLD));
        titulo.setForeground(Paleta.GUINDA);
        contenido.add(centrar(titulo));
        contenido.add(espacio(20));

        // ----- Tarjetas de los dos jugadores -----
        JPanel tarjetas = new JPanel(new GridLayout(1, 2, 18, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(crearTarjeta(nombre1, intentos1, "src/recursos/Iconos/avatar1.png", ganador == 1));
        tarjetas.add(crearTarjeta(nombre2, intentos2, "src/recursos/Iconos/avatar2.png", ganador == 2));
        contenido.add(centrar(tarjetas));
        contenido.add(espacio(20));

        // ----- Premio (solo decorativo) -----
        JLabel premio = new JLabel("Premio: ₡ 2 320 ($4.76)", JLabel.CENTER);
        premio.setFont(Utilidades.cargarFuentePixel(26f).deriveFont(Font.BOLD));
        premio.setForeground(Paleta.GUINDA);
        premio.setOpaque(true);
        premio.setBackground(Paleta.DORADO);
        premio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.GUINDA, 5),
                BorderFactory.createEmptyBorder(14, 30, 14, 30)));
        contenido.add(centrar(premio));
        contenido.add(espacio(20));

        // ----- Botones -----
        BotonPixel btnPremios = new BotonPixel("Ver premios", Paleta.DORADO,
            Paleta.TEXTO_NARANJA, 22f);
        btnPremios.setText("Ver premios");
        btnPremios.setBackground(Paleta.DORADO);
        btnPremios.setForeground(Paleta.TEXTO_NARANJA);
        btnPremios.setFont(Utilidades.cargarFuentePixel(22f));
        // btnPremios: sin acción por ahora

        BotonPixel btnCerrar = new BotonPixel("Cerrar", Color.WHITE,
            Paleta.GUINDA, 22f);
        btnCerrar.setText("Cerrar");
        btnCerrar.setBackground(Color.WHITE);
        btnCerrar.setForeground(Paleta.GUINDA);
        btnCerrar.setFont(Utilidades.cargarFuentePixel(22f));
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnPremios);
        panelBotones.add(btnCerrar);
        contenido.add(centrar(panelBotones));

        setContentPane(raiz);
        pack();
        setLocationRelativeTo(padre);
    }

    private JPanel crearTarjeta(String nombre, int intentos, String rutaAvatar, boolean gana) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(gana ? TARJETA_GANADOR : TARJETA_NORMAL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(gana ? AMARILLO : Paleta.GUINDA, 4),
                BorderFactory.createEmptyBorder(18, 36, 18, 36)));

        // Avatar (si el archivo no existe, queda un espacio vacío)
        ImageIcon avatar = Utilidades.cargarIconoRedimensionado(rutaAvatar, 64, 64);
        JLabel lblAvatar = new JLabel(avatar, JLabel.CENTER);
        lblAvatar.setPreferredSize(new java.awt.Dimension(64, 64));

        JLabel lblNombre = new JLabel(nombre, JLabel.CENTER);
        lblNombre.setFont(Utilidades.cargarFuentePixel(24f));
        lblNombre.setForeground(Paleta.GUINDA);

        JLabel lblNumero = new JLabel(String.valueOf(intentos), JLabel.CENTER);
        lblNumero.setFont(Utilidades.cargarFuentePixel(48f).deriveFont(Font.BOLD));
        lblNumero.setForeground(Paleta.GUINDA);

        JLabel lblTexto = new JLabel("intentos", JLabel.CENTER);
        lblTexto.setFont(Utilidades.cargarFuentePixel(22f));
        lblTexto.setForeground(TEXTO_SUAVE);

        tarjeta.add(centrar(lblAvatar));
        tarjeta.add(espacio(6));
        tarjeta.add(centrar(lblNombre));
        tarjeta.add(centrar(lblNumero));
        tarjeta.add(centrar(lblTexto));
        return tarjeta;
    }

    private static <T extends JComponent> T centrar(T componente) {
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
        return componente;
    }

    private static Component espacio(int alto) {
        return javax.swing.Box.createVerticalStrut(alto);
    }
}