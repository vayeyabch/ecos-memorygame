package ecos.modelo;

// Sesion guarda los datos compartidos. Aquí se lee el nombre del jugador 1 y se deja guardado el jugador 2.
import ecos.app.Sesion;

// Es el modelo del login del jugador 2. Contiene la lógica: revisa los datos que escribió antes de jugar
// y, cuando son correctos, deja al jugador 2 guardado en la sesión. No dibuja nada ni conoce la ventana.
// ModeloAcceso no se importa porque está en el mismo paquete, ecos.modelo.
public class ModeloLoginJugador2 {
    // El modelo del login normal. Se reutiliza para revisar la cuenta y la contraseña, en vez de repetir esa lógica.
    private final ModeloAcceso acceso;

    // Constructor por defecto: crea su propio ModeloAcceso. this(...) llama al otro constructor de esta misma clase.
    public ModeloLoginJugador2() {
        this(new ModeloAcceso());
    }

    // Constructor que recibe el ModeloAcceso de afuera. Sirve para poder darle uno distinto, por ejemplo para pruebas.
    public ModeloLoginJugador2(ModeloAcceso acceso) {
        this.acceso = acceso;
    }

    // Revisa los datos del jugador 2. Devuelve un texto con el error, o null si todo está bien.
    // La revisión sigue un orden, y se detiene en el primer problema que encuentra.
    public String validar(String usuario, String clave) {
        // Primero revisa que no estén vacíos. El || significa o.
        if (usuario.isEmpty() || clave.isEmpty()) {
            return "Escribí tu usuario y tu contraseña.";
        }
        // No puede ser la misma cuenta del jugador 1. equalsIgnoreCase compara sin importar mayúsculas o minúsculas.
        if (usuario.equalsIgnoreCase(Sesion.getNombre())) {
            return "Esa cuenta ya es la del jugador 1. Usá otra.";
        }
        try {
            // La cuenta tiene que existir y la contraseña ser la correcta. Esa revisión ya la hace ModeloAcceso.
            // Si algo está mal, lanza un error con su propio mensaje.
            acceso.iniciarSesion(usuario, clave);
        // Si el error es de datos incorrectos, por ejemplo usuario inexistente o contraseña mala, devuelve su mensaje.
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        // Cualquier otro error en tiempo de ejecución se toma como un fallo de la base de datos.
        // printStackTrace imprime el detalle en la consola, para poder revisarlo, y al usuario se le da un mensaje simple.
        // Este catch va después del anterior porque el de arriba es más específico.
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "No se pudo conectar con la base de datos.";
        }
        // Si no hubo ningún problema, devuelve null, que significa que no hay error.
        return null;
    }

    // Deja al jugador 2 con su cuenta para la partida. Lo llama el controlador cuando validar no devolvió error.
    public void iniciarSesion(String usuario) {
        // Guarda su nombre como jugador 2.
        Sesion.setJugador2(usuario);
        // Marca que ya no es invitado. Con cuenta suma monedas y entra al ranking.
        Sesion.setInvitado2(false);
    }
}