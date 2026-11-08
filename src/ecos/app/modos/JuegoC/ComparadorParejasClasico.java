package ecos.app.modos.JuegoC;

// Dice si dos cartas son iguales o diferentes.
// Si son diferentes suena "fallo". Si son iguales el sonido ("par_encontrado")
// lo pone PantallaClasico.marcarPareja, así no suena dos veces.
public class ComparadorParejasClasico {

    public boolean sonIguales(int caraA, int caraB) {
        // Si las cartas son iguales, devuelve true, si no, devuelve false.
        return caraA == caraB;
    }

    public boolean comparar(int caraA, int caraB) {
        // Verifica si las cartas son iguales. si no son iguales, suena el fallo.
        boolean iguales = sonIguales(caraA, caraB);
        if (!iguales) {
            SonidosPartidaClasica.fallo();
        }
        return iguales;
    }
}
