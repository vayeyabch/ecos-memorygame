package ecos.modelo;

// Utilidades de Java, List para las listas.
import java.util.List;

// Modelo de PantallaRegistroFacial (paso 2 de 2), guarda la cuenta en la base de datos junto con el rostro capturado.
// A cargo de Valeria.
public class ModeloRegistroFacial {

    // De aquí se guarda la cuenta.
    private final RepositorioUsuarios repo;

    public ModeloRegistroFacial() {
        this(new RepositorioUsuarios());
    }

    public ModeloRegistroFacial(RepositorioUsuarios repo) {
        this.repo = repo;
    }

    // Cuenta que ya existe y a la que se le va a agregar el rostro (null si es un registro nuevo). La pone
    // ControladorAcceso después de comprobar la contraseña.
    private static Usuario existente = null;

    public static void paraCuenta(Usuario u) { existente = u; }
    public static void limpiarExistente() { existente = null; }

    // True si se está agregando el rostro a una cuenta que ya existe.
    public boolean esCuentaExistente() { return existente != null; }

    // True si hay una cuenta con la que trabajar (la que ya existe, o la del paso 1).
    public boolean hayDatos() {
        return existente != null || ModeloRegistro.pendiente() != null;
    }

    // Crea la cuenta pendiente SIN rostro (el rostro es opcional). Devuelve el mensaje de error, o null si quedó guardada.
    public String guardarSinRostro() {
        Usuario p = ModeloRegistro.pendiente();
        if (p == null) {
            return "Volvé al paso 1 y completá tus datos.";
        }
        try {
            if (repo.existe(p.getUsuario())) {
                return "Ese usuario ya existe. Volvé al paso 1 y elegí otro.";
            }
            repo.guardar(new Usuario(p.getUsuario(), p.getNombre(), p.getApellidos(), p.getCorreo(), p.getClaveHash(),
                    null, 0));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "No se pudo guardar en la base de datos.";
        }
        ModeloRegistro.limpiarPendiente();
        return null;
    }

    // Guarda el rostro capturado: en la cuenta que ya existe, o crea la cuenta pendiente con su rostro. Devuelve el
    // mensaje de error, o null si quedó guardado.
    public String guardar(List<byte[]> caras) {
        if (caras == null || caras.isEmpty()) {
            return "No se capturó ningún rostro. Intentá de nuevo.";
        }
        // Cuenta que ya existía, solo se le pone el rostro (guardar actualiza si el usuario ya existe).
        if (existente != null) {
            try {
                repo.guardar(new Usuario(existente.getUsuario(), existente.getNombre(), existente.getApellidos(),
                        existente.getCorreo(), existente.getClaveHash(), ReconocedorFacial.codificar(caras), 0));
            } catch (RuntimeException e) {
                e.printStackTrace();
                return "No se pudo guardar en la base de datos.";
            }
            return null;
        }
        Usuario p = ModeloRegistro.pendiente();
        if (p == null) {
            return "Volvé al paso 1 y completá tus datos.";
        }
        try {
            // Por si alguien tomó ese usuario mientras tanto.
            if (repo.existe(p.getUsuario())) {
                return "Ese usuario ya existe. Volvé al paso 1 y elegí otro.";
            }
            repo.guardar(new Usuario(p.getUsuario(), p.getNombre(), p.getApellidos(), p.getCorreo(), p.getClaveHash(),
                    ReconocedorFacial.codificar(caras), 0));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "No se pudo guardar en la base de datos.";
        }
        ModeloRegistro.limpiarPendiente();
        return null;
    }
}