package ecos.app.modos.JuegoC;

import java.util.function.Consumer;

// Controlador de VentanaLoginGanador: lee lo que escribe el jugador 2 y, en los dos casos (guardar u omitir), avisa con alTerminar para seguir con la victoria.
public class ControladorLoginGanador {

    private final VentanaLoginGanador vista; // La ventana.
    private final int puntos; // Puntos que se le suman al ganador.
    private final Consumer<String> alTerminar; // Recibe un texto ("+5 puntos para jason") o null si no sumó.

    public ControladorLoginGanador(VentanaLoginGanador vista, int puntos, Consumer<String> alTerminar) {
        this.vista = vista;
        this.puntos = puntos;
        this.alTerminar = alTerminar;
        vista.setAlGuardar(this::guardar); // Al guardar -> guardar().
        vista.setAlOmitir(this::omitir);   // Al omitir -> omitir().
    }

    private void guardar() {
        String usuario = vista.getUsuario(); // Lo que escribió.
        String clave = vista.getClave();
        if (usuario.isEmpty() || clave.isEmpty()) { // Faltan datos.
            vista.mostrarError("Escribí tu usuario y tu contraseña.");
            return;
        }
        vista.cerrar();
        alTerminar.accept("+" + puntos + " puntos para " + usuario);
    }

    private void omitir() {
        vista.cerrar();
        alTerminar.accept(null); // Sigue sin sumar.
    }
}
