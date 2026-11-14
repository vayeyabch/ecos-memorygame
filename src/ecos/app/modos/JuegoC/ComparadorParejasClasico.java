package ecos.app.modos.JuegoC;

// Dice si dos cartas son iguales. Si son diferentes suena el fallo; el sonido de pareja lo pone PantallaClasico para que
// no suene dos veces.
public class ComparadorParejasClasico {

    public boolean sonIguales(int caraA, int caraB) {
        return caraA == caraB;
    }

    public boolean comparar(int caraA, int caraB) {
        boolean iguales = sonIguales(caraA, caraB);
        if (!iguales) {
            SonidosPartidaClasica.fallo();
        }
        return iguales;
    }
}
