package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.app.modos.JuegoC.ControladorLoginJugador2;
import ecos.app.modos.JuegoC.VentanaLoginJugador2;
import ecos.componentes.Etiqueta;
import ecos.modelo.ModeloLoginJugador2;
import ecos.modelo.ModeloMonedas;
import ecos.modelo.ModeloReglasClasico;
import ecos.pantallas.PantallaReglasClasico;
// Acciones de Java, Consumer para una acción que recibe un dato.
import java.util.function.Consumer;

// Controlador de PantallaReglasClasico, el jugador 2 inicia sesión o juega sin cuenta, y después se abre la partida.
// A cargo de Hellen.
public class ControladorReglasClasico {

    // La pantalla (lo que se ve), los jugadores de la partida y el modo de cambiar de pantalla.
    private final PantallaReglasClasico vista;
    private final ModeloReglasClasico modelo = new ModeloReglasClasico();
    private final Consumer<String> irA;

    public ControladorReglasClasico(PantallaReglasClasico vista, Consumer<String> irA) {
        this.vista = vista;
        this.irA = irA;
        // Al entrar se llama alEntrar(), al tocar Iniciar sesión abrirLogin(), al tocar Jugar sin cuenta elegirInvitado() y al
        // tocar Comenzar comenzar().
        vista.setAlEntrar(this::alEntrar);
        vista.setAlIniciarSesion(this::abrirLogin);
        vista.setAlInvitado(this::elegirInvitado);
        vista.setAlComenzar(this::comenzar);
    }

    private void alEntrar() {
        // El jugador 2 tiene que elegir otra vez.
        modelo.reiniciar();
        vista.setJugador1(modelo.nombreJugador1());
        vista.setReglaTablero(modelo.reglaTablero());
        vista.setReglaMonedas(new ModeloMonedas().reglaMonedas());
        vista.setEstadoJugador2(modelo.textoFaltaElegir(), Etiqueta.Rol.SUAVE);
    }

    private void abrirLogin() {
        VentanaLoginJugador2 ventana = new VentanaLoginJugador2();
        new ControladorLoginJugador2(ventana, new ModeloLoginJugador2(), usuario -> {
            // Ya inició sesión.
            modelo.elegirCuenta();
            vista.setEstadoJugador2(modelo.textoCuenta(), Etiqueta.Rol.ACIERTO);
        });
        ventana.mostrar(vista);
    }

    private void elegirInvitado() {
        modelo.elegirInvitado();
        vista.setEstadoJugador2(modelo.textoInvitado(), Etiqueta.Rol.ACENTO);
    }

    private void comenzar() {
        // El jugador 2 todavía no eligió.
        if (!modelo.puedeComenzar()) {
            vista.sonarError();
            vista.setEstadoJugador2(modelo.textoFaltaElegir(), Etiqueta.Rol.PELIGRO);
            return;
        }
        modelo.prepararPartida();
        // Empieza la partida.
        irA.accept("clasico");
    }
}
