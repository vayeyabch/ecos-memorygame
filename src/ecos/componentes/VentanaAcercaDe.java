package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Recursos;
import ecos.recursos.Sprites;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Ventana Acerca de: muestra a las cuatro integrantes del equipo con su foto, apellidos y correo.
public class VentanaAcercaDe {

    // Textos de la ventana, juntos arriba para traducirlos al inglés fácil; los nombres no se traducen.
    private static final String TITULO = "ACERCA DE ECOS";
    private static final String SUBTITULO = "Proyecto Memory Game";
    private static final String DESCRIPCION = "Proyecto final del curso Desarrollo de Software II, realizado por estudiantes de Informática Empresarial.";
    private static final String CERRAR = "Cerrar";

    // Integrantes con primer nombre, apellidos, correo (lo pidió el profesor) y foto sin extensión dentro de
    // src/recursos/equipo.
    private static final String[] NOMBRES = {"Valeria", "Hellen", "Isabella", "Allison"};
    private static final String[] APELLIDOS = {"Bermúdez Chacón", "Herrera Cascante", "Guzmán Leiva", "Ortega Aguilar"};
    private static final String[] CORREOS = {"vbermudezch@gmail.com", "hellenicoleherrerac@gmail.com", "isabellagl2008@hotmail.com", "aliortegaaguilar@gmail.com"};
    private static final String[] FOTOS = {"fvaleria", "fhellen", "fisabella", "fallison"};
    // Formatos de foto que acepta y tamaño del marco.
    private static final String[] EXTENSIONES = {".png", ".jpg", ".jpeg"};
    private static final int LADO = 100;

    // Abre la ventana encima de la pantalla actual; origen sirve para saber en qué ventana se dibuja.
    public static void mostrar(Component origen) {
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(18);
        panel.add(centrar(Etiqueta.titulo(TITULO, 20f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(6));
        panel.add(centrar(Etiqueta.texto(SUBTITULO, 26f, Etiqueta.Rol.ACENTO)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(Etiqueta.texto("<html><div style='width:620px;text-align:center'>" + DESCRIPCION + "</div></html>", 22f, Etiqueta.Rol.SUAVE)));
        // Las cuatro integrantes en 2 x 2.
        panel.add(Box.createVerticalStrut(14));
        JPanel cuadricula = new JPanel(new GridLayout(2, 2, 40, 14));
        cuadricula.setOpaque(false);
        for (int i = 0; i < NOMBRES.length; i++) {
            cuadricula.add(celda(i));
        }
        panel.add(centrar(cuadricula));
        CapaModal capa = new CapaModal(panel);
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA);
        cerrar.addActionListener(e -> capa.cerrar());
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(cerrar));
        capa.mostrar(origen);
    }

    // Una celda: el marco de la foto arriba y el nombre abajo.
    private static JComponent celda(int i) {
        JPanel celda = new JPanel();
        celda.setOpaque(false);
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS));
        celda.add(centrar(new Foto(rutaFoto(FOTOS[i]))));
        celda.add(Box.createVerticalStrut(6));
        celda.add(centrar(Etiqueta.titulo(NOMBRES[i], 15f, Etiqueta.Rol.TEXTO)));
        celda.add(centrar(Etiqueta.texto(APELLIDOS[i], 22f, Etiqueta.Rol.SUAVE)));
        celda.add(centrar(Etiqueta.texto(CORREOS[i], 19f, Etiqueta.Rol.ACENTO)));
        return celda;
    }

    // Busca la foto con cualquiera de las extensiones permitidas; devuelve null si no existe.
    private static String rutaFoto(String nombre) {
        for (String ext : EXTENSIONES) {
            if (Recursos.existe("equipo/" + nombre + ext)) {
                return "equipo/" + nombre + ext;
            }
        }
        return null;
    }

    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }

    // Marco cuadrado para la foto; queda vacío si todavía no hay foto.
    private static class Foto extends JComponent {
        private final String ruta;

        Foto(String ruta) {
            this.ruta = ruta;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(LADO, LADO);
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Tema.borde());
            g2.fillRect(0, 0, LADO, LADO);
            g2.setColor(Tema.caja());
            g2.fillRect(4, 4, LADO - 8, LADO - 8);
            if (ruta != null) {
                g2.drawImage(Sprites.cargarEscalada(ruta, LADO - 8, LADO - 8), 4, 4, null);
            }
            g2.dispose();
        }
    }
}
