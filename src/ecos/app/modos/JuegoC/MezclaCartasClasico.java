package ecos.app.modos.JuegoC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Crea la matriz de cartas de UN jugador: cada imagen aparece dos veces (su pareja)
// y quedan mezcladas al azar. Cada jugador pide la suya, por eso salen distintas.
public class MezclaCartasClasico {

    public static final int IMAGENES = 18; // imágenes distintas que existen

    public static int[][] crearMatriz(int n, Random azar) {
        int total = n * n;
        int parejas = total / 2; // en 6x6 son 18 parejas

        List<Integer> imagenes = new ArrayList<>();
        for (int i = 0; i < IMAGENES; i++) {
            imagenes.add(i);
        }
        Collections.shuffle(imagenes, azar); // para tableros chicos, escoge imágenes al azar

        List<Integer> cartas = new ArrayList<>();
        for (int p = 0; p < parejas; p++) {
            int imagen = imagenes.get(p % IMAGENES);
            cartas.add(imagen); // la carta
            cartas.add(imagen); // su pareja
        }
        Collections.shuffle(cartas, azar);

        int[][] matriz = new int[n][n]; // llena la matriz con las cartas mezcladas
        for (int fila = 0; fila < n; fila++) {
            for (int col = 0; col < n; col++) {
                matriz[fila][col] = cartas.get(fila * n + col);
            }
        }
        return matriz;
    }

    // La pantalla recibe las caras en una sola fila: la matriz se recorre fila por fila.
    public static int[] aplanar(int[][] matriz) {
        int n = matriz.length;
        int[] plano = new int[n * n]; // la pantalla recibe las caras en una sola fila
        for (int fila = 0; fila < n; fila++) { // recorre la matriz fila por fila y copia cada fila en el plano
            System.arraycopy(matriz[fila], 0, plano, fila * n, n);
        }
        return plano;
    }
}
