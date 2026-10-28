package ecos.componentes;

import ecos.app.Tema; // Para usar los colores del tema y saber cuándo cambia.
import ecos.recursos.Fuentes; // Para usar las fuentes pixel del juego.
import ecos.recursos.Sprites; // Para obtener y escalar la imagen de la mascota.
import java.awt.Color; // Clase de Java para representar un color.
import java.awt.GridBagConstraints; // Reglas de posición de cada componente dentro del GridBagLayout.
import java.awt.GridBagLayout; // Layout en cuadrícula flexible.
import java.awt.Insets; // Márgenes (arriba, izquierda, abajo, derecha).
import javax.swing.ImageIcon; // Imagen lista para ponerse dentro de un JLabel.
import javax.swing.JLabel; // Etiqueta de Swing para mostrar texto o imágenes.

public class EncabezadoJugador extends PanelPixel { // Hereda de PanelPixel, así que ya tiene borde y sombra pixelados.

    private final JLabel foto = new JLabel(); // Etiqueta donde se muestra la mascota.
    private final JLabel nombre = new JLabel(); // Etiqueta con el nombre del jugador.
    private final JLabel intentos = new JLabel(); // Etiqueta con la cantidad de intentos.
    private final JLabel monedas = new JLabel(); // Etiqueta con la cantidad de monedas.
    private boolean activo = false; // true = es el turno de este jugador; false = no lo es.

    public EncabezadoJugador(String nombreJugador, int numeroMascota) { // Recibe el nombre y el número de mascota.
        super(new GridBagLayout()); // Usa GridBagLayout para acomodar foto y textos.
        setPadding(10); // Espacio interno de 10 píxeles.
        nombre.setFont(Fuentes.titulo(16f)); // El nombre usa la fuente de títulos, tamaño 16.
        intentos.setFont(Fuentes.texto(20f)); // Los intentos usan la fuente de texto, tamaño 20.
        monedas.setFont(Fuentes.texto(20f)); // Las monedas usan la fuente de texto, tamaño 20.

        GridBagConstraints c = new GridBagConstraints(); // Objeto que guarda la posición de cada componente.
        c.insets = new Insets(2, 6, 2, 6); // Margen alrededor de cada componente.
        c.gridx = 0; c.gridy = 0; c.gridheight = 3; // La foto va en la columna 0, fila 0, ocupando 3 filas de alto.
        add(foto, c); // Agrega la foto.
        c.gridx = 1; c.gridheight = 1; c.anchor = GridBagConstraints.WEST; // Pasa a la columna 1, una fila de alto, alineado a la izquierda.
        add(nombre, c); // Agrega el nombre en la primera fila.
        c.gridy = 1; // Baja a la segunda fila.
        add(intentos, c); // Agrega los intentos.
        c.gridy = 2; // Baja a la tercera fila.
        add(monedas, c); // Agrega las monedas.

        setMascota(numeroMascota); // Pone la mascota elegida.
        setNombre(nombreJugador); // Pone el nombre recibido.
        setIntentos(0); // Empieza con 0 intentos.
        setMonedas(0); // Empieza con 0 monedas.
        Tema.alCambiar(this::colorear); // Vuelve a colorear los textos cuando cambie el tema.
        colorear(); // Pone los colores iniciales.
    }

    public void setNombre(String texto) { // Cambia el nombre mostrado.
        nombre.setText(texto); // Escribe el nuevo nombre en la etiqueta.
    }

    public void setIntentos(int cantidad) { // Cambia los intentos mostrados.
        intentos.setText("Intentos: " + cantidad); // Escribe el texto con la cantidad.
    }

    public void setMonedas(int cantidad) { // Cambia las monedas mostradas.
        monedas.setText("Monedas: " + cantidad); // Escribe el texto con la cantidad.
    }

    // Mascota del 1 al 9.
    public void setMascota(int numero) { // Cambia la mascota mostrada.
        foto.setIcon(new ImageIcon(Sprites.escalar(Sprites.mascota(numero), 64, 64))); // Carga la mascota, la escala a 64x64 sin borrosidad y la pone en la etiqueta.
    }

    // Resalta la tarjeta cuando es el turno de este jugador.
    public void setActivo(boolean activo) { // Recibe true si es su turno.
        this.activo = activo; // Guarda el estado.
        setColorFondo(activo ? Tema.dorado() : null); // Si es su turno el fondo es dorado; si no, vuelve al color del tema.
        colorear(); // Ajusta el color del texto al nuevo fondo.
    }

    private void colorear() { // Método interno: pone el color correcto a los textos.
        // sobre el fondo dorado (turno activo) el texto siempre es oscuro
        Color c = activo ? new Color(91, 37, 56) : Tema.texto(); // Si está activo usa un color oscuro; si no, el del tema.
        nombre.setForeground(c); // Aplica el color al nombre.
        intentos.setForeground(c); // Aplica el color a los intentos.
        monedas.setForeground(c); // Aplica el color a las monedas.
    }
}