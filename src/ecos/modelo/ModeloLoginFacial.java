package ecos.modelo;

// Utilidades de Java, List para las listas.
import java.util.List;

// Modelo de PantallaLoginFacial, compara las caras capturadas con los rostros registrados en la base de datos.
// A cargo de Valeria.
public class ModeloLoginFacial {

    // Distancia máxima para aceptar el rostro (menor = más parecido). Si deja entrar a quien no es, bajarla; si no
    // reconoce a la persona correcta, subirla.
    public static final double UMBRAL = 80.0;

    // De aquí se leen los rostros.
    private final RepositorioUsuarios repo;

    public ModeloLoginFacial() {
        this(new RepositorioUsuarios());
    }

    public ModeloLoginFacial(RepositorioUsuarios repo) {
        this.repo = repo;
    }

    // Busca a quién pertenece el rostro. Devuelve la cuenta, o lanza IllegalArgumentException con el mensaje para la
    // pantalla. Usa la base de datos y la comparación tarda un poco, no se llama desde el hilo de la interfaz.
    public Usuario identificar(List<byte[]> caras) {
        List<Usuario> conRostro;
        try {
            conRostro = repo.listarConRostro();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("No se pudo conectar con la base de datos.");
        }
        if (conRostro.isEmpty()) {
            throw new IllegalArgumentException("Todavía no hay rostros registrados.");
        }
        ReconocedorFacial.Resultado r = ReconocedorFacial.compartido().identificar(conRostro, caras);
        if (r == null || r.usuario == null || r.confianza >= UMBRAL) {
            throw new IllegalArgumentException("No te reconocí. Probá de nuevo o entrá con tu contraseña.");
        }
        return r.usuario;
    }
}
