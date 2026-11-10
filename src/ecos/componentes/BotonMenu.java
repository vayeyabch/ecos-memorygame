package ecos.componentes;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Tema;
import ecos.recursos.Fuentes;
// Librería gráfica AWT, AlphaComposite para la transparencia, Color para los colores, Cursor para el cursor del mouse,
// Dimension para los tamaños, FontMetrics para medir el texto, Graphics para dibujar y Graphics2D para dibujar con más
// opciones.
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Eventos del mouse y el teclado de AWT, MouseAdapter para reaccionar al mouse y MouseEvent para los datos de un clic.
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Librería Swing, JButton para los botones.
import javax.swing.JButton;

// Botón pixel del juego con varios estilos (principal, secundario, rosa, pestaña); sube al pasar el mouse y baja al apretarlo.
public class BotonMenu extends JButton {

    // Los colores posibles del botón.
    public enum Estilo { PRINCIPAL, SECUNDARIO, ROSA, PESTANA }

    // Grosor del borde en píxeles, alto de la sombra dura de abajo, y cuánto sube el botón al pasar el mouse y baja al
    // apretarlo.
    private static final int BORDE = 4;
    private static final int SOMBRA = 5;
    private static final int SUBIDA = 2;
    private static final int BAJADA = 4;

    // Estilo actual del botón. True = versión chica del botón (letra más chica). true = el mouse está encima del botón.
    // true = el botón está siendo apretado.
    private Estilo estilo;
    private boolean pequeno = false;
    private boolean encima = false;
    private boolean apretado = false;
    // Acción que repinta el botón cuando cambia el tema.
    private final Runnable alCambiarTema = this::repaint;

    // Constructor simple, si es principal usa el color durazno y si no el secundario.
    public BotonMenu(String texto, boolean principal) {
        // Llama al otro constructor con el estilo que corresponde.
        this(texto, principal ? Estilo.PRINCIPAL : Estilo.SECUNDARIO);
    }

