package ecos.modelo;

// Utilidades de Java, HashMap para diccionarios rápidos y Map para los diccionarios.
import java.util.HashMap;
import java.util.Map;

// Modelo de las monedas, cada usuario acumula monedas al ganar y las gasta en el Ojo espía.
// Las monedas NO afectan el ranking ni el premio en dinero, eso sale solo de los intentos de la partida.
// Un jugador sin cuenta (usuario null) siempre tiene 0 monedas y no se le guarda nada.
// A cargo de Hellen.
public class ModeloMonedas {

    // Lo que gana quien gana una partida. Lo que cuesta el Ojo espía.
    public static final int MONEDAS_POR_VICTORIA = 15;
    public static final int PRECIO_OJO = 30;

    // PENDIENTE (BD), este mapa se cambia por la columna de monedas (puntos) de la tabla usuarios. Hoy se pierde al
    // cerrar el programa.
    private static final Map<String, Integer> SALDOS = new HashMap<>();

    // El usuario sin importar mayúsculas.
    private static String clave(String usuario) {
        return usuario.trim().toLowerCase();
    }

    // Monedas del usuario (0 si no tiene cuenta o es nuevo).
    public int monedas(String usuario) {
        return usuario == null ? 0 : SALDOS.getOrDefault(clave(usuario), 0);
    }

    // Suma las monedas de una victoria.
    public void sumarVictoria(String usuario) {
        // Sin cuenta no se guarda nada.
        if (usuario != null) {
            SALDOS.merge(clave(usuario), MONEDAS_POR_VICTORIA, Integer::sum);
        }
    }

    // Dice si le alcanza para el Ojo espía.
    public boolean alcanzaParaOjo(String usuario) {
        return monedas(usuario) >= PRECIO_OJO;
    }

    // Cobra el Ojo espía; false si no le alcanza.
    public boolean comprarOjo(String usuario) {
        if (!alcanzaParaOjo(usuario)) {
            return false;
        }
        SALDOS.put(clave(usuario), monedas(usuario) - PRECIO_OJO);
        return true;
    }

    // Regla de las monedas para la pantalla de reglas.
    public String reglaMonedas() {
        return "Cada victoria te da <b>" + MONEDAS_POR_VICTORIA + " monedas</b>. Con <b>" + PRECIO_OJO + "</b> comprás el Ojo espía. Sin cuenta no hay premio ni monedas.";
    }
}
