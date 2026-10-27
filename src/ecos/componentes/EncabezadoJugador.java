package ecos.componentes;

import ecos.app.Tema;
import ecos.recursos.Fuentes;
import ecos.recursos.Sprites;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Tarjeta de un jugador: mascota, nombre, intentos y monedas.
 * Con setActivo(true) se resalta el borde (es el turno de ese jugador).
 */
public class EncabezadoJugador extends PanelPixel {

    private final JLabel foto = new JLabel();
    private final JLabel nombre = new JLabel();
    private final JLabel intentos = new JLabel();
    private final JLabel monedas = new JLabel();
    private boolean activo = false;

    public EncabezadoJugador(String nombreJugador, int numeroMascota) {
        super(new GridBagLayout());
        setPadding(10);
        nombre.setFont(Fuentes.titulo(16f));
        intentos.setFont(Fuentes.texto(20f));
        monedas.setFont(Fuentes.texto(20f));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 6, 2, 6);
        c.gridx = 0; c.gridy = 0; c.gridheight = 3;
        add(foto, c);
        c.gridx = 1; c.gridheight = 1; c.anchor = GridBagConstraints.WEST;
        add(nombre, c);
        c.gridy = 1;
        add(intentos, c);
        c.gridy = 2;
        add(monedas, c);

        setMascota(numeroMascota);
        setNombre(nombreJugador);
        setIntentos(0);
        setMonedas(0);
        Tema.alCambiar(this::colorear);
        colorear();
    }

    public void setNombre(String texto) {
        nombre.setText(texto);
    }

    public void setIntentos(int cantidad) {
        intentos.setText("Intentos: " + cantidad);
    }

    public void setMonedas(int cantidad) {
        monedas.setText("Monedas: " + cantidad);
    }

    /** Mascota del 1 al 9. */
    public void setMascota(int numero) {
        foto.setIcon(new ImageIcon(Sprites.escalar(Sprites.mascota(numero), 64, 64)));
    }

    /** Resalta la tarjeta cuando es el turno de este jugador. */
    public void setActivo(boolean activo) {
        this.activo = activo;
        setColorFondo(activo ? Tema.dorado() : null);
        colorear();
    }

    private void colorear() {
        // sobre el fondo dorado (turno activo) el texto siempre es oscuro
        Color c = activo ? new Color(91, 37, 56) : Tema.texto();
        nombre.setForeground(c);
        intentos.setForeground(c);
        monedas.setForeground(c);
    }
}
