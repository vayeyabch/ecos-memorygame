package ecos.modelo;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;

// Modelo de PantallaReglasClasico, prepara a los dos jugadores. El jugador 1 es quien inició sesión;
// el jugador 2 inicia sesión o juega sin cuenta (como invitado).
// A cargo de Hellen.
public class ModeloReglasClasico {

    // Cómo va a jugar el jugador 2.
    private enum Jugador2 { SIN_ELEGIR, CUENTA, INVITADO }

    // Nombre del jugador 2 sin cuenta.
    private static final String INVITADO = "Invitado";

    // Al entrar a la pantalla todavía no eligió.
    private Jugador2 estado = Jugador2.SIN_ELEGIR;

    // Cada vez que se entra a la pantalla hay que elegir de nuevo.
    public void reiniciar() {
        estado = Jugador2.SIN_ELEGIR;
    }

    // El jugador 1 es quien inició sesión.
    public String nombreJugador1() {
        return Sesion.getNombre();
    }

    // Primera regla, con el tamaño que se eligió en el menú.
    public String reglaTablero() {
        int n = Sesion.getTamano();
        return "Cada jugador tiene su tablero (<b>" + n + "x" + n + "</b>) con las parejas en otro orden.";
    }

    // El jugador 2 juega sin cuenta.
    public void elegirInvitado() {
        Sesion.setJugador2(INVITADO);
        Sesion.setInvitado2(true);
        estado = Jugador2.INVITADO;
    }

    // El jugador 2 ya inició sesión (ModeloLoginJugador2 guardó su cuenta).
    public void elegirCuenta() {
        estado = Jugador2.CUENTA;
    }

    // Solo se puede empezar cuando el jugador 2 ya eligió.
    public boolean puedeComenzar() {
        return estado != Jugador2.SIN_ELEGIR;
    }

    // Mensaje cuando el jugador 2 inició sesión.
    public String textoCuenta() {
        return "Jugador 2: " + Sesion.getJugador2() + ". Suma monedas y entra al ranking.";
    }

    // Mensaje cuando el jugador 2 juega sin cuenta.
    public String textoInvitado() {
        return "Jugador 2: Invitado. Sin cuenta no hay monedas, ranking ni premio.";
    }

    // Mensaje cuando intenta comenzar sin elegir.
    public String textoFaltaElegir() {
        return "Primero elegí cómo juega el jugador 2: iniciar sesión o jugar sin cuenta.";
    }

    // Guarda al jugador 1 antes de empezar.
    public void prepararPartida() {
        Sesion.setJugador1(Sesion.getNombre());
    }
}
