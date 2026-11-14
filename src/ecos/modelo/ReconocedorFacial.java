package ecos.modelo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.flip;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

// Detecta caras y las compara con OpenCV (JavaCV). Guarda cada rostro como una imagen en gris de 100x100 píxeles
// convertida a texto Base64 para la base. Necesita los dos archivos haarcascade en src/recursos/facial.
public class ReconocedorFacial {

    public static final int LADO = 100;

    // true intercambia izquierda y derecha en los giros, por si la cámara gira al revés.
    private static final boolean INVERTIR_PERFIL = false;

    // Hacia dónde mira la cabeza, como en un espejo.
    public enum Pose { FRENTE, IZQUIERDA, DERECHA, NINGUNA }

    public static class Deteccion {
        public final Pose pose;
        // Dónde está la cara (null si no hay).
        public final Rect cara;

        public Deteccion(Pose pose, Rect cara) {
            this.pose = pose;
            this.cara = cara;
        }
    }

    // Detector de caras de frente.
    private final CascadeClassifier detector;
    // Detector de caras de perfil.
    private final CascadeClassifier detectorPerfil;

    // Resultado de comparar: quién parece ser y qué tan seguro es.
    public static class Resultado {
        public final Usuario usuario;
        // Distancia: 0 es idéntico, mientras más alto menos parecido.
        public final double confianza;

        public Resultado(Usuario usuario, double confianza) {
            this.usuario = usuario;
            this.confianza = confianza;
        }
    }

    // Reconocedor compartido; se crea la primera vez porque cargar los detectores tarda.
    private static ReconocedorFacial compartido;

    public static synchronized ReconocedorFacial compartido() {
        if (compartido == null) {
            compartido = new ReconocedorFacial(archivo("haarcascade_frontalface_default.xml"), archivo("haarcascade_profileface.xml"));
        }
        return compartido;
    }

    // Ruta de un detector. OpenCV necesita un archivo real, así que desde un .jar se copia a una carpeta temporal.
    private static String archivo(String nombre) {
        File enDisco = new File("src/recursos/facial/" + nombre);
        if (enDisco.exists()) {
            return enDisco.getPath();
        }
        try (InputStream in = ReconocedorFacial.class.getResourceAsStream("/recursos/facial/" + nombre)) {
            if (in == null) {
                throw new RuntimeException("Falta el archivo " + nombre + " en src/recursos/facial");
            }
            File temporal = File.createTempFile("ecos_", "_" + nombre);
            temporal.deleteOnExit();
            Files.copy(in, temporal.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return temporal.getPath();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo preparar " + nombre, e);
        }
    }

    // Carga los detectores de frente y de perfil, con las rutas de sus archivos.
    public ReconocedorFacial(String rutaFrente, String rutaPerfil) {
        detector = new CascadeClassifier(rutaFrente);
        if (detector.empty()) {
            throw new RuntimeException("No se pudo cargar el detector de caras: " + rutaFrente);
        }
        detectorPerfil = new CascadeClassifier(rutaPerfil);
        if (detectorPerfil.empty()) {
            throw new RuntimeException("No se pudo cargar el detector de perfil: " + rutaPerfil);
        }
    }

    // Convierte la imagen de la cámara (color) a gris y mejora el contraste.
    public Mat aGris(Mat color) {
        Mat gris = new Mat();
        cvtColor(color, gris, COLOR_BGR2GRAY);
        equalizeHist(gris, gris);
        return gris;
    }

    // Busca la cara de frente más grande de la imagen. Entrega null si no hay ninguna.
    public Rect buscarCara(Mat gris) {
        return buscarMayor(detector, gris);
    }

    // Dice hacia dónde mira la cabeza. Prueba de frente y, si no, de perfil, también en la imagen volteada porque el
    // detector solo conoce un lado.
    public Deteccion detectarPose(Mat gris) {
        Rect frente = buscarCara(gris);
        if (frente != null) {
            return new Deteccion(Pose.FRENTE, frente);
        }
        Rect ladoA = buscarMayor(detectorPerfil, gris);
        if (ladoA != null) {
            return new Deteccion(INVERTIR_PERFIL ? Pose.IZQUIERDA : Pose.DERECHA, ladoA);
        }
        Mat espejo = new Mat();
        flip(gris, espejo, 1);
        Rect volteada = buscarMayor(detectorPerfil, espejo);
        if (volteada != null) {
            Rect real = new Rect(gris.cols() - volteada.x() - volteada.width(), volteada.y(), volteada.width(), volteada.height());
            return new Deteccion(INVERTIR_PERFIL ? Pose.DERECHA : Pose.IZQUIERDA, real);
        }
        return new Deteccion(Pose.NINGUNA, null);
    }

    // Busca con un detector la cara más grande. Entrega null si no hay ninguna.
    private Rect buscarMayor(CascadeClassifier cascada, Mat gris) {
        RectVector caras = new RectVector();
        cascada.detectMultiScale(gris, caras);
        if (caras.size() == 0) {
            return null;
        }
        Rect mayor = caras.get(0);
        for (long i = 1; i < caras.size(); i++) {
            Rect r = caras.get(i);
            if (r.width() * r.height() > mayor.width() * mayor.height()) {
                mayor = r;
            }
        }
        return new Rect(mayor.x(), mayor.y(), mayor.width(), mayor.height());
    }

    // Recorta la cara, la deja en 100x100 y entrega sus bytes.
    public byte[] recortar(Mat gris, Rect cara) {
        Mat recorte = new Mat(gris, cara);
        Mat chica = new Mat();
        resize(recorte, chica, new Size(LADO, LADO));
        byte[] bytes = new byte[LADO * LADO];
        chica.data().get(bytes);
        return bytes;
    }

    private static Mat aMat(byte[] bytes) {
        Mat m = new Mat(LADO, LADO, CV_8UC1);
        m.data().put(bytes);
        return m;
    }

    // Junta varias caras en un texto Base64 separado por comas para la columna rostro.
    public static String codificar(List<byte[]> caras) {
        StringBuilder sb = new StringBuilder();
        for (byte[] cara : caras) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(Base64.getEncoder().encodeToString(cara));
        }
        return sb.toString();
    }

