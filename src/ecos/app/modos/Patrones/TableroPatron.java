package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.BaldosaPixel;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
// Librería gráfica AWT, GridLayout para acomodar en una cuadrícula fija.
import java.awt.GridLayout;
// Acciones de Java, IntConsumer para una acción que recibe un número entero.
import java.util.function.IntConsumer;

// Matriz de 4x4 casillas del modo patrones.
public class TableroPatron extends PanelPixel {

    // Las 16 casillas, de la 0 a la 15.
    private final BaldosaPixel[] baldosas = new BaldosaPixel[16];

    // Lado = tamaño de cada casilla; alClic = qué hacer cuando tocan una (recibe su número).
    public TableroPatron(int lado, IntConsumer alClic) {
        // Cuadrícula de 4x4 con 10 px de separación. Margen interno del panel. Crea las 16 casillas.
        super(new GridLayout(4, 4, 10, 10));
        setPadding(8);
        for (int i = 0; i < 16; i++) {
            // Copia fija de i para usarla dentro de la lambda.
            final int casilla = i;
            // Cada casilla con su propio dibujo. Al tocarla, avisa cuál fue. La mete en la cuadrícula.
            baldosas[i] = new BaldosaPixel(lado, Sprites.icono(i + 1, 64));
            baldosas[i].setAlClic(() -> alClic.accept(casilla));
            add(baldosas[i]);
        }
    }

     // Cambia el tamaño de todas las casillas (para ajustarse a la ventana).
    public void setLado(int lado) {
        for (BaldosaPixel b : baldosas) {
            b.setLado(lado);
        }
    }

    // true = se pueden tocar; false = bloqueadas (mientras se muestra el patrón).
    public void setJugable(boolean jugable) {
        for (BaldosaPixel b : baldosas) {
            b.setActiva(jugable);
        }
    }
  // Enciende o apaga una casilla (así se muestra el patrón).
    public void encender(int casilla, boolean encendida) {
        baldosas[casilla].setEncendida(encendida);
    }
}
