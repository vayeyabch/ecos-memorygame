package ecos.modelo;

// Datos de una cuenta: usuario, nombre, apellidos, correo, contraseña en hash, rostro y monedas. Solo guarda y entrega
// información.
public class Usuario {

    private final String usuario;
    private final String nombre;
    private final String apellidos;
    private final String correo;
    private final String claveHash;
    // Datos del rostro, null si todavía no lo registró.
    private final String rostro;
    private final int monedas;

    public Usuario(String usuario, String nombre, String apellidos, String correo, String claveHash) {
        this(usuario, nombre, apellidos, correo, claveHash, null, 0);
    }

    public Usuario(String usuario, String nombre, String apellidos, String correo, String claveHash, String rostro,
            int monedas) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.claveHash = claveHash;
        this.rostro = rostro;
        this.monedas = monedas;
    }

    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
    public String getClaveHash() { return claveHash; }
    public String getRostro() { return rostro; }
    public int getMonedas() { return monedas; }
}
