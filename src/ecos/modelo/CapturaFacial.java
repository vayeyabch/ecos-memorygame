package ecos.modelo;

// Utilidades de Java, ArrayList y List para las listas.
import java.util.ArrayList;
import java.util.List;
// Clases de la cámara (JavaCV), un cuadro, el convertidor a imagen de OpenCV y el lector de la cámara.
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
// Imagen de OpenCV y la función que la voltea como un espejo.
import org.bytedeco.opencv.opencv_core.Mat;
import static org.bytedeco.opencv.global.opencv_core.flip;

// Guía a la persona con la cámara. Puede pedir tres movimientos (mirar al frente, girar la cabeza a su izquierda y a su
// derecha) o solo mirar al frente (modo "solo frente", el que usa el registro). Solo avanza cuando DETECTA que el movimiento
// se está haciendo bien. Corre en un hilo aparte y avisa por medio de Escucha.
// Sirve igual para registrar el rostro que para entrar con Face ID, quien la usa decide qué hacer con las fotos.
// Los avisos de Escucha llegan en el hilo de la captura, no en el de la interfaz (el controlador debe pasarlos a Swing).
// A cargo de Valeria.
public class CapturaFacial {

    // Los movimientos, en orden, cuántas fotos de frente se toman, tiempo mínimo entre fotos, cuadros seguidos con el giro
    // correcto para darlo por bueno (unos 0,25 s), tiempo máximo por movimiento y pausa entre lecturas de la cámara.
    private static final ReconocedorFacial.Pose[] COMPLETA = {
        ReconocedorFacial.Pose.FRENTE, ReconocedorFacial.Pose.IZQUIERDA, ReconocedorFacial.Pose.DERECHA};
    private static final ReconocedorFacial.Pose[] SOLO_FRENTE = {ReconocedorFacial.Pose.FRENTE};
    private static final int FOTOS_FRENTE = 5;
    private static final long ESPERA_FOTOS_MS = 350;
    private static final int CONFIRMAR = 4;
    private static final long TIEMPO_POR_PASO_MS = 12000;
    private static final long PAUSA_MS = 60;

    // Lo que la captura le avisa a quien la usa.
    public interface Escucha {
        // Empezó un movimiento nuevo (0 = frente, 1 = izquierda, 2 = derecha) con el texto a mostrar.
        void alCambiarPaso(int paso, String instruccion);

        // Mensaje de avance o de ayuda mientras se hace el movimiento.
        void alProgresar(String texto);

        // Hizo todos los movimientos pedidos, entrega las fotos de frente.
        void alTerminar(List<byte[]> caras);

        // No se pudo, con el motivo.
        void alFallar(String mensaje);
    }

    private final ReconocedorFacial reconocedor;
    // Los movimientos que se piden en esta captura.
    private final ReconocedorFacial.Pose[] secuencia;
    private final OpenCVFrameConverter.ToMat aMat = new OpenCVFrameConverter.ToMat();
    // Se pone en true para detener la captura desde fuera.
    private volatile boolean cancelado = false;

    // Pide los tres movimientos (frente, izquierda y derecha).
    public CapturaFacial(ReconocedorFacial reconocedor) {
        this(reconocedor, false);
    }

    // Si soloFrente es true, pide únicamente mirar al frente; si es false, los tres movimientos.
    public CapturaFacial(ReconocedorFacial reconocedor, boolean soloFrente) {
        this.reconocedor = reconocedor;
        this.secuencia = soloFrente ? SOLO_FRENTE : COMPLETA;
    }

    // Empieza la captura en un hilo aparte.
    public void iniciar(Escucha escucha) {
        Thread hilo = new Thread(() -> correr(escucha), "captura-facial");
        hilo.setDaemon(true);
        hilo.start();
    }

    // Detiene la captura y libera la cámara.
    public void cancelar() {
        cancelado = true;
    }

