package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar la fuente pixel del juego.
import java.awt.AlphaComposite; // Permite dibujar con transparencia.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Cursor; // Forma del puntero del mouse.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.FontMetrics; // Medidas de una fuente.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import java.awt.event.MouseAdapter; // Clase lista para escuchar el mouse sin implementar todos los métodos.
import java.awt.event.MouseEvent; // Datos del evento del mouse.
import javax.swing.JButton; // Botón de Swing: la clase de la que hereda esta.

public class BotonMenu extends JButton {

    public enum Estilo { PRINCIPAL, SECUNDARIO, ROSA, PESTANA } // Los colores posibles del botón.

    private static final int BORDE = 4; // Grosor del borde, en píxeles.
    private static final int SOMBRA = 5; // Alto de la sombra dura de abajo.
    private static final int SUBIDA = 2; // Cuánto sube el botón al pasar el mouse.
    private static final int BAJADA = 4; // Cuánto baja el botón al apretarlo.

    private Estilo estilo; // Estilo actual del botón.
    private boolean pequeno = false; // true = versión chica del botón (letra más chica).
    private boolean encima = false; // true = el mouse está encima del botón.
    private boolean apretado = false; // true = el botón está siendo apretado.
    private final Runnable alCambiarTema = this::repaint; // Acción que repinta el botón cuando cambia el tema.

    public BotonMenu(String texto, boolean principal) { // Constructor simple: principal = color durazno; si no, el secundario.
        this(texto, principal ? Estilo.PRINCIPAL : Estilo.SECUNDARIO); // Llama al otro constructor con el estilo que corresponde.
    }

    public BotonMenu(String texto, Estilo estilo) { // Constructor completo.
        super(texto); // Le pasa el texto al JButton.
        this.estilo = estilo; // Guarda el estilo.
        setFont(Fuentes.titulo(14f)); // Letra de títulos, tamaño 14.
        setContentAreaFilled(false); // Quita el fondo normal de Swing.
        setBorderPainted(false); // Quita el borde normal de Swing.
        setFocusPainted(false); // Quita el recuadro de foco de Swing.
        setOpaque(false); // Es transparente: el dibujo se hace a mano.
        setRolloverEnabled(false); // Quita el cambio de color de Swing al pasar el mouse.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Pone la manito al pasar el mouse.
        addMouseListener(new MouseAdapter() { // Escucha el mouse para animar el botón.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseEntered(MouseEvent e) { encima = true; repaint(); } // Al entrar el mouse, el botón sube.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseExited(MouseEvent e) { encima = false; apretado = false; repaint(); } // Al salir el mouse, vuelve a su lugar.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mousePressed(MouseEvent e) { apretado = true; repaint(); } // Al apretar, el botón baja.
            @Override // Indica que se reemplaza un método de MouseAdapter.
            public void mouseReleased(MouseEvent e) { apretado = false; repaint(); } // Al soltar, vuelve a su lugar.
        });
    }

    public void setEstilo(Estilo estilo) { // Cambia el estilo (por ejemplo, una pestaña seleccionada).
        this.estilo = estilo; // Guarda el estilo nuevo.
        repaint(); // Repinta.
    }

    public void setPequeno(boolean pequeno) { // Activa o desactiva la versión chica.
        this.pequeno = pequeno; // Guarda la decisión.
        setFont(Fuentes.titulo(pequeno ? 12f : 14f)); // Cambia el tamaño de la letra.
        revalidate(); // Avisa que el tamaño cambió.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public void addNotify() { // Swing lo llama cuando el botón entra a una ventana.
        super.addNotify(); // Deja que Swing haga lo suyo primero.
        Tema.alCambiar(alCambiarTema); // Empieza a escuchar los cambios de tema.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public void removeNotify() { // Swing lo llama cuando el botón sale de la ventana.
        Tema.quitar(alCambiarTema); // Deja de escuchar los cambios de tema.
        super.removeNotify(); // Deja que Swing haga lo suyo.
    }

    private int relleno() { // Espacio entre el texto y el borde, a los lados.
        return pequeno ? 12 : 20; // Más chico en la versión chica.
    }

