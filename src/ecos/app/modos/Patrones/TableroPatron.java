package ecos.app.modos.Patrones;

import ecos.componentes.BaldosaPixel;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sprites;
import java.awt.GridLayout;
import java.util.function.IntConsumer;

// Matriz de 4x4 casillas del modo patrones.
public class TableroPatron extends PanelPixel {

    private final BaldosaPixel[] baldosas = new BaldosaPixel[16];

    public TableroPatron(int lado, IntConsumer alClic) {
        super(new GridLayout(4, 4, 10, 10));
        setPadding(8);
        for (int i = 0; i < 16; i++) {
            final int casilla = i;
            baldosas[i] = new BaldosaPixel(lado, Sprites.icono(i + 1, 64));
            baldosas[i].setAlClic(() -> alClic.accept(casilla));
            add(baldosas[i]);
        }
    }

    public void setLado(int lado) {
        for (BaldosaPixel b : baldosas) {
            b.setLado(lado);
        }
    }

    public void setJugable(boolean jugable) {
        for (BaldosaPixel b : baldosas) {
            b.setActiva(jugable);
        }
    }

    public void encender(int casilla, boolean encendida) {
        baldosas[casilla].setEncendida(encendida);
    }
}
