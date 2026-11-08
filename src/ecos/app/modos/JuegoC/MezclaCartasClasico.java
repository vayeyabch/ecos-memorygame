package ecos.app.modos.JuegoC;

// Utilidades de Java, ArrayList para listas que crecen, Collections para mezclar listas, List para las listas y Random
// Utilidades de Java, ArrayList para listas que crecen, Collections para mezclar listas, List para las listas y Random
// para los números al azar.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Crea la matriz de cartas de UN jugador, cada imagen aparece dos veces (su pareja)
// y quedan mezcladas al azar. Cada jugador pide la suya, por eso salen distintas.
public class MezclaCartasClasico {

    // Imágenes distintas que existen.
    public static final int IMAGENES = 18;

    public static int[][] crearMatriz(int n, Random azar) {
        int total = n * n;
        // En 6x6 son 18 parejas.
        int parejas = total / 2;

        List<Integer> imagenes = new ArrayList<>();
        for (int i = 0; i < IMAGENES; i++) {
            imagenes.add(i);
        }
        // Para tableros chicos, escoge imágenes al azar.
        Collections.shuffle(imagenes, azar);

        List<Integer> cartas = new ArrayList<>();
        for (int p = 0; p < parejas; p++) {
            int imagen = imagenes.get(p % IMAGENES);
            cartas.add(imagen);
            cartas.add(imagen);
        }
        Collections.shuffle(cartas, azar);

        // Llena la matriz con las cartas mezcladas.
        int[][] matriz = new int[n][n];
        for (int fila = 0; fila < n; fila++) {
            for (int col = 0; col < n; col++) {
                matriz[fila][col] = cartas.get(fila * n + col);
            }
        }
        return matriz;
    }

    // La pantalla recibe las caras en una sola fila, la matriz se recorre fila por fila.
    public static int[] aplanar(int[][] matriz) {
        int n = matriz.length;
        // La pantalla recibe las caras en una sola fila. Recorre la matriz fila por fila y copia cada fila en el plano.
        int[] plano = new int[n * n];
        for (int fila = 0; fila < n; fila++) {
            System.arraycopy(matriz[fila], 0, plano, fila * n, n);
        }
        return plano;
    }
}
