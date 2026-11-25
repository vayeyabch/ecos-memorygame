package ecos.componentes;

// Tema da los colores del marco de la foto.
import ecos.app.Tema;
// Recursos se usa para preguntar si existe el archivo de una foto antes de intentar cargarla.
import ecos.recursos.Recursos;
// Sprites carga la foto y la escala al tamaño del marco.
import ecos.recursos.Sprites;
// Component es el tipo general de cualquier pieza visual. Se usa para el origen y para centrar piezas.
import java.awt.Component;
// Dimension es un tamaño, con ancho y alto. Se usa para decirle a Swing cuánto mide el marco de la foto.
import java.awt.Dimension;
// Graphics es el pincel general con el que Swing pide dibujar. Llega como dato a paintComponent.
import java.awt.Graphics;
// Graphics2D es el pincel con más opciones. Es el que se usa para dibujar el marco.
import java.awt.Graphics2D;
// GridLayout acomoda las piezas en una cuadrícula de filas y columnas. Aquí forma el 2 x 2 de las integrantes.
import java.awt.GridLayout;
// Box trae los espacios invisibles. Aquí se usa para separar las piezas hacia abajo.
import javax.swing.Box;
// BoxLayout acomoda las piezas una debajo de otra. Aquí se usa en el panel y en cada celda.
import javax.swing.BoxLayout;
// JComponent es el tipo base de las piezas de Swing. Los métodos devuelven este tipo para poder agregarlas a un panel.
import javax.swing.JComponent;
// JPanel es un panel, un contenedor donde se meten otras piezas. Aquí se usa para la cuadrícula y las celdas.
import javax.swing.JPanel;

// Es la ventana de créditos del equipo. Muestra a las cuatro integrantes con su foto, apellidos y correo,
// y la descripción del proyecto. No es una Pantalla, es un diálogo que se muestra encima de la pantalla actual.
// PanelPixel, CapaModal, BotonMenu y Etiqueta no se importan porque están en el mismo paquete, ecos.componentes.
// Todo es static, así que se usa escribiendo VentanaAcercaDe.mostrar(...), sin crear un objeto.
public class VentanaAcercaDe {

    // Textos fijos de la ventana, juntos arriba. Son constantes porque no cambian.
    // Se escriben exactamente igual que en Textos, así se traducen al inglés. Los nombres de las integrantes no se traducen.
    private static final String TITULO = "ACERCA DE ECOS";
    private static final String SUBTITULO = "Proyecto Memory Game";
    private static final String DESCRIPCION = "Proyecto final del curso Desarrollo de Software II, realizado por estudiantes de Informática Empresarial.";
    private static final String CERRAR = "Cerrar";

    // Los datos de las integrantes están en cuatro arreglos paralelos: la posición 0 de cada uno es de Valeria,
    // la 1 de Hellen, y así. Los cuatro deben tener el mismo largo. Las fotos van sin extensión, dentro de src/recursos/equipo.
    private static final String[] NOMBRES = {"Valeria", "Hellen", "Isabella", "Allison"};
    private static final String[] APELLIDOS = {"Bermúdez Chacón", "Herrera Cascante", "Guzmán Leiva", "Ortega Aguilar"};
    private static final String[] CORREOS = {"vbermudezch@gmail.com", "hellenicoleherrerac@gmail.com", "isabellagl2008@hotmail.com", "aliortegaaguilar@gmail.com"};
    private static final String[] FOTOS = {"fvaleria", "fhellen", "fisabella", "fallison"};
    // Formatos de foto que acepta, en el orden en que los prueba.
    private static final String[] EXTENSIONES = {".png", ".jpg", ".jpeg"};
    // Tamaño del marco cuadrado de la foto.
    private static final int LADO = 100;

