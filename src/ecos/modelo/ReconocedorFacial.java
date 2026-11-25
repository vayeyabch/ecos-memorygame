package ecos.modelo;

// File: representa un archivo o ruta del disco; se usa para buscar el detector en src/recursos/facial.
import java.io.File;
// IOException: error de entrada/salida; puede fallar al copiar el detector desde el .jar.
import java.io.IOException;
// InputStream: flujo de bytes para leer un archivo (el detector dentro del .jar).
import java.io.InputStream;
// IntBuffer: espacio de enteros para llenar la lista de etiquetas que usa OpenCV.
import java.nio.IntBuffer;
// Files: copia el detector del .jar a una carpeta temporal.
import java.nio.file.Files;
// StandardCopyOption: opción de copia; REPLACE_EXISTING reemplaza si ya existe.
import java.nio.file.StandardCopyOption;
// ArrayList: lista donde se guardan los dueños de cada cara y las caras decodificadas.
import java.util.ArrayList;
// Base64: convierte bytes a texto (y de vuelta) para guardar las caras en una columna de la base.
import java.util.Base64;
// List: tipo de las listas.
import java.util.List;
// DoublePointer: "puntero" a un número decimal; OpenCV devuelve la confianza por aquí.
import org.bytedeco.javacpp.DoublePointer;
// IntPointer: "puntero" a un entero; OpenCV devuelve la etiqueta (quién parece ser) por aquí.
import org.bytedeco.javacpp.IntPointer;
// Mat: la matriz de OpenCV; así se representa una imagen.
import org.bytedeco.opencv.opencv_core.Mat;
// MatVector: lista de imágenes (Mat) para entrenar el reconocedor.
import org.bytedeco.opencv.opencv_core.MatVector;
// Rect: rectángulo que marca dónde está una cara.
import org.bytedeco.opencv.opencv_core.Rect;
// RectVector: lista de rectángulos; el detector devuelve todas las caras que encuentra.
import org.bytedeco.opencv.opencv_core.RectVector;
// Size: ancho y alto; se usa para cambiar el tamaño del recorte.
import org.bytedeco.opencv.opencv_core.Size;
// LBPHFaceRecognizer: el algoritmo que compara caras (histogramas de patrones locales).
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
// CascadeClassifier: el detector de caras (clasificador Haar) que lee los archivos haarcascade.
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
// CV_32SC1: tipo de matriz de enteros de 32 bits; se usa para las etiquetas.
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
// CV_8UC1: tipo de matriz de 1 canal de 8 bits (imagen en gris).
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
// flip: voltea la imagen como en un espejo; sirve para detectar el perfil del otro lado.
import static org.bytedeco.opencv.global.opencv_core.flip;
// COLOR_BGR2GRAY: indica la conversión de color (BGR) a gris.
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
// cvtColor: convierte una imagen de un formato de color a otro.
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
// equalizeHist: mejora el contraste de la imagen en gris.
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist;
// resize: cambia el tamaño de una imagen.
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

// Detecta caras y las compara con OpenCV (JavaCV). Guarda cada rostro como una imagen en gris de 100x100 píxeles
// convertida a texto Base64 para la base. Necesita los dos archivos haarcascade en src/recursos/facial.
public class ReconocedorFacial {

    // Lado del cuadrado al que se reduce cada cara (100x100).
    public static final int LADO = 100;

    // true intercambia izquierda y derecha en los giros, por si la cámara gira al revés.
    private static final boolean INVERTIR_PERFIL = false;

    // Hacia dónde mira la cabeza, como en un espejo.
    public enum Pose { FRENTE, IZQUIERDA, DERECHA, NINGUNA }

    // Resultado de detectar: la pose y dónde está la cara. Los campos son final y public: solo se leen.
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

    // Devuelve siempre el mismo reconocedor (patrón Singleton). synchronized: dos hilos no lo crean a la vez.
    public static synchronized ReconocedorFacial compartido() {
        if (compartido == null) {
            compartido = new ReconocedorFacial(archivo("haarcascade_frontalface_default.xml"), archivo("haarcascade_profileface.xml"));
        }
        return compartido;
    }

