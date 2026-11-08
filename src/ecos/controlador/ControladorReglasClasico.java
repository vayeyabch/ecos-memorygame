package ecos.controlador;

import ecos.app.modos.JuegoC.ControladorLoginJugador2;
import ecos.app.modos.JuegoC.VentanaLoginJugador2;
import ecos.componentes.Etiqueta;
import ecos.modelo.ModeloLoginJugador2;
import ecos.modelo.ModeloMonedas;
import ecos.modelo.ModeloReglasClasico;
import ecos.pantallas.PantallaReglasClasico;
import java.util.function.Consumer;

// Controlador de PantallaReglasClasico: el jugador 2 inicia sesión o juega sin cuenta, y después se abre la partida.
// Responsable: Hellen.
public class ControladorReglasClasico {

    private final PantallaReglasClasico vista; // La pantalla (lo que se ve).
    private final ModeloReglasClasico modelo = new ModeloReglasClasico(); // Jugadores de la partida.
    private final Consumer<String> irA; // Sirve para cambiar de pantalla.

    public ControladorReglasClasico(PantallaReglasClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        vista.setAlEntrar(this::alEntrar);               // al entrar -> alEntrar()
        vista.setAlIniciarSesion(this::abrirLogin);      // al tocar Iniciar sesión -> abrirLogin()
        vista.setAlInvitado(this::elegirInvitado);       // al tocar Jugar sin cuenta -> elegirInvitado()
        vista.setAlComenzar(this::comenzar);             // al tocar Comenzar -> comenzar()
    }

    private void alEntrar() {
        modelo.reiniciar(); // El jugador 2 tiene que elegir otra vez.
        vista.setJugador1(modelo.nombreJugador1());
        vista.setReglaTablero(modelo.reglaTablero());
        vista.setReglaMonedas(new ModeloMonedas().reglaMonedas());
        vista.setEstadoJugador2(modelo.textoFaltaElegir(), Etiqueta.Rol.SUAVE);
    }

    private void abrirLogin() {
        VentanaLoginJugador2 ventana = new VentanaLoginJugador2();
        new ControladorLoginJugador2(ventana, new ModeloLoginJugador2(), usuario -> {
            modelo.elegirCuenta(); // Ya inició sesión.
            vista.setEstadoJugador2(modelo.textoCuenta(), Etiqueta.Rol.ACIERTO);
        });
        ventana.mostrar(vista);
    }

    private void elegirInvitado() {
        modelo.elegirInvitado();
        vista.setEstadoJugador2(modelo.textoInvitado(), Etiqueta.Rol.ACENTO);
    }

    private void comenzar() {
        if (!modelo.puedeComenzar()) { // El jugador 2 todavía no eligió.
            vista.sonarError();
            vista.setEstadoJugador2(modelo.textoFaltaElegir(), Etiqueta.Rol.PELIGRO);
            return;
        }
        modelo.prepararPartida();
        irA.accept("clasico"); // Empieza la partida.
    }
}
