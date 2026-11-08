package ecos.modelo;

import ecos.app.Sesion; // Aquí se guardan los jugadores de la partida.

// Modelo de PantallaReglasClasico: prepara a los dos jugadores. El jugador 1 es quien inició sesión;
// el jugador 2 inicia sesión o juega sin cuenta (como invitado).
// Responsable: Hellen.
public class ModeloReglasClasico {

    private enum Jugador2 { SIN_ELEGIR, CUENTA, INVITADO } // Cómo va a jugar el jugador 2.

    private static final String INVITADO = "Invitado"; // Nombre del jugador 2 sin cuenta.

    private Jugador2 estado = Jugador2.SIN_ELEGIR; // Al entrar a la pantalla todavía no eligió.

    public void reiniciar() { // Cada vez que se entra a la pantalla hay que elegir de nuevo.
        estado = Jugador2.SIN_ELEGIR;
    }

    public String nombreJugador1() { // El jugador 1 es quien inició sesión.
        return Sesion.getNombre();
    }

    public String reglaTablero() { // Primera regla, con el tamaño que se eligió en el menú.
        int n = Sesion.getTamano(); // 4 o 6.
        return "Cada jugador tiene su tablero (<b>" + n + "x" + n + "</b>) con las parejas en otro orden.";
    }

    public void elegirInvitado() { // El jugador 2 juega sin cuenta.
        Sesion.setJugador2(INVITADO);
        Sesion.setInvitado2(true);
        estado = Jugador2.INVITADO;
    }

    public void elegirCuenta() { // El jugador 2 ya inició sesión (ModeloLoginJugador2 guardó su cuenta).
        estado = Jugador2.CUENTA;
    }

    public boolean puedeComenzar() { // Solo se puede empezar cuando el jugador 2 ya eligió.
        return estado != Jugador2.SIN_ELEGIR;
    }

    public boolean esCuenta() { return estado == Jugador2.CUENTA; } // true si el jugador 2 inició sesión.

    public boolean esInvitado() { return estado == Jugador2.INVITADO; } // true si juega sin cuenta.

    public String textoCuenta() { // Mensaje cuando el jugador 2 inició sesión.
        return "Jugador 2: " + Sesion.getJugador2() + ". Suma monedas y entra al ranking.";
    }

    public String textoInvitado() { // Mensaje cuando el jugador 2 juega sin cuenta.
        return "Jugador 2: Invitado. Sin cuenta no hay monedas, ranking ni premio.";
    }

    public String textoFaltaElegir() { // Mensaje cuando intenta comenzar sin elegir.
        return "Primero elegí cómo juega el jugador 2: iniciar sesión o jugar sin cuenta.";
    }

    public void prepararPartida() { // Guarda al jugador 1 antes de empezar.
        Sesion.setJugador1(Sesion.getNombre());
    }
}