    // Ruta de un detector. OpenCV necesita un archivo real, así que desde un .jar se copia a una carpeta temporal.
    private static String archivo(String nombre) {
        // Primero busca en el disco (cuando se corre desde VS Code).
        File enDisco = new File("src/recursos/facial/" + nombre);
        if (enDisco.exists()) {
            return enDisco.getPath();
        }
        // Si no, lo busca dentro del .jar. try-with-resources cierra el flujo solo.
        try (InputStream in = ReconocedorFacial.class.getResourceAsStream("/recursos/facial/" + nombre)) {
            if (in == null) {
                throw new RuntimeException("Falta el archivo " + nombre + " en src/recursos/facial");
            }
            // Crea un archivo temporal que se borra al cerrar el programa y copia el detector ahí.
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
        // empty() es true si el archivo no se pudo leer.
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
        // 1) De frente.
        Rect frente = buscarCara(gris);
        if (frente != null) {
            return new Deteccion(Pose.FRENTE, frente);
        }
        // 2) De perfil, en la imagen tal cual.
        Rect ladoA = buscarMayor(detectorPerfil, gris);
        if (ladoA != null) {
            return new Deteccion(INVERTIR_PERFIL ? Pose.IZQUIERDA : Pose.DERECHA, ladoA);
        }
        // 3) De perfil, en la imagen volteada (el otro lado).
        Mat espejo = new Mat();
        flip(gris, espejo, 1);
        Rect volteada = buscarMayor(detectorPerfil, espejo);
        if (volteada != null) {
            // Se calcula dónde estaba el rectángulo en la imagen sin voltear.
            Rect real = new Rect(gris.cols() - volteada.x() - volteada.width(), volteada.y(), volteada.width(), volteada.height());
            return new Deteccion(INVERTIR_PERFIL ? Pose.DERECHA : Pose.IZQUIERDA, real);
        }
        // 4) No encontró ninguna cara.
        return new Deteccion(Pose.NINGUNA, null);
    }

    // Busca con un detector la cara más grande. Entrega null si no hay ninguna.
    private Rect buscarMayor(CascadeClassifier cascada, Mat gris) {
        RectVector caras = new RectVector();
        // Llena "caras" con todos los rectángulos que encuentra.
        cascada.detectMultiScale(gris, caras);
        if (caras.size() == 0) {
            return null;
        }
        // Se queda con la de mayor área (ancho x alto).
        Rect mayor = caras.get(0);
        for (long i = 1; i < caras.size(); i++) {
            Rect r = caras.get(i);
            if (r.width() * r.height() > mayor.width() * mayor.height()) {
                mayor = r;
            }
        }
        // Devuelve una copia, porque el original pertenece al vector de OpenCV.
        return new Rect(mayor.x(), mayor.y(), mayor.width(), mayor.height());
    }

    // Recorta la cara, la deja en 100x100 y entrega sus bytes.
    public byte[] recortar(Mat gris, Rect cara) {
        // Mat(gris, cara) es la parte de la imagen que está dentro del rectángulo.
        Mat recorte = new Mat(gris, cara);
        Mat chica = new Mat();
        resize(recorte, chica, new Size(LADO, LADO));
        // 100x100 píxeles, un byte por píxel.
        byte[] bytes = new byte[LADO * LADO];
        chica.data().get(bytes);
        return bytes;
    }

    // Hace lo contrario de recortar: de los bytes arma una imagen (Mat) de 100x100 en gris.
    private static Mat aMat(byte[] bytes) {
        Mat m = new Mat(LADO, LADO, CV_8UC1);
        m.data().put(bytes);
        return m;
    }

    // Junta varias caras en un texto Base64 separado por comas para la columna rostro.
    public static String codificar(List<byte[]> caras) {
        StringBuilder sb = new StringBuilder();
        for (byte[] cara : caras) {
            // Pone una coma antes de cada cara, menos la primera.
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
        // Si no hay rostro guardado, devuelve la lista vacía.
        if (texto == null || texto.isEmpty()) {
            return caras;
        }
        for (String parte : texto.split(",")) {
            byte[] bytes = Base64.getDecoder().decode(parte);
            // Solo acepta caras con el tamaño correcto; ignora datos dañados.
            if (bytes.length == LADO * LADO) {
                caras.add(bytes);
            }
        }
        return caras;
    }

    // Compara las caras capturadas con todos los usuarios y entrega el más parecido, o null si no hay a quién comparar.
    public Resultado identificar(List<Usuario> usuarios, List<byte[]> capturadas) {
        MatVector imagenes = new MatVector();
        // dueno: para cada imagen, la posición del usuario al que pertenece en la lista.
        List<Integer> dueno = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            for (byte[] cara : decodificar(usuarios.get(i).getRostro())) {
                imagenes.push_back(aMat(cara));
                dueno.add(i);
            }
        }
        // Si ninguna cuenta tiene rostro guardado, no hay con quién comparar.
        if (dueno.isEmpty()) {
            return null;
        }
        // Matriz de etiquetas: una por imagen, con el número del dueño.
        Mat etiquetas = new Mat(dueno.size(), 1, CV_32SC1);
        IntBuffer lista = etiquetas.createBuffer();
        for (int i = 0; i < dueno.size(); i++) {
            lista.put(i, dueno.get(i));
        }
        LBPHFaceRecognizer reconocedor = LBPHFaceRecognizer.create();
        // Lo entrena con las caras guardadas.
        reconocedor.train(imagenes, etiquetas);

        // Para cada cara capturada se pregunta a quién se parece y se guarda la mejor (menor distancia).
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