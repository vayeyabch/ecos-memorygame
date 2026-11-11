package ecos.modelo;

// Modelo de PantallaRegistro (paso 1 de 2), revisa los datos del formulario. Si están bien los deja guardados en memoria
// para registrar el rostro (paso 2, que es opcional) o para crear la cuenta de una vez sin rostro.
// A cargo de Valeria.
public class ModeloRegistro {

    // Límites de la tabla usuarios y largo mínimo de la contraseña.
    private static final int MAX_NOMBRE = 50;
    private static final int MAX_APELLIDOS = 50;
    private static final int MAX_CORREO = 100;
    private static final int MAX_USUARIO = 20;
    private static final int MIN_CLAVE = 6;

    // Cuenta a medio crear. Es static porque la pantalla del paso 1 y la del paso 2 tienen cada una su controlador y su
    // modelo, y los datos tienen que pasar de una a otra (igual que Sesion). La contraseña ya viene en hash.
    private static Usuario pendiente = null;

    // De aquí se revisa si el usuario ya existe.
    private final RepositorioUsuarios repo;

    public ModeloRegistro() {
        this(new RepositorioUsuarios());
    }

    public ModeloRegistro(RepositorioUsuarios repo) {
        this.repo = repo;
    }

    // Revisa el formulario. Devuelve el mensaje de error, o null si todo está bien (y deja la cuenta pendiente).
    public String validar(String nombre, String apellidos, String correo, String usuario, String clave, String clave2) {
        if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || usuario.isEmpty() || clave.isEmpty()
                || clave2.isEmpty()) {
            return "Completá todos los campos.";
        }
        if (nombre.length() > MAX_NOMBRE || apellidos.length() > MAX_APELLIDOS || correo.length() > MAX_CORREO
                || usuario.length() > MAX_USUARIO) {
            return "Algún campo es demasiado largo.";
        }
        // Algo@algo.algo, sin espacios.
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "El correo no es válido.";
        }
        if (usuario.contains(" ")) {
            return "El usuario no puede tener espacios.";
        }
        if (clave.length() < MIN_CLAVE) {
            return "La contraseña necesita " + MIN_CLAVE + " caracteres o más.";
        }
        if (!clave.equals(clave2)) {
            return "Las contraseñas no son iguales.";
        }
        try {
            if (repo.existe(usuario)) {
                return "Ese usuario ya existe. Probá con otro.";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "No se pudo conectar con la base de datos.";
        }
        // Todo bien, queda pendiente de registrar el rostro. La contraseña se guarda ya convertida en hash.
        pendiente = new Usuario(usuario, nombre, apellidos, correo, Seguridad.hash(clave));
        return null;
    }

    // Crea la cuenta que dejó pendiente validar sin registrar el rostro, que es opcional. Devuelve el mensaje de error, o
    // null si la cuenta quedó guardada.
    public String guardarSinRostro() {
        if (pendiente == null) {
            return "Completá todos los campos.";
        }
        try {
            if (repo.existe(pendiente.getUsuario())) {
                return "Ese usuario ya existe. Probá con otro.";
            }
            repo.guardar(pendiente);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "No se pudo guardar en la base de datos.";
        }
        pendiente = null;
        return null;
    }

    // La cuenta que está esperando el paso 2 (null si no hay).
    public static Usuario pendiente() {
        return pendiente;
    }

    // Borra la cuenta pendiente (cuando ya se guardó o se cancela).
    public static void limpiarPendiente() {
        pendiente = null;
    }
}