    // Hace lo contrario: del texto saca la lista de caras.
    public static List<byte[]> decodificar(String texto) {
        List<byte[]> caras = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            return caras;
        }
        for (String parte : texto.split(",")) {
            byte[] bytes = Base64.getDecoder().decode(parte);
            if (bytes.length == LADO * LADO) {
                caras.add(bytes);
            }
        }
        return caras;
    }

    // Compara las caras capturadas con todos los usuarios y entrega el más parecido, o null si no hay a quién comparar.
    public Resultado identificar(List<Usuario> usuarios, List<byte[]> capturadas) {
        MatVector imagenes = new MatVector();
        List<Integer> dueno = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            for (byte[] cara : decodificar(usuarios.get(i).getRostro())) {
                imagenes.push_back(aMat(cara));
                dueno.add(i);
            }
        }
        if (dueno.isEmpty()) {
            return null;
        }
        Mat etiquetas = new Mat(dueno.size(), 1, CV_32SC1);
        IntBuffer lista = etiquetas.createBuffer();
        for (int i = 0; i < dueno.size(); i++) {
            lista.put(i, dueno.get(i));
        }
        LBPHFaceRecognizer reconocedor = LBPHFaceRecognizer.create();
        // Lo entrena con las caras guardadas.
        reconocedor.train(imagenes, etiquetas);

        Usuario mejorUsuario = null;
        double mejorConfianza = Double.MAX_VALUE;
        for (byte[] cara : capturadas) {
            IntPointer etiqueta = new IntPointer(1);
            DoublePointer confianza = new DoublePointer(1);
            reconocedor.predict(aMat(cara), etiqueta, confianza);
            if (confianza.get(0) < mejorConfianza) {
                mejorConfianza = confianza.get(0);
                mejorUsuario = usuarios.get(etiqueta.get(0));
            }
        }
        return new Resultado(mejorUsuario, mejorConfianza);
    }
}
