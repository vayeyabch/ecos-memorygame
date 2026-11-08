package ecos.modelo;

import java.util.HashMap; // Mapa usuario -> monedas.
import java.util.Map; // Tipo de mapa.

// Modelo de las monedas: cada usuario acumula monedas al ganar y las gasta en el Ojo espía.
// Las monedas NO afectan el ranking ni el premio en dinero: eso sale solo de los intentos de la partida.
// Un jugador sin cuenta (usuario null) siempre tiene 0 monedas y no se le guarda nada.
// Responsable: Hellen.
public class ModeloMonedas {

    public static final int MONEDAS_POR_VICTORIA = 15; // Lo que gana quien gana una partida.
    public static final int PRECIO_OJO = 30; // Lo que cuesta el Ojo espía.

    // PENDIENTE (BD): este mapa se cambia por la columna de monedas (puntos) de la tabla usuarios. Hoy se pierde al cerrar el programa.
    private static final Map<String, Integer> SALDOS = new HashMap<>();

    private static String clave(String usuario) { // El usuario sin importar mayúsculas.
        return usuario.trim().toLowerCase();
    }

    public int monedas(String usuario) { // Monedas del usuario (0 si no tiene cuenta o es nuevo).
        return usuario == null ? 0 : SALDOS.getOrDefault(clave(usuario), 0);
    }

    public void sumarVictoria(String usuario) { // Suma las monedas de una victoria.
        if (usuario != null) { // Sin cuenta no se guarda nada.
            SALDOS.merge(clave(usuario), MONEDAS_POR_VICTORIA, Integer::sum);
        }
    }

    public boolean alcanzaParaOjo(String usuario) { // Dice si le alcanza para el Ojo espía.
        return monedas(usuario) >= PRECIO_OJO;
    }

    public boolean comprarOjo(String usuario) { // Cobra el Ojo espía; false si no le alcanza.
        if (!alcanzaParaOjo(usuario)) {
            return false;
        }
        SALDOS.put(clave(usuario), monedas(usuario) - PRECIO_OJO);
        return true;
    }

    public String reglaMonedas() { // Regla de las monedas para la pantalla de reglas.
        return "Cada victoria te da <b>" + MONEDAS_POR_VICTORIA + " monedas</b>. Con <b>" + PRECIO_OJO + "</b> comprás el Ojo espía. Sin cuenta no hay premio ni monedas.";
    }
}
