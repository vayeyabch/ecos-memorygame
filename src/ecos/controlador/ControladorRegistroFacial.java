package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.componentes.VisorEscaner;
import ecos.modelo.CapturaFacial;
import ecos.modelo.ModeloRegistroFacial;
import ecos.modelo.ReconocedorFacial;
import ecos.pantallas.PantallaRegistroFacial;
import ecos.recursos.Sonido;
// Utilidades de Java, List para las listas.
import java.util.List;
// Librería Swing, SwingUtilities para volver al hilo de la interfaz y Timer para esperar un momento.
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Controlador de PantallaRegistroFacial (paso 2 de 2), usa la cámara para pedir que la persona mire al frente y, si sale
// bien, guarda la cuenta con su rostro, marca el check en "Mirá al frente" y suena el sonido de éxito.
// A cargo de Valeria.
public class ControladorRegistroFacial {

    private final PantallaRegistroFacial vista;
    private final ModeloRegistroFacial modelo;
    // La captura que está corriendo (null si ninguna).
    private CapturaFacial captura = null;

    public ControladorRegistroFacial(PantallaRegistroFacial vista) {
        this(vista, new ModeloRegistroFacial());
    }

    public ControladorRegistroFacial(PantallaRegistroFacial vista, ModeloRegistroFacial modelo) {
        this.vista = vista;
        this.modelo = modelo;
        // Al tocar "Registrar mi rostro" se llama empezar(), al entrar a la pantalla alEntrar() y al salir cancelar().
        vista.setAccionCapturar(this::empezar);
        vista.setAccionOmitir(this::omitir);
        vista.setAlEntrar(this::alEntrar);
        vista.setAlSalir(this::cancelar);
    }

    private void alEntrar() {
        captura = null;
        if (!modelo.hayDatos()) {
            vista.setTextoEstado("Primero completá tus datos en el paso 1.");
        }
    }

    // Si se sale de la pantalla se detiene la cámara.
    private void cancelar() {
        if (captura != null) {
            captura.cancelar();
            captura = null;
        }
        // Al salir ya no se está agregando rostro a ninguna cuenta.
        ModeloRegistroFacial.limpiarExistente();
    }

    // Botón Omitir por ahora, si es un registro nuevo crea la cuenta sin rostro y si era una cuenta que ya existía solo vuelve.
    private void omitir() {
        if (captura != null) {
            return;
        }
        if (modelo.esCuentaExistente()) {
            vista.irAAcceso();
            return;
        }
        vista.getBotonOmitir().setEnabled(false);
        vista.getBotonCapturar().setEnabled(false);
        vista.setTextoEstado("Creando tu cuenta…");
        new Thread(() -> {
            String error = modelo.guardarSinRostro();
            SwingUtilities.invokeLater(() -> {
                vista.getBotonOmitir().setEnabled(true);
                if (error != null) {
                    falla(error);
                    return;
                }
                vista.getVisor().setEstado(VisorEscaner.Estado.OK);
                vista.setTextoEstado("¡Cuenta creada! Podés registrar tu rostro después.");
                Sonido.efecto(Sonido.EXITO);
                Timer espera = new Timer(1500, e -> vista.irAAcceso());
                espera.setRepeats(false);
                espera.start();
            });
        }, "crear-cuenta").start();
    }

    private void empezar() {
        if (captura != null) {
            return;
        }
        if (!modelo.hayDatos()) {
            falla("Primero completá tus datos en el paso 1.");
            return;
        }
        CapturaFacial nueva;
        try {
            nueva = new CapturaFacial(ReconocedorFacial.compartido(), true); // true = solo mirar al frente
        } catch (Throwable t) {
            // Falta un archivo del detector o las librerías de OpenCV no cargaron.
            t.printStackTrace();
            falla("No se pudo preparar el reconocimiento facial.");
            return;
        }
        captura = nueva;
        vista.getBotonCapturar().setEnabled(false);
        vista.getVisor().setEstado(VisorEscaner.Estado.ESCANEANDO);
        vista.getVisor().setDesplazamiento(0);
        Sonido.efecto("escaneo");
        nueva.iniciar(new CapturaFacial.Escucha() {
            @Override
            public void alCambiarPaso(int paso, String instruccion) {
                SwingUtilities.invokeLater(() -> {
                    if (captura != nueva) {
                        return;
                    }
                    vista.setTextoEstado(instruccion);
                });
            }

            @Override
            public void alProgresar(String texto) {
                SwingUtilities.invokeLater(() -> {
                    if (captura == nueva) {
                        vista.setTextoEstado(texto);
                    }
                });
            }

            @Override
            public void alTerminar(List<byte[]> caras) {
                SwingUtilities.invokeLater(() -> {
                    if (captura == nueva) {
                        guardar(caras);
                    }
                });
            }

            @Override
            public void alFallar(String mensaje) {
                SwingUtilities.invokeLater(() -> {
                    if (captura == nueva) {
                        falla(mensaje);
                    }
                });
            }
        });
    }

    // Guarda la cuenta en la base de datos (internet), en otro hilo para no congelar la pantalla.
    private void guardar(List<byte[]> caras) {
        vista.getVisor().setDesplazamiento(0);
        vista.setTextoEstado("Guardando tu cuenta…");
        new Thread(() -> {
            String error = modelo.guardar(caras);
            SwingUtilities.invokeLater(() -> {
                if (error != null) {
                    falla(error);
                    return;
                }
                captura = null;
                vista.getVisor().setEstado(VisorEscaner.Estado.OK);
                // Check en "Mirá al frente", mensaje y sonido de éxito.
                vista.marcarFrente();
                vista.setTextoEstado("¡Rostro registrado! Ya podés iniciar sesión.");
                Sonido.efecto(Sonido.EXITO);
                // Un momento después de mostrar el OK vuelve al acceso para iniciar sesión.
                Timer espera = new Timer(1500, e -> vista.irAAcceso());
                espera.setRepeats(false);
                espera.start();
            });
        }, "guardar-cuenta").start();
    }

    // Muestra el error y deja volver a intentar.
    private void falla(String mensaje) {
        captura = null;
        vista.getVisor().setDesplazamiento(0);
        vista.getVisor().setEstado(VisorEscaner.Estado.ERROR);
        vista.setTextoEstado(mensaje);
        vista.getBotonCapturar().setEnabled(true);
        Sonido.efecto("error");
    }
}