package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema actual.
import ecos.recursos.Fuentes; // Para usar la fuente pixel del juego.
import ecos.recursos.Sprites; // Para obtener las mascotas.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.Dimension; // Representa un tamaño (ancho y alto).
import java.awt.FontMetrics; // Medidas de una fuente.
import java.awt.Graphics; // Herramienta básica para dibujar.
import java.awt.Graphics2D; // Versión más completa de Graphics.
import javax.swing.JPanel; // Panel de Swing: la clase de la que hereda esta.
import javax.swing.Timer; // Reloj que ejecuta una acción cada cierto tiempo.

public class VisorEscaner extends JPanel {

    public enum Estado { NORMAL, ESCANEANDO, OK, ERROR } // Los estados posibles del visor.

    private static final int LADO = 260; // Lado del cuadro del visor.
    private static final int SOMBRA = 5; // Alto de la sombra dura de abajo.
    private static final int MASCOTA = 168; // Tamaño de la mascota dentro del visor.
    private static final int MASCOTAS = 9; // Cuántas mascotas hay.
    private static final int CAMBIO_MASCOTA_TICS = 13; // Cada cuántos pasos (de 100 ms) cambia la mascota.
    private static final double[] ESCALONES = {0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25}; // Altura del salto en 8 pasos.
    private static final Color FONDO = new Color(0x1C1327); // Violeta oscuro del visor (igual en los dos temas).
    private static final Color ROSA = new Color(0xFCA5AB); // Rosa de las esquinas y el haz.

    private final Timer reloj = new Timer(100, e -> alTic()); // Reloj de las animaciones.
    private Estado estado = Estado.NORMAL; // Estado actual.
    private int indice = 1; // Número de la mascota que se ve.
    private int tic = 0; // Cuenta los pasos de la animación.
    private double desplazamiento = 0; // Cuánto está corrida la mascota hacia los lados ahora.
    private int destino = 0; // Cuánto debería estar corrida.

    public VisorEscaner() { // Constructor: no recibe nada.
        setOpaque(false); // Es transparente (el dibujo se hace a mano).
        setPreferredSize(new Dimension(LADO, LADO + SOMBRA)); // Tamaño del visor más su sombra.
    }

    public void setEstado(Estado estado) { // Cambia el estado del visor.
        this.estado = estado; // Guarda el estado.
        repaint(); // Repinta.
    }

    public void setDesplazamiento(int px) { // Corre la mascota hacia un lado (para simular que gira la cabeza).
        this.destino = px * 2; // Guarda hasta dónde debe llegar.
    }

    public void iniciar() { // Empieza las animaciones.
        reloj.start(); // Arranca el reloj.
    }

    public void detener() { // Detiene las animaciones.
        reloj.stop(); // Detiene el reloj.
    }

    private void alTic() { // Se ejecuta en cada paso de la animación.
        tic++; // Cuenta un paso más.
        if (estado != Estado.ESCANEANDO && tic % CAMBIO_MASCOTA_TICS == 0) { // Si no está escaneando y toca cambiar.
            indice = indice % MASCOTAS + 1; // Pasa a la siguiente mascota.
        }
        desplazamiento += (destino - desplazamiento) / 2.5; // Se acerca poco a poco al destino.
        repaint(); // Repinta.
    }

    @Override // Indica que se reemplaza el método de dibujo de JPanel.
    protected void paintComponent(Graphics g) { // Swing lo llama cada vez que hay que dibujar.
        Graphics2D g2 = (Graphics2D) g.create(); // Copia del pincel para no afectar al original.
        Color marco = Tema.borde(); // Color normal del marco.
        if (estado == Estado.OK) { // Si salió bien.
            marco = Tema.acierto(); // El marco es verde.
        } else if (estado == Estado.ERROR) { // Si salió mal.
            marco = Tema.peligro(); // El marco es rojo.
        }
        g2.setColor(Tema.sombra()); // Color de la sombra dura.
        g2.fillRect(0, SOMBRA, LADO, LADO); // Dibuja la sombra.
        g2.setColor(marco); // Color del marco.
        g2.fillRect(0, 0, LADO, LADO); // Dibuja el marco.
        g2.setColor(FONDO); // Color del fondo del visor.
        g2.fillRect(4, 4, LADO - 8, LADO - 8); // Dibuja el fondo.

        g2.setClip(4, 4, LADO - 8, LADO - 8); // Todo lo que sigue se recorta dentro del visor.
        int salto = (int) Math.round(ESCALONES[tic % ESCALONES.length] * 16); // Altura del salto de la mascota.
        int x = (LADO - MASCOTA) / 2 + (int) Math.round(desplazamiento); // Posición horizontal de la mascota.
        int y = (LADO - MASCOTA) / 2 - salto; // Posición vertical de la mascota.
        g2.drawImage(Sprites.mascota(indice, MASCOTA), x, y, null); // Dibuja la mascota.

        g2.setColor(ROSA); // Color de las esquinas.
        int m = 12; // Distancia de las esquinas al borde.
        int l = 32; // Largo de cada línea de la esquina.
        int t = 5; // Grosor de las esquinas.
        g2.fillRect(m, m, l, t); g2.fillRect(m, m, t, l); // Esquina de arriba a la izquierda.
        g2.fillRect(LADO - m - l, m, l, t); g2.fillRect(LADO - m - t, m, t, l); // Esquina de arriba a la derecha.
        g2.fillRect(m, LADO - m - t, l, t); g2.fillRect(m, LADO - m - l, t, l); // Esquina de abajo a la izquierda.
        g2.fillRect(LADO - m - l, LADO - m - t, l, t); g2.fillRect(LADO - m - t, LADO - m - l, t, l); // Esquina de abajo a la derecha.

        if (estado == Estado.ESCANEANDO) { // Si está escaneando.
            int recorrido = LADO - 8 - 20; // Cuánto baja y sube el haz.
            int paso = tic % 32; // Posición dentro del ciclo de ida y vuelta (16 escalones cada lado).
            int arriba = paso < 16 ? paso : 32 - paso; // Sube y baja.
            int hy = 10 + arriba * recorrido / 16; // Posición vertical del haz.
            g2.setColor(ROSA); // Color del haz.
            g2.fillRect(12, hy, LADO - 24, 8); // Dibuja el haz.
            g2.setColor(new Color(0xFFF0E4)); // Color del borde interior del haz.
            g2.drawRect(12 + 1, hy + 1, LADO - 24 - 3, 5); // Dibuja el contorno claro del haz.
        }

        if (estado == Estado.OK) { // Si salió bien.
            g2.setColor(new Color(11, 7, 16, 184)); // Negro semitransparente.
            g2.fillRect(4, 4, LADO - 8, LADO - 8); // Oscurece el visor.
            g2.setFont(Fuentes.titulo(32f)); // Letra grande.
            FontMetrics fm = g2.getFontMetrics(); // Medidas de la fuente.
            g2.setColor(Tema.acierto()); // Color verde.
            g2.drawString("OK", (LADO - fm.stringWidth("OK")) / 2, (LADO - fm.getHeight()) / 2 + fm.getAscent()); // Escribe OK en el centro.
        }
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
}
