package ecos.modelo;

// Fecha y hora de la partida.
import java.time.LocalDateTime;

// Datos de una partida ganada por una cuenta (una fila de la tabla partidas).
// Es solo una "caja de datos", guarda y entrega información.
// A cargo de la Parte 2.
public class Partida {

    private final String usuario;
    private final ModoJuego modo;
    private final int intentos;
    private final double premioDolares;
    private final double premioColones;
    // Dólar de compra que se usó para calcular los colones.
    private final double dolar;
    private final LocalDateTime fecha;

    public Partida(String usuario, ModoJuego modo, int intentos, double premioDolares, double premioColones,
            double dolar, LocalDateTime fecha) {
        this.usuario = usuario;
        this.modo = modo;
        this.intentos = intentos;
        this.premioDolares = premioDolares;
        this.premioColones = premioColones;
        this.dolar = dolar;
        this.fecha = fecha;
    }

    public String getUsuario() { return usuario; }
    public ModoJuego getModo() { return modo; }
    public int getIntentos() { return intentos; }
    public double getPremioDolares() { return premioDolares; }
    public double getPremioColones() { return premioColones; }
    public double getDolar() { return dolar; }
    public LocalDateTime getFecha() { return fecha; }
}
