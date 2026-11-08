package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Recursos;
import ecos.recursos.Sprites;
// Librería gráfica AWT, Component para cualquier elemento de la pantalla, Dimension para los tamaños, Graphics para
// dibujar, Graphics2D para dibujar con más opciones y GridLayout para acomodar en una cuadrícula fija.
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
// Librería Swing, Box para los espacios entre componentes, BoxLayout para apilar en fila o columna, JComponent para la
// base de los componentes y JPanel para los paneles.
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Ventana "Acerca de", muestra a las cuatro integrantes del equipo con su foto, apellidos y correo.
public class VentanaAcercaDe {

    // Textos de la ventana, todos aquí arriba para que sea fácil traducirlos al inglés después, porque los nombres no se
    // traducen. En inglés serían "ABOUT ECOS", "Memory Game Project" y "Final project of the Software Development II course,
    // made by Business Informatics students."
    private static final String TITULO = "ACERCA DE ECOS";
    private static final String SUBTITULO = "Proyecto Memory Game";
    private static final String DESCRIPCION = "Proyecto final del curso Desarrollo de Software II, realizado por estudiantes de Informática Empresarial.";
    private static final String CERRAR = "Cerrar";

    // Integrantes con su primer nombre, apellidos, correo (lo pidió el profesor) y archivo de la foto (sin extensión) dentro
    // de src/recursos/equipo.
    private static final String[] NOMBRES = {"Valeria", "Hellen", "Isabella", "Allison"};
    private static final String[] APELLIDOS = {"Bermúdez Chacón", "Herrera Cascante", "Guzmán Leiva", "Ortega Aguilar"};
    private static final String[] CORREOS = {"vbermudezch@gmail.com", "hellenicoleherrerac@gmail.com", "isabellagl2008@hotmail.com", "aliortegaaguilar@gmail.com"};
    private static final String[] FOTOS = {"fvaleria", "fhellen", "fisabella", "fallison"};
    // Formatos de foto que acepta y tamaño del marco de cada foto, en píxeles.
    private static final String[] EXTENSIONES = {".png", ".jpg", ".jpeg"};
    private static final int LADO = 100;

    // Abre la ventana encima de la pantalla actual. Recibe cualquier componente de la ventana.
    // "origen" sirve para saber en qué ventana se dibuja.
    public static void mostrar(Component origen) {
        // Caja con borde pixelado que apila todo en vertical, con margen interno.
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(18);
        panel.add(centrar(Etiqueta.titulo(TITULO, 20f, Etiqueta.Rol.TEXTO)));
        // Descripción del proyecto.
        panel.add(Box.createVerticalStrut(6));
        panel.add(centrar(Etiqueta.texto(SUBTITULO, 26f, Etiqueta.Rol.ACENTO)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(centrar(Etiqueta.texto("<html><div style='width:620px;text-align:center'>" + DESCRIPCION + "</div></html>", 22f, Etiqueta.Rol.SUAVE)));
        // Las cuatro integrantes en 2 x 2. Una celda por integrante.
        panel.add(Box.createVerticalStrut(14));
        JPanel cuadricula = new JPanel(new GridLayout(2, 2, 40, 14));
        cuadricula.setOpaque(false);
        for (int i = 0; i < NOMBRES.length; i++) {
            // Agrega la foto y el nombre.
            cuadricula.add(celda(i));
        }
        // Agrega la cuadrícula. Cortina oscura con el panel al centro. Botón para cerrar la ventana. Al hacer clic se cierra.
        panel.add(centrar(cuadricula));
        CapaModal capa = new CapaModal(panel);
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA);
        cerrar.addActionListener(e -> capa.cerrar());
        // Agrega el botón. Muestra la ventana.
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(cerrar));
        capa.mostrar(origen);
    }

    // Una celda, el marco de la foto arriba y el nombre abajo.
    // Recibe el número de la integrante.
    private static JComponent celda(int i) {
        // Columna con la foto y el nombre apilados en vertical, con el marco de la foto.
        JPanel celda = new JPanel();
        celda.setOpaque(false);
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS));
        celda.add(centrar(new Foto(rutaFoto(FOTOS[i]))));
        // Primer nombre.
        celda.add(Box.createVerticalStrut(6));
        celda.add(centrar(Etiqueta.titulo(NOMBRES[i], 15f, Etiqueta.Rol.TEXTO)));
        celda.add(centrar(Etiqueta.texto(APELLIDOS[i], 22f, Etiqueta.Rol.SUAVE)));
        celda.add(centrar(Etiqueta.texto(CORREOS[i], 19f, Etiqueta.Rol.ACENTO)));
        // Entrega la celda.
        return celda;
    }

    // Busca la foto con cualquiera de las extensiones permitidas; devuelve null si todavía no existe.
    // Recibe el nombre del archivo sin extensión.
    private static String rutaFoto(String nombre) {
        // Prueba .png, .jpg y .jpeg. Si el archivo existe. Entrega su ruta.
        for (String ext : EXTENSIONES) {
            if (Recursos.existe("equipo/" + nombre + ext)) {
                return "equipo/" + nombre + ext;
            }
        }
        // Todavía no hay foto.
        return null;
    }

    // Centra un componente dentro de la caja.
    private static JComponent centrar(JComponent c) {
        // Lo alinea al centro.
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }

    // Marco cuadrado para la foto, que queda vacío si todavía no hay foto, hecho con un componente pequeño que solo dibuja
    // el marco.
    private static class Foto extends JComponent {
        // Ruta de la foto (null si no hay).
        private final String ruta;

        // Recibe la ruta de la foto.
        Foto(String ruta) {
            this.ruta = ruta;
        }

        // Tamaño que necesita el marco.
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(LADO, LADO);
        }

        // Tamaño máximo del marco.
        @Override
        public Dimension getMaximumSize() {
            // Nunca se estira.
            return getPreferredSize();
        }

        // Swing lo llama cada vez que hay que dibujar.
        @Override
        protected void paintComponent(Graphics g) {
            // Usa una copia del pincel y dibuja el borde y el color del fondo del marco.
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Tema.borde());
            g2.fillRect(0, 0, LADO, LADO);
            g2.setColor(Tema.caja());
            // Dibuja el fondo. Si ya existe la foto. La dibuja dentro del marco.
            g2.fillRect(4, 4, LADO - 8, LADO - 8);
            if (ruta != null) {
                g2.drawImage(Sprites.cargarEscalada(ruta, LADO - 8, LADO - 8), 4, 4, null);
            }
            // Libera el pincel.
            g2.dispose();
        }
    }
}
