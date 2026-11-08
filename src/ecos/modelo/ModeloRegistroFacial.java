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

    // True si el paso 1 ya se completó.
    public boolean hayDatos() {
        return ModeloRegistro.pendiente() != null;
    }

    // Guarda la cuenta pendiente con las caras capturadas. Devuelve el mensaje de error, o null si quedó guardada.
    public String guardar(List<byte[]> caras) {
        Usuario p = ModeloRegistro.pendiente();
        if (p == null) {
            return "Volvé al paso 1 y completá tus datos.";
        }
        if (caras == null || caras.isEmpty()) {
            return "No se capturó ningún rostro. Intentá de nuevo.";
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
