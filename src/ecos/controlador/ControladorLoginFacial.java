package ecos.controlador;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;
import ecos.componentes.VisorEscaner;
import ecos.modelo.CapturaFacial;
import ecos.modelo.ModeloLoginFacial;
import ecos.modelo.ReconocedorFacial;
import ecos.modelo.Usuario;
import ecos.pantallas.PantallaLoginFacial;
import ecos.recursos.Sonido;
// Utilidades de Java, List para las listas.
import java.util.List;
// Librería Swing, SwingUtilities para volver al hilo de la interfaz y Timer para esperar un momento.
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Controlador de PantallaLoginFacial, usa la cámara para guiar los tres movimientos (frente, izquierda, derecha) y, si salen
// bien, busca a quién pertenece el rostro. Si lo reconoce, inicia sesión y va al menú.
// A cargo de Valeria.
public class ControladorLoginFacial {

    // Cuánto se corre la mascota en cada movimiento, al frente, izquierda, derecha.
    private static final int[] POSES = {0, -22, 22};

    private final PantallaLoginFacial vista;
    private final ModeloLoginFacial modelo;
    // La captura que está corriendo (null si ninguna).
    private CapturaFacial captura = null;

    public ControladorLoginFacial(PantallaLoginFacial vista) {
        this(vista, new ModeloLoginFacial());
    }

    public ControladorLoginFacial(PantallaLoginFacial vista, ModeloLoginFacial modelo) {
        this.vista = vista;
        this.modelo = modelo;
        // Al tocar "Escanear mi rostro" se llama empezar(), al entrar a la pantalla alEntrar() y al salir cancelar().
        vista.setAccionEscanear(this::empezar);
        vista.setAlEntrar(() -> captura = null);
        vista.setAlSalir(this::cancelar);
    }

    // Si se sale de la pantalla se detiene la cámara.
    private void cancelar() {
        if (captura != null) {
            captura.cancelar();
            captura = null;
        }
    }

    private void empezar() {
        if (captura != null) {
            return;
        }
        CapturaFacial nueva;
        try {
            nueva = new CapturaFacial(ReconocedorFacial.compartido());
        } catch (Throwable t) {
            // Falta un archivo del detector o las librerías de OpenCV no cargaron.
            t.printStackTrace();
            falla("No se pudo preparar el reconocimiento facial.");
            return;
        }
        captura = nueva;
        vista.getBotonEscanear().setEnabled(false);
        vista.getVisor().setEstado(VisorEscaner.Estado.ESCANEANDO);
        vista.getVisor().setDesplazamiento(POSES[0]);
        Sonido.efecto("escaneo");
        nueva.iniciar(new CapturaFacial.Escucha() {
            @Override
            public void alCambiarPaso(int paso, String instruccion) {
                SwingUtilities.invokeLater(() -> {
                    if (captura != nueva) {
                        return;
                    }
                    vista.getVisor().setDesplazamiento(POSES[paso]);
                    vista.setTextoEstado(instruccion);
                    if (paso > 0) {
                        Sonido.efecto("tick");
                    }
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
                        identificar(caras);
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

    // Compara el rostro con la base de datos, en otro hilo para no congelar la pantalla.
    private void identificar(List<byte[]> caras) {
        vista.getVisor().setDesplazamiento(0);
        vista.setTextoEstado("Reconociendo tu rostro…");
        new Thread(() -> {
            Usuario encontrado = null;
            String error = null;
            try {
                encontrado = modelo.identificar(caras);
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            } catch (Throwable t) {
                t.printStackTrace();
                error = "Falló el reconocimiento facial.";
            }
            final Usuario u = encontrado;
            final String mensaje = error;
            SwingUtilities.invokeLater(() -> {
                if (u == null) {
                    falla(mensaje);
                    return;
                }
                captura = null;
                // Inicia sesión igual que con la contraseña.
                Sesion.setNombre(u.getUsuario());
                vista.getVisor().setEstado(VisorEscaner.Estado.OK);
                vista.setTextoEstado("¡Rostro reconocido! Hola, " + u.getUsuario() + ".");
                Sonido.efecto("registro_ok");
                Timer espera = new Timer(1200, e -> vista.irAMenu());
                espera.setRepeats(false);
                espera.start();
            });
        }, "reconocer-rostro").start();
    }

    // Muestra el error y deja volver a intentar.
    private void falla(String mensaje) {
        captura = null;
        vista.getVisor().setDesplazamiento(0);
        vista.getVisor().setEstado(VisorEscaner.Estado.ERROR);
        vista.setTextoEstado(mensaje);
        vista.getBotonEscanear().setEnabled(true);
        Sonido.efecto("error");
    }
}