    // Todo el trabajo de la captura.
    private void correr(Escucha escucha) {
        OpenCVFrameGrabber camara = new OpenCVFrameGrabber(0);
        try {
            camara.setImageWidth(640);
            camara.setImageHeight(480);
            camara.start();
        } catch (Exception | UnsatisfiedLinkError e) {
            e.printStackTrace();
            if (!cancelado) {
                escucha.alFallar("No se pudo abrir la cámara. Revisá que esté conectada y que otro programa no la esté usando.");
            }
            return;
        }
        try {
            List<byte[]> caras = new ArrayList<>();
            int paso = 0;
            int aciertos = 0;
            long ultimaToma = 0;
            long inicioPaso = System.currentTimeMillis();
            escucha.alCambiarPaso(0, instruccion(secuencia[0]));
            while (!cancelado) {
                Frame cuadro = camara.grab();
                if (cuadro == null || cuadro.image == null) {
                    Thread.sleep(PAUSA_MS);
                    continue;
                }
                Mat color = aMat.convert(cuadro);
                if (color == null || color.empty()) {
                    Thread.sleep(PAUSA_MS);
                    continue;
                }
                // Se voltea como un espejo, así "mi izquierda" se ve a la izquierda.
                Mat espejo = new Mat();
                flip(color, espejo, 1);
                Mat gris = reconocedor.aGris(espejo);
                ReconocedorFacial.Deteccion d = reconocedor.detectarPose(gris);
                ReconocedorFacial.Pose pedida = secuencia[paso];
                long ahora = System.currentTimeMillis();

                if (ahora - inicioPaso > TIEMPO_POR_PASO_MS) {
                    escucha.alFallar("No se detectó el movimiento a tiempo. Intentá de nuevo.");
                    return;
                }
                boolean pasoListo = false;
                if (d.pose != pedida) {
                    // Se pierde el avance de confirmación y se le dice qué corregir.
                    aciertos = 0;
                    escucha.alProgresar(pista(d.pose, pedida));
                } else if (pedida == ReconocedorFacial.Pose.FRENTE) {
                    if (ahora - ultimaToma >= ESPERA_FOTOS_MS) {
                        caras.add(reconocedor.recortar(gris, d.cara));
                        ultimaToma = ahora;
                    }
                    escucha.alProgresar("Mirá al frente… foto " + caras.size() + " de " + FOTOS_FRENTE);
                    pasoListo = caras.size() >= FOTOS_FRENTE;
                } else {
                    aciertos++;
                    escucha.alProgresar("¡Bien! Mantené el giro un momento…");
                    pasoListo = aciertos >= CONFIRMAR;
                }
                if (pasoListo) {
                    paso++;
                    aciertos = 0;
                    inicioPaso = System.currentTimeMillis();
                    if (paso >= secuencia.length) {
                        if (!cancelado) {
                            escucha.alTerminar(caras);
                        }
                        return;
                    }
                    escucha.alCambiarPaso(paso, instruccion(secuencia[paso]));
                }
                Thread.sleep(PAUSA_MS);
            }
        } catch (Exception | UnsatisfiedLinkError e) {
            e.printStackTrace();
            if (!cancelado) {
                escucha.alFallar("Falló la cámara. Intentá de nuevo.");
            }
        } finally {
            // Siempre se libera la cámara.
            try {
                camara.release();
            } catch (Exception e) {
                // No importa.
            }
        }
    }

    // Texto de lo que se le pide a la persona.
    private String instruccion(ReconocedorFacial.Pose p) {
        switch (p) {
            case FRENTE: return secuencia.length == 1 ? "Mirá de frente a la cámara." : "Paso 1 de 3: mirá de frente a la cámara.";
            case IZQUIERDA: return "Paso 2 de 3: girá la cabeza hacia tu IZQUIERDA.";
            default: return "Paso 3 de 3: girá la cabeza hacia tu DERECHA.";
        }
    }

    // Ayuda según lo que se detecta.
    private static String pista(ReconocedorFacial.Pose detectada, ReconocedorFacial.Pose pedida) {
        if (detectada == ReconocedorFacial.Pose.NINGUNA) {
            return pedida == ReconocedorFacial.Pose.FRENTE ? "No veo tu rostro, mirá a la cámara con buena luz."
                    : "No te veo bien: girá un poco más, o volvé al frente.";
        }
        if (detectada == ReconocedorFacial.Pose.FRENTE) {
            return "Girá más, casi de perfil.";
        }
        return pedida == ReconocedorFacial.Pose.FRENTE ? "Volvé a mirar de frente." : "Giraste hacia el otro lado.";
    }
}
