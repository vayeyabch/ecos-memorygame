package ecos.app.modos.JuegoC;

// Dice si dos cartas son iguales o diferentes.
// Si son diferentes suena "fallo". Si son iguales el sonido ("par_encontrado")
// lo pone PantallaClasico.marcarPareja, así no suena dos veces.
public class ComparadorParejasClasico {

    public boolean sonIguales(int caraA, int caraB) {
        return caraA == caraB; //si las cartas son iguales, devuelve true, si no, devuelve false
    }

    public boolean comparar(int caraA, int caraB) {
        boolean iguales = sonIguales(caraA, caraB); // verifica si las cartas son iguales
        if (!iguales) { // si no son iguales, suena el fallo
            SonidosPartidaClasica.fallo();
        }
        return iguales;
    }
}