    // Constructor completo.
    public BotonMenu(String texto, Estilo estilo) {
        // Le pasa el texto al JButton. Guarda el estilo. Letra de títulos, tamaño 14. Quita el fondo normal de Swing.
        super(texto);
        this.estilo = estilo;
        setFont(Fuentes.titulo(14f));
        setContentAreaFilled(false);
        // Quita el borde normal de Swing. Quita el recuadro de foco de Swing. Es transparente, el dibujo se hace a mano.
        // Quita el cambio de color de Swing al pasar el mouse.
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        // Pone la manito al pasar el mouse. Escucha el mouse para animar el botón.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            // Al entrar el mouse, el botón sube.
            @Override
            public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
            // Al salir el mouse, vuelve a su lugar.
            @Override
            public void mouseExited(MouseEvent e) { encima = false; apretado = false; repaint(); }
            // Al apretar, el botón baja.
            @Override
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); }
            // Al soltar, vuelve a su lugar.
            @Override
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); }
        });
    }

    // Cambia el estilo (por ejemplo, una pestaña seleccionada).
    public void setEstilo(Estilo estilo) {
        // Guarda el estilo nuevo.
        this.estilo = estilo;
        repaint();
    }

    // Activa o desactiva la versión chica.
    public void setPequeno(boolean pequeno) {
        // Guarda la decisión. Cambia el tamaño de la letra. Avisa que el tamaño cambió.
        this.pequeno = pequeno;
        setFont(Fuentes.titulo(pequeno ? 12f : 14f));
        revalidate();
        repaint();
    }

    // Swing lo llama cuando el botón entra a una ventana.
    @Override
    public void addNotify() {
        // Deja que Swing haga lo suyo primero. Empieza a escuchar los cambios de tema.
        super.addNotify();
        Tema.alCambiar(alCambiarTema);
    }

    // Swing lo llama cuando el botón sale de la ventana.
    @Override
    public void removeNotify() {
        // Deja de escuchar los cambios de tema. Deja que Swing haga lo suyo.
        Tema.quitar(alCambiarTema);
        super.removeNotify();
    }

    // Espacio entre el texto y el borde, a los lados.
    private int relleno() {
        // Más chico en la versión chica.
        return pequeno ? 12 : 20;
    }

    // Tamaño que necesita el botón.
    @Override
    public Dimension getPreferredSize() {
        // Toma las medidas de la fuente y calcula el alto del cuerpo del botón y su tamaño total, con sitio para la sombra y
        // el movimiento.
        FontMetrics fm = getFontMetrics(getFont());
        int alto = fm.getHeight() + (pequeno ? 14 : 22) + BORDE * 2;
        return new Dimension(fm.stringWidth(getText()) + relleno() * 2 + BORDE * 2, alto + SOMBRA + SUBIDA + 1);
    }

    // Color de relleno según el estilo.
    private Color colorFondo() {
        // Elige el color de fondo según el estilo.
        return switch (estilo) {
            case PRINCIPAL -> Tema.boton();
            case SECUNDARIO -> Tema.boton2();
            case ROSA -> Tema.rosa();
            // Color de caja (pestaña sin seleccionar).
            case PESTANA -> Tema.caja();
        };
    }

    // Color de las letras según el estilo.
    private Color colorTexto() {
        // Elige el color de las letras según el estilo.
        return switch (estilo) {
            case PRINCIPAL -> Tema.textoBoton();
            case SECUNDARIO -> Tema.textoBoton2();
            case ROSA -> new Color(91, 22, 38);
            // Letras apagadas.
            case PESTANA -> Tema.textoSuave();
        };
    }

    // Swing lo llama cada vez que hay que dibujar el botón.
    @Override
    protected void paintComponent(Graphics g) {
        // Usa una copia del pincel y, si el botón está desactivado, lo dibuja a medias transparente.
        Graphics2D g2 = (Graphics2D) g.create();
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        // Calcula el ancho y el alto del cuerpo (sin sombra ni espacio de movimiento) y la posición vertical, que sube con
        // el mouse y baja al apretar, y achica la sombra cuando está apretado.
        int w = getWidth();
        int cuerpo = getHeight() - SOMBRA - SUBIDA - 1;
        int y = apretado ? SUBIDA + BAJADA : (encima ? 0 : SUBIDA);
        int sombra = apretado ? 1 : SOMBRA;
        // Color de la sombra dura. Dibuja la sombra pegada abajo del cuerpo. Color del borde. Dibuja el cuerpo completo con
        // el color del borde.
        g2.setColor(Tema.sombra());
        g2.fillRect(0, y + cuerpo - BORDE, w, BORDE + sombra);
        g2.setColor(Tema.borde());
        g2.fillRect(0, y, w, cuerpo);
        // Color de relleno. Dibuja el relleno dejando visible el borde. Blanco semitransparente para el brillo. Franja clara arriba.
        g2.setColor(colorFondo());
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, cuerpo - BORDE * 2);
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, BORDE);
        // Negro semitransparente para la sombra interior. Franja oscura abajo. Usa la letra del botón. Medidas de la fuente.
        g2.setColor(new Color(0, 0, 0, 41));
        g2.fillRect(BORDE, y + cuerpo - BORDE * 2, w - BORDE * 2, BORDE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        // Color de las letras. Centra el texto horizontalmente. Centra el texto verticalmente. Dibuja el texto.
        g2.setColor(colorTexto());
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = y + (cuerpo - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), tx, ty);
        // Libera el pincel.
        g2.dispose();
    }

    // Tamaño mínimo del componente.
    @Override
    public Dimension getMinimumSize() {
        // Nunca se achica más de lo que necesita.
        return getPreferredSize();
    }

    // Tamaño máximo del componente.
    @Override
    public Dimension getMaximumSize() {
        // Nunca se estira más de lo que necesita.
        return getPreferredSize();
    }

    // Devuelve un panel que estira este botón a todo el ancho disponible.
    // No recibe nada.
    public javax.swing.JPanel anchoCompleto() {
        // Panel con BorderLayout, que sí estira lo que tiene adentro. Lo hace transparente. Pone el botón en el centro (se
        // estira). Permite que se estire a lo ancho.
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        panel.setOpaque(false);
        panel.add(this, java.awt.BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        // Lo alinea a la izquierda. Entrega el panel.
        panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        return panel;
    }
}
