package ecos.modelo;

// Datos de una cuenta registrada, usuario, nombre, apellidos, correo, contraseña (en hash), rostro y monedas.
// Es solo una "caja de datos", guarda y entrega información.
// A cargo de Valeria.
public class Usuario {

    private final String usuario;
    private final String nombre;
    private final String apellidos;
    private final String correo;
    // Contraseña convertida en hash, nunca la real.
    private final String claveHash;
    // Datos del rostro registrado, null si todavía no lo registró.
    private final String rostro;
    // Monedas de la cuenta, empieza en 0.
    private final int monedas;

    // Cuenta nueva sin rostro y con 0 monedas.
    public Usuario(String usuario, String nombre, String apellidos, String correo, String claveHash) {
        this(usuario, nombre, apellidos, correo, claveHash, null, 0);
    }

    // Cuenta con todos sus datos.
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