    // Arma y abre la ventana encima de la pantalla actual. La llama BotonAcercaDe al hacer clic.
    // El parámetro origen es una pieza de la pantalla desde la que se abre, y sirve para saber en qué ventana dibujar la capa.
    public static void mostrar(Component origen) {
        // Crea el panel con el estilo pixel del juego, con las piezas una debajo de otra y 18 píxeles de margen.
        PanelPixel panel = new PanelPixel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPadding(18);
        // Título, subtítulo y descripción, todos centrados. Cada strut es un espacio invisible entre ellos.
        panel.add(centrar(Etiqueta.titulo(TITULO, 20f, Etiqueta.Rol.TEXTO)));
        panel.add(Box.createVerticalStrut(6));
        panel.add(centrar(Etiqueta.texto(SUBTITULO, 26f, Etiqueta.Rol.ACENTO)));
        panel.add(Box.createVerticalStrut(8));
        // La descripción es larga, así que va en una caja HTML de 620 píxeles de ancho para que se parta en varias líneas.
        // Textos traduce solo lo que está entre etiquetas, y por eso la constante debe coincidir con la de Textos.
        panel.add(centrar(Etiqueta.texto("<html><div style='width:620px;text-align:center'>" + DESCRIPCION + "</div></html>", 22f, Etiqueta.Rol.SUAVE)));
        // Las cuatro integrantes en una cuadrícula de 2 filas por 2 columnas, con 40 de separación horizontal y 14 vertical.
        panel.add(Box.createVerticalStrut(14));
        JPanel cuadricula = new JPanel(new GridLayout(2, 2, 40, 14));
        // Transparente, para que se vea el panel de atrás.
        cuadricula.setOpaque(false);
        // Agrega una celda por integrante. NOMBRES.length es cuántas hay, o sea 4.
        for (int i = 0; i < NOMBRES.length; i++) {
            cuadricula.add(celda(i));
        }
        panel.add(centrar(cuadricula));
        // La capa se crea aquí y no antes porque el botón de cerrar la necesita. La capa contiene el panel y lo muestra encima.
        CapaModal capa = new CapaModal(panel);
        BotonMenu cerrar = new BotonMenu(CERRAR, BotonMenu.Estilo.ROSA);
        // Al hacer clic cierra la capa. La flecha es la función que se ejecuta, y e es el aviso del clic.
        cerrar.addActionListener(e -> capa.cerrar());
        panel.add(Box.createVerticalStrut(14));
        panel.add(centrar(cerrar));
        // Muestra la capa encima de la pantalla actual.
        capa.mostrar(origen);
    }

    // Arma la celda de una integrante: el marco de la foto arriba y sus datos abajo. El parámetro i es su posición en los arreglos.
    private static JComponent celda(int i) {
        JPanel celda = new JPanel();
        celda.setOpaque(false);
        // Las piezas de la celda van una debajo de otra.
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS));
        // La foto. rutaFoto busca el archivo con la extensión que exista y se lo pasa al marco.
        celda.add(centrar(new Foto(rutaFoto(FOTOS[i]))));
        celda.add(Box.createVerticalStrut(6));
        // El nombre con la fuente de títulos, y debajo los apellidos y el correo con la fuente de texto.
        celda.add(centrar(Etiqueta.titulo(NOMBRES[i], 15f, Etiqueta.Rol.TEXTO)));
        celda.add(centrar(Etiqueta.texto(APELLIDOS[i], 22f, Etiqueta.Rol.SUAVE)));
        celda.add(centrar(Etiqueta.texto(CORREOS[i], 19f, Etiqueta.Rol.ACENTO)));
        return celda;
    }

    // Busca la foto con cualquiera de las extensiones permitidas y devuelve su ruta. Devuelve null si no existe ninguna.
    // Resuelve que cada integrante pueda tener su foto en png, jpg o jpeg sin cambiar el código.
    private static String rutaFoto(String nombre) {
        // Prueba cada extensión en orden. existe pregunta si el archivo está, sin abrirlo.
        for (String ext : EXTENSIONES) {
            if (Recursos.existe("equipo/" + nombre + ext)) {
                return "equipo/" + nombre + ext;
            }
        }
        // Si ninguna existe, devuelve null, y el marco queda vacío.
        return null;
    }

    // Alinea una pieza al centro. En un BoxLayout vertical, cada pieza se alinea por su cuenta,
    // y este método evita repetir la misma línea para cada una.
    private static JComponent centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        return c;
    }

    // Es el marco cuadrado de la foto. Es una clase interna, o sea una clase escrita dentro de otra, y static,
    // así que no necesita un objeto de VentanaAcercaDe para existir. Es private porque solo esta ventana la usa.
    // Si todavía no hay foto, el marco se ve vacío.
    private static class Foto extends JComponent {
        // La ruta de la foto, o null si no hay.
        private final String ruta;

        // Guarda la ruta que recibe. Se llama escribiendo new Foto(ruta).
        Foto(String ruta) {
            this.ruta = ruta;
        }

        // Dice cuánto mide el marco: un cuadrado de LADO por LADO.
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(LADO, LADO);
        }

        // El tamaño máximo es el mismo, para que el marco nunca se estire.
        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        // Dibuja el marco y la foto. Swing lo llama solo.
        @Override
        protected void paintComponent(Graphics g) {
            // Copia el pincel para no afectar el original, y la convierte a Graphics2D.
            Graphics2D g2 = (Graphics2D) g.create();
            // Primero un cuadrado completo con el color del borde.
            g2.setColor(Tema.borde());
            g2.fillRect(0, 0, LADO, LADO);
            // Luego un cuadrado más chico encima, 4 píxeles hacia adentro por cada lado, que hace de fondo del marco.
            g2.setColor(Tema.caja());
            g2.fillRect(4, 4, LADO - 8, LADO - 8);
            // Si hay foto, la pide ya escalada al tamaño del hueco y la dibuja adentro. Si no, deja el hueco vacío.
            if (ruta != null) {
                g2.drawImage(Sprites.cargarEscalada(ruta, LADO - 8, LADO - 8), 4, 4, null);
            }
            // Libera el pincel copiado.
            g2.dispose();
        }
    }
}