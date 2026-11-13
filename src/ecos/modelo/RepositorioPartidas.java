package ecos.modelo;

// Base de datos, la conexión, la consulta con ? y el resultado.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Guarda y lee las victorias en la tabla partidas (usuario, modo, intentos, premio_dolares, premio_colones, dolar, fecha).
// Cada método abre y cierra su conexión (ConexionBD). Si la base falla lanza RuntimeException; quien lo llama decide qué
// mostrar. Solo se guardan partidas de cuentas que existen en la tabla usuarios, así que un invitado nunca queda guardado.
// A cargo de la Parte 2.
public class RepositorioPartidas {

    // Guarda la victoria Y suma las monedas en una sola transacción: o quedan las dos cosas o ninguna.
    // true si quedaron las dos, false si la cuenta no existe (no se guarda nada).
    public boolean guardarVictoriaConMonedas(Partida p, int monedas) {
        try (Connection c = ConexionBD.abrir()) {
            c.setAutoCommit(false);
            try {
                boolean ok = insertar(c, p) && sumarMonedas(c, p.getUsuario(), monedas);
                if (ok) {
                    c.commit();
                } else {
                    c.rollback();
                }
                return ok;
            } catch (SQLException | RuntimeException e) {
                // Si algo falla a la mitad se deshace todo.
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la victoria y las monedas", e);
        }
    }

    // Top de jugadores (las partidas del multijugador en red NO cuentan): la mejor partida de cada usuario, de mayor a menor premio en dólares (a igual premio gana la más
    // antigua). Las monedas no influyen.
    public List<Partida> top(int cantidad) {
        String sql = "SELECT usuario, modo, intentos, premio_dolares, premio_colones, dolar, fecha FROM ("
                + " SELECT DISTINCT ON (LOWER(usuario)) usuario, modo, intentos, premio_dolares, premio_colones, dolar, fecha"
                + " FROM partidas WHERE modo <> 'red' ORDER BY LOWER(usuario), premio_dolares DESC, fecha ASC) mejores"
                + " ORDER BY premio_dolares DESC, fecha ASC LIMIT ?";
        List<Partida> lista = new ArrayList<>();
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Partida(rs.getString("usuario"), ModoJuego.deCodigo(rs.getString("modo")),
                            rs.getInt("intentos"), rs.getDouble("premio_dolares"), rs.getDouble("premio_colones"),
                            rs.getDouble("dolar"), rs.getTimestamp("fecha").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer el ranking", e);
        }
        return lista;
    }

    // Inserta la partida usando el nombre de la cuenta tal como está en usuarios. 0 filas = esa cuenta no existe.
    private boolean insertar(Connection c, Partida p) throws SQLException {
        String sql = "INSERT INTO partidas (usuario, modo, intentos, premio_dolares, premio_colones, dolar, fecha)"
                + " SELECT usuario, ?, ?, ?, ?, ?, ? FROM usuarios WHERE LOWER(usuario) = LOWER(?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getModo().getCodigo());
            ps.setInt(2, p.getIntentos());
            ps.setDouble(3, p.getPremioDolares());
            ps.setDouble(4, p.getPremioColones());
            ps.setDouble(5, p.getDolar());
            ps.setTimestamp(6, Timestamp.valueOf(p.getFecha()));
            ps.setString(7, p.getUsuario());
            return ps.executeUpdate() == 1;
        }
    }

    // Suma las monedas dentro de la misma transacción.
    private boolean sumarMonedas(Connection c, String usuario, int cantidad) throws SQLException {
        String sql = "UPDATE usuarios SET monedas = monedas + ? WHERE LOWER(usuario) = LOWER(?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setString(2, usuario);
            return ps.executeUpdate() == 1;
        }
    }
}
