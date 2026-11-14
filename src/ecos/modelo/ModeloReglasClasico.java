package ecos.modelo;

import ecos.app.Sesion;

// Modelo de PantallaReglasClasico: prepara a los dos jugadores. El jugador 1 es quien inició sesión y el jugador 2
// inicia sesión o juega sin cuenta.
public class ModeloReglasClasico {

    private enum Jugador2 { SIN_ELEGIR, CUENTA, INVITADO }

    private static final String INVITADO = "Invitado";

    private Jugador2 estado = Jugador2.SIN_ELEGIR;

    public void reiniciar() {
        estado = Jugador2.SIN_ELEGIR;
    }

    public String nombreJugador1() {
        return Sesion.getNombre();
    }

    // Primera regla, con el tamaño que se eligió en el menú.
    public String reglaTablero() {
        int n = Sesion.getTamano();
        return "Cada jugador tiene su tablero (<b>" + n + "x" + n + "</b>) con las parejas en otro orden.";
    }

    public void elegirInvitado() {
        Sesion.setJugador2(INVITADO);
        Sesion.setInvitado2(true);
        estado = Jugador2.INVITADO;
    }

    public void elegirCuenta() {
        estado = Jugador2.CUENTA;
    }

    // Solo se puede empezar cuando el jugador 2 ya eligió.
    public boolean puedeComenzar() {
        return estado != Jugador2.SIN_ELEGIR;
    }

    public String textoCuenta() {
        return "Jugador 2: " + Sesion.getJugador2() + ". Suma monedas y entra al ranking.";
    }

    public String textoInvitado() {
        return "Jugador 2: Invitado. Sin cuenta no hay monedas, ranking ni premio.";
    }

    public String textoFaltaElegir() {
        return "Primero elegí cómo juega el jugador 2: iniciar sesión o jugar sin cuenta.";
    }

    public void prepararPartida() {
        Sesion.setJugador1(Sesion.getNombre());
    }
}