    @Override // Indica que se reemplaza un método de JButton.
    public Dimension getPreferredSize() { // Tamaño que necesita el botón.
        FontMetrics fm = getFontMetrics(getFont()); // Medidas de la fuente.
        int alto = fm.getHeight() + (pequeno ? 14 : 22) + BORDE * 2; // Alto del cuerpo del botón.
        return new Dimension(fm.stringWidth(getText()) + relleno() * 2 + BORDE * 2, alto + SOMBRA + SUBIDA + 1); // Tamaño total, con sitio para la sombra y el movimiento.
    }

    private Color colorFondo() { // Color de relleno según el estilo.
        return switch (estilo) { // Elige según el estilo.
            case PRINCIPAL -> Tema.boton(); // Durazno.
            case SECUNDARIO -> Tema.boton2(); // Blanco (claro) o violeta (oscuro).
            case ROSA -> Tema.rosa(); // Rosa.
            case PESTANA -> Tema.caja(); // Color de caja (pestaña sin seleccionar).
        };
    }

    private Color colorTexto() { // Color de las letras según el estilo.
        return switch (estilo) { // Elige según el estilo.
            case PRINCIPAL -> Tema.textoBoton(); // Naranja oscuro.
            case SECUNDARIO -> Tema.textoBoton2(); // Azul (claro) o rosado (oscuro).
            case ROSA -> new Color(91, 22, 38); // Guinda oscuro.
            case PESTANA -> Tema.textoSuave(); // Letras apagadas.
        };
    }

    @Override // Indica que se reemplaza el método de dibujo de JButton.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar el botón.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        if (!isEnabled()) { // Si el botón está desactivado.
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // Lo dibuja a medias transparente.
        }
        int w = getWidth(); // Ancho del botón.
        int cuerpo = getHeight() - SOMBRA - SUBIDA - 1; // Alto del cuerpo (sin la sombra ni el espacio de movimiento).
        int y = apretado ? SUBIDA + BAJADA : (encima ? 0 : SUBIDA); // Posición vertical: sube con el mouse, baja al apretar.
        int sombra = apretado ? 1 : SOMBRA; // La sombra se achica cuando el botón está apretado.
        g2.setColor(Tema.sombra()); // Color de la sombra dura.
        g2.fillRect(0, y + cuerpo - BORDE, w, BORDE + sombra); // Dibuja la sombra pegada abajo del cuerpo.
        g2.setColor(Tema.borde()); // Color del borde.
        g2.fillRect(0, y, w, cuerpo); // Dibuja el cuerpo completo con el color del borde.
        g2.setColor(colorFondo()); // Color de relleno.
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, cuerpo - BORDE * 2); // Dibuja el relleno dejando visible el borde.
        g2.setColor(new Color(255, 255, 255, 100)); // Blanco semitransparente para el brillo.
        g2.fillRect(BORDE, y + BORDE, w - BORDE * 2, BORDE); // Franja clara arriba.
        g2.setColor(new Color(0, 0, 0, 41)); // Negro semitransparente para la sombra interior.
        g2.fillRect(BORDE, y + cuerpo - BORDE * 2, w - BORDE * 2, BORDE); // Franja oscura abajo.
        g2.setFont(getFont()); // Usa la letra del botón.
        FontMetrics fm = g2.getFontMetrics(); // Medidas de la fuente.
        g2.setColor(colorTexto()); // Color de las letras.
        int tx = (w - fm.stringWidth(getText())) / 2; // Centra el texto horizontalmente.
        int ty = y + (cuerpo - fm.getHeight()) / 2 + fm.getAscent(); // Centra el texto verticalmente.
        g2.drawString(getText(), tx, ty); // Dibuja el texto.
        g2.dispose(); // Libera el pincel.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMinimumSize() { // Tamaño mínimo del componente.
        return getPreferredSize(); // Nunca se achica más de lo que necesita.
    }

    @Override // Indica que se reemplaza un método de JComponent.
    public Dimension getMaximumSize() { // Tamaño máximo del componente.
        return getPreferredSize(); // Nunca se estira más de lo que necesita.
    }

    // Devuelve un panel que estira este botón a todo el ancho disponible.
    public javax.swing.JPanel anchoCompleto() { // No recibe nada.
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout()); // Panel con BorderLayout, que sí estira lo que tiene adentro.
        panel.setOpaque(false); // Lo hace transparente.
        panel.add(this, java.awt.BorderLayout.CENTER); // Pone el botón en el centro (se estira).
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height)); // Permite que se estire a lo ancho.
        panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT); // Lo alinea a la izquierda.
        return panel; // Entrega el panel.
    }
}
