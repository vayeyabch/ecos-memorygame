package ecos.modelo;

// Maneja las monedas de cada cuenta. Se ganan al ganar una partida y se gastan en el Ojo espía.
// Se guardan en la columna monedas de la tabla usuarios, a través de RepositorioUsuarios, que no se importa
// porque está en el mismo paquete, ecos.modelo.
// No afectan el ranking ni el premio en dinero. Un jugador sin cuenta siempre tiene 0 y no se le guarda nada.
// Si la base falla se avisa en la consola y el juego sigue, sin romperse.
public class ModeloMonedas {

    // Monedas que gana quien gana una partida y lo que cuesta el Ojo espía.
    // Son public static final, o sea constantes que otras clases leen con ModeloMonedas.PRECIO_OJO.
    public static final int MONEDAS_POR_VICTORIA = 15;
    public static final int PRECIO_OJO = 30;

    // El repositorio es el que habla con la base de datos. Este modelo solo decide las reglas.
    private final RepositorioUsuarios repo;

    // Constructor por defecto: crea su propio repositorio. this(...) llama al otro constructor de esta misma clase.
    public ModeloMonedas() {
        this(new RepositorioUsuarios());
    }

    // Constructor que recibe el repositorio de afuera. Sirve para poder darle uno distinto, por ejemplo para pruebas.
    public ModeloMonedas(RepositorioUsuarios repo) {
        this.repo = repo;
    }

    // Devuelve las monedas del usuario. Da 0 si no tiene cuenta, es nuevo o la base no responde.
    public int monedas(String usuario) {
        // Un jugador sin cuenta llega como null, y siempre tiene 0.
        if (usuario == null) {
            return 0;
        }
        try {
            // Le pregunta el saldo a la base de datos, por medio del repositorio.
            return repo.monedas(usuario);
        // Si la base falla, imprime el detalle en la consola para poder revisarlo, y devuelve 0 para que el juego siga.
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Dice si le alcanza para el Ojo espía. Compara sus monedas con el precio. >= significa mayor o igual.
    public boolean alcanzaParaOjo(String usuario) {
        return monedas(usuario) >= PRECIO_OJO;
    }

    // Cobra el Ojo espía. Devuelve true si se cobró y false si no le alcanza o la base no responde.
    public boolean comprarOjo(String usuario) {
        // Sin cuenta no se puede comprar.
        if (usuario == null) {
            return false;
        }
        try {
            // Es el repositorio quien cobra, y devuelve true o false según si alcanzaba.
            return repo.cobrarMonedas(usuario, PRECIO_OJO);
        // Si la base falla, avisa en la consola y devuelve false, o sea que no se cobró.
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Arma el texto de la regla de las monedas que muestra la pantalla de reglas.
    // Usa las constantes, así que si cambia el valor, el texto se actualiza solo. Lleva etiquetas HTML para la negrita.
    // Este texto debe coincidir con la plantilla de Textos, con sus %s, para que se traduzca al inglés.
    public String reglaMonedas() {
        return "Cada victoria te da <b>" + MONEDAS_POR_VICTORIA + " monedas</b>. Con <b>" + PRECIO_OJO + "</b> comprás el Ojo espía. Sin cuenta no hay premio ni monedas.";
    }
}