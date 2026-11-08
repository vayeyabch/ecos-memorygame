package ecos.modelo;

// Modelo de PantallaAcceso, revisa usuario y contraseña al iniciar sesión. No sabe nada de pantallas ni botones.
// A cargo de Valeria.
public class ModeloAcceso {

    // De aquí se buscan las cuentas.
    private final RepositorioUsuarios repo;

    public ModeloAcceso() {
        this(new RepositorioUsuarios());
    }

    public ModeloAcceso(RepositorioUsuarios repo) {
        this.repo = repo;
    }

    // Revisa lo escrito. Si está bien entrega la cuenta; si no lanza IllegalArgumentException con el mensaje para la pantalla.
    public Usuario iniciarSesion(String usuario, String clave) {
        if (usuario.isEmpty() || clave.isEmpty()) {
            throw new IllegalArgumentException("Escribí tu usuario y tu contraseña.");
        }
        Usuario u = repo.buscarPorUsuario(usuario);
        // Mismo mensaje si falla el usuario o la contraseña, para no dar pistas.
        if (u == null || !u.getClaveHash().equals(Seguridad.hash(clave))) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }
        return u;
    }
}
