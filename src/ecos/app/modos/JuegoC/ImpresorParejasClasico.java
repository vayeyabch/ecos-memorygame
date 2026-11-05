package ecos.app.modos.JuegoC;

// Clase de ayuda SOLO para ver en la consola dónde están las parejas de cada jugador.
// No cambia el juego: únicamente imprime. Se puede borrar cuando ya no haga falta.
public class ImpresorParejasClasico {

    // Imprime, para cada jugador, la matriz con el número de imagen de cada carta
    // y la lista de dónde está cada pareja (fila, columna).
    public static void imprimir(PartidaClasica partida) {
        for (int jugador = 0; jugador < 2; jugador++) {
            imprimirJugador(jugador, partida.getCaras(jugador));
        }
    }

    private static void imprimirJugador(int jugador, int[][] matriz) {
        int n = matriz.length; // la matriz es de n x n (6 x 6)

        System.out.println("===== JUGADOR " + (jugador + 1) + ": matriz de parejas =====");
        System.out.println("(mismo numero = misma imagen = una pareja)");

        // 1. La matriz: cada número es la imagen (1 a 18) de esa carta
        for (int i = 0; i < n; i++) {
            StringBuilder fila = new StringBuilder();
            for (int j = 0; j < n; j++) {
                fila.append(String.format("%3d", matriz[i][j] + 1)); // +1 para que vaya de 1 a 18
            }
            System.out.println(fila);
        }

        // 2. La lista: para cada imagen, las dos posiciones donde está (fila, columna)
        int parejas = n * n / 2;
        for (int imagen = 0; imagen < parejas; imagen++) {
            StringBuilder posiciones = new StringBuilder();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (matriz[i][j] == imagen) {
                        if (posiciones.length() > 0) {
                            posiciones.append(" y ");
                        }
                        posiciones.append("(fila ").append(i + 1).append(", col ").append(j + 1).append(")");
                    }
                }
            }
            System.out.println("Pareja " + (imagen + 1) + ": " + posiciones);
        }
        System.out.println();
    }
}
