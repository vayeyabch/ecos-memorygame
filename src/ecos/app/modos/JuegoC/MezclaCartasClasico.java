package ecos.app.modos.JuegoC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Crea la matriz de cartas de un jugador: cada imagen aparece dos veces y quedan mezcladas. Cada jugador pide la suya,
// por eso salen distintas.
public class MezclaCartasClasico {

    public static final int IMAGENES = 18;

    public static int[][] crearMatriz(int n, Random azar) {
        int total = n * n;
        int parejas = total / 2;

        List<Integer> imagenes = new ArrayList<>();
        for (int i = 0; i < IMAGENES; i++) {
            imagenes.add(i);
        }
        Collections.shuffle(imagenes, azar);

        List<Integer> cartas = new ArrayList<>();
        for (int p = 0; p < parejas; p++) {
            int imagen = imagenes.get(p % IMAGENES);
            cartas.add(imagen);
            cartas.add(imagen);
        }
        Collections.shuffle(cartas, azar);

        int[][] matriz = new int[n][n];
        for (int fila = 0; fila < n; fila++) {
            for (int col = 0; col < n; col++) {
                matriz[fila][col] = cartas.get(fila * n + col);
            }
        }
        return matriz;
    }

    public static int[] aplanar(int[][] matriz) {
        int n = matriz.length;
        // La pantalla recibe las caras en una sola fila, así que se recorre la matriz fila por fila.
        int[] plano = new int[n * n];
        for (int fila = 0; fila < n; fila++) {
            System.arraycopy(matriz[fila], 0, plano, fila * n, n);
        }
        return plano;
    }
}
