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

    // Dice si la cuenta tiene rostro registrado. Lanza IllegalArgumentException si el usuario no existe.
    public boolean tieneRostro(String usuario) {
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Escribí tu usuario.");
        }
        Usuario u = repo.buscarPorUsuario(usuario);
        if (u == null) {
            throw new IllegalArgumentException("Ese usuario no existe.");
        }
        return u.getRostro() != null && !u.getRostro().isEmpty();
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
