package ecos.app.modos.Patrones;

import ecos.componentes.BaldosaPixel;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
import java.awt.GridLayout;
import java.util.function.IntConsumer;

// Matriz de 4x4 casillas del modo patrones.
public class TableroPatron extends PanelPixel {

    private final BaldosaPixel[] baldosas = new BaldosaPixel[16]; // las 16 casillas, de la 0 a la 15

    public TableroPatron(int lado, IntConsumer alClic) {  // lado = tamaño de cada casilla; alClic = qué hacer cuando tocan una (recibe su número)
        super(new GridLayout(4, 4, 10, 10)); // cuadrícula de 4x4 con 10 px de separación
        setPadding(8); // margen interno del panel
        for (int i = 0; i < 16; i++) {  // crea las 16 casillas
            final int casilla = i;  // copia fija de i para usarla dentro de la lambda
            baldosas[i] = new BaldosaPixel(lado, Sprites.icono(i + 1, 64));  // cada casilla con su propio dibujo
            baldosas[i].setAlClic(() -> alClic.accept(casilla));  // al tocarla, avisa cuál fue
            add(baldosas[i]); // la mete en la cuadrícula
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
