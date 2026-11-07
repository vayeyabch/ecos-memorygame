package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema.
import ecos.recursos.Recursos; // Para saber si ya existe la foto de cada integrante.
import ecos.recursos.Sprites; // Para cargar las fotos.
import java.awt.Component; // Tipo general de cualquier componente.
import java.awt.Dimension; // Ancho y alto.
import java.awt.Graphics; // Pincel básico para dibujar.
import java.awt.Graphics2D; // Pincel avanzado para dibujar.
import java.awt.GridLayout; // Layout en cuadrícula.
import javax.swing.Box; // Para dejar espacios vacíos.
import javax.swing.BoxLayout; // Layout que apila en vertical.
import javax.swing.JComponent; // Componente básico de Swing.
import javax.swing.JPanel; // Panel de Swing.

public class VentanaAcercaDe {

    // TEXTOS de la ventana. Están todos aquí arriba para que sea fácil traducirlos al inglés después (los nombres no se traducen).
    private static final String TITULO = "ACERCA DE ECOS"; // EN: "ABOUT ECOS".
    private static final String SUBTITULO = "Proyecto Memory Game"; // EN: "Memory Game Project".
    private static final String DESCRIPCION = "Proyecto final del curso Desarrollo de Software II, realizado por estudiantes de Informática Empresarial."; // EN: "Final project of the Software Development II course, made by Business Informatics students."
    private static final String CERRAR = "Cerrar"; // EN: "Close".

    // Integrantes: primer nombre, apellidos y nombre del archivo de la foto (sin extensión) dentro de src/recursos/equipo.
    private static final String[] NOMBRES = {"Valeria", "Hellen", "Isabella", "Allison"}; // Primer nombre.
    private static final String[] APELLIDOS = {"Bermúdez Chacón", "Herrera Cascante", "Guzmán Leiva", "Ortega Aguilar"}; // Apellidos.
    private static final String[] CORREOS = {"vbermudezch@gmail.com", "hellenicoleherrerac@gmail.com", "isabellagl2008@hotmail.com", "aliortegaaguilar@gmail.com"}; // Correo de cada integrante (los pidió el profesor).
    private static final String[] FOTOS = {"fvaleria", "fhellen", "fisabella", "fallison"}; // Archivos de las fotos.
    private static final String[] EXTENSIONES = {".png", ".jpg", ".jpeg"}; // Formatos que acepta.
    private static final int LADO = 100; // Tamaño del marco de cada foto, en píxeles.

    // Abre la ventana encima de la pantalla actual. Recibe cualquier componente de la ventana.
    public static void mostrar(Component origen) { // "origen" sirve para saber en qué ventana se dibuja.
        PanelPixel panel = new PanelPixel(); // Caja con borde pixelado.
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Apila todo en vertical.
        panel.setPadding(18); // Margen interno.
        panel.add(centrar(Etiqueta.titulo(TITULO, 20f, Etiqueta.Rol.TEXTO))); // Título.
        panel.add(Box.createVerticalStrut(6)); // Espacio.
        panel.add(centrar(Etiqueta.texto(SUBTITULO, 26f, Etiqueta.Rol.ACENTO))); // Subtítulo.
        panel.add(Box.createVerticalStrut(8)); // Espacio.
        panel.add(centrar(Etiqueta.texto("<html><div style='width:620px;text-align:center'>" + DESCRIPCION + "</div></html>", 22f, Etiqueta.Rol.SUAVE))); // Descripción del proyecto.
        panel.add(Box.createVerticalStrut(14)); // Espacio.
        JPanel cuadricula = new JPanel(new GridLayout(2, 2, 40, 14)); // Las cuatro integrantes en 2 x 2.
        cuadricula.setOpaque(false); // Transparente.
        for (int i = 0; i < NOMBRES.length; i++) { // Una celda por integrante.
            cuadricula.add(celda(i)); // Agrega la foto y el nombre.
        }
        panel.add(centrar(cuadricula)); // Agrega la cuadrícula.
        CapaModal capa = new CapaModal(panel); // Cortina oscura con el panel al centro.
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA); // Botón para cerrar la ventana.
        cerrar.addActionListener(e -> capa.cerrar()); // Al hacer clic se cierra.
        panel.add(Box.createVerticalStrut(14)); // Espacio.
        panel.add(centrar(cerrar)); // Agrega el botón.
        capa.mostrar(origen); // Muestra la ventana.
    }

    // Una celda: el marco de la foto arriba y el nombre abajo.
    private static JComponent celda(int i) { // Recibe el número de la integrante.
        JPanel celda = new JPanel(); // Columna con foto y nombre.
        celda.setOpaque(false); // Transparente.
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS)); // Apila en vertical.
        celda.add(centrar(new Foto(rutaFoto(FOTOS[i])))); // El marco de la foto.
        celda.add(Box.createVerticalStrut(6)); // Espacio.
        celda.add(centrar(Etiqueta.titulo(NOMBRES[i], 15f, Etiqueta.Rol.TEXTO))); // Primer nombre.
        celda.add(centrar(Etiqueta.texto(APELLIDOS[i], 22f, Etiqueta.Rol.SUAVE))); // Apellidos.
        celda.add(centrar(Etiqueta.texto(CORREOS[i], 19f, Etiqueta.Rol.ACENTO))); // Correo.
        return celda; // Entrega la celda.
    }

    // Busca la foto con cualquiera de las extensiones permitidas; devuelve null si todavía no existe.
    private static String rutaFoto(String nombre) { // Recibe el nombre del archivo sin extensión.
        for (String ext : EXTENSIONES) { // Prueba .png, .jpg y .jpeg.
            if (Recursos.existe("equipo/" + nombre + ext)) { // Si el archivo existe.
                return "equipo/" + nombre + ext; // Entrega su ruta.
            }
        }
        return null; // Todavía no hay foto.
    }

    private static JComponent centrar(JComponent c) { // Centra un componente dentro de la caja.
        c.setAlignmentX(Component.CENTER_ALIGNMENT); // Lo alinea al centro.
        return c; // Lo entrega.
    }

    // Marco cuadrado para la foto. Si todavía no hay foto, queda vacío.
    private static class Foto extends JComponent { // Componente pequeño que solo dibuja el marco.
        private final String ruta; // Ruta de la foto (null si no hay).

        Foto(String ruta) { // Recibe la ruta de la foto.
            this.ruta = ruta; // La guarda.
        }

        @Override // Indica que se reemplaza un método de JComponent.
        public Dimension getPreferredSize() { // Tamaño que necesita el marco.
            return new Dimension(LADO, LADO); // Un cuadrado.
        }

        @Override // Indica que se reemplaza un método de JComponent.
        public Dimension getMaximumSize() { // Tamaño máximo del marco.
            return getPreferredSize(); // Nunca se estira.
        }

        @Override // Indica que se reemplaza el método de dibujo.
        protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
            Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel.
            g2.setColor(Tema.borde()); // Color del borde.
            g2.fillRect(0, 0, LADO, LADO); // Dibuja el borde.
            g2.setColor(Tema.caja()); // Color del fondo del marco.
            g2.fillRect(4, 4, LADO - 8, LADO - 8); // Dibuja el fondo.
            if (ruta != null) { // Si ya existe la foto.
                g2.drawImage(Sprites.cargarEscalada(ruta, LADO - 8, LADO - 8), 4, 4, null); // La dibuja dentro del marco.
            }
            g2.dispose(); // Libera el pincel.
        }
    }
}
