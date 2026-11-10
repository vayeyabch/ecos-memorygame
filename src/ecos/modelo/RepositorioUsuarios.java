package ecos.modelo;

// Base de datos, la conexión, la consulta con ? y el resultado.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Guarda y busca cuentas en la tabla usuarios, incluidas sus monedas.
// Cada método abre y cierra su conexión (ConexionBD). Si la base falla lanza RuntimeException; quien lo llama decide qué
// mostrar. Las monedas se cambian con una sola instrucción SQL para que nunca queden negativas.
// A cargo de Valeria.
public class RepositorioUsuarios {

    // Busca una cuenta por su usuario (sin importar mayúsculas). null si no existe.
    public Usuario buscarPorUsuario(String usuario) {
        String sql = "SELECT usuario, nombre, apellidos, correo, clave_hash, rostro, monedas FROM usuarios"
                + " WHERE LOWER(usuario) = LOWER(?)";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Usuario(rs.getString("usuario"), rs.getString("nombre"), rs.getString("apellidos"),
                        rs.getString("correo"), rs.getString("clave_hash"), rs.getString("rostro"), rs.getInt("monedas"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuario", e);
        }
    }

    // Dice si ya existe un usuario con ese nombre.
    public boolean existe(String usuario) {
        return buscarPorUsuario(usuario) != null;
    }

    // Guarda una cuenta nueva, o actualiza sus datos si ya existía. Las monedas NO se tocan aquí.
    public void guardar(Usuario u) {
        String upd = "UPDATE usuarios SET nombre=?, apellidos=?, correo=?, clave_hash=?, rostro=? WHERE LOWER(usuario)=LOWER(?)";
        String ins = "INSERT INTO usuarios (usuario, nombre, apellidos, correo, clave_hash, rostro) VALUES (?,?,?,?,?,?)";
        try (Connection c = ConexionBD.abrir()) {
            try (PreparedStatement ps = c.prepareStatement(upd)) {
                ps.setString(1, u.getNombre());
                ps.setString(2, u.getApellidos());
                ps.setString(3, u.getCorreo());
                ps.setString(4, u.getClaveHash());
                ps.setString(5, u.getRostro());
                ps.setString(6, u.getUsuario());
                if (ps.executeUpdate() > 0) {
                    return;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setString(1, u.getUsuario());
                ps.setString(2, u.getNombre());
                ps.setString(3, u.getApellidos());
                ps.setString(4, u.getCorreo());
                ps.setString(5, u.getClaveHash());
                ps.setString(6, u.getRostro());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el usuario", e);
        }
    }

    // Monedas de la cuenta (0 si no existe).
    public int monedas(String usuario) {
        String sql = "SELECT monedas FROM usuarios WHERE LOWER(usuario) = LOWER(?)";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer las monedas", e);
        }
    }

    // Cobra monedas solo si le alcanzan, todo en una instrucción. true si se cobró, false si no tenía suficientes.
    public boolean cobrarMonedas(String usuario, int cantidad) {
        String sql = "UPDATE usuarios SET monedas = monedas - ? WHERE LOWER(usuario) = LOWER(?) AND monedas >= ?";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setString(2, usuario);
            ps.setInt(3, cantidad);
            // Filas cambiadas, 1 = cobró, 0 = no alcanzaba.
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cobrar monedas", e);
        }
    }

    // Todas las cuentas que registraron su rostro (para el login con Face ID).
    public List<Usuario> listarConRostro() {
        String sql = "SELECT usuario, nombre, apellidos, correo, clave_hash, rostro, monedas FROM usuarios"
                + " WHERE rostro IS NOT NULL AND rostro <> ''";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getString("usuario"), rs.getString("nombre"), rs.getString("apellidos"),
                        rs.getString("correo"), rs.getString("clave_hash"), rs.getString("rostro"), rs.getInt("monedas")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer los rostros", e);
        }
        return lista;
    }
}
