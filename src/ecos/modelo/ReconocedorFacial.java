package ecos.modelo;

import java.io.File; // Archivos del disco.
import java.io.IOException; // Error al leer o escribir archivos.
import java.io.InputStream; // Lee un archivo de adentro del programa.
import java.nio.IntBuffer; // Para llenar la lista de etiquetas.
import java.nio.file.Files; // Copia archivos.
import java.nio.file.StandardCopyOption; // Opciones al copiar.
import java.util.ArrayList; // Lista que crece.
import java.util.Base64; // Convierte bytes a texto y de vuelta (para guardar el rostro en la base de datos).
import java.util.List; // Lista.
import org.bytedeco.javacpp.DoublePointer; // Número decimal que OpenCV llena (la "confianza").
import org.bytedeco.javacpp.IntPointer; // Número entero que OpenCV llena (la etiqueta).
import org.bytedeco.opencv.opencv_core.Mat; // Una imagen en OpenCV.
import org.bytedeco.opencv.opencv_core.MatVector; // Lista de imágenes.
import org.bytedeco.opencv.opencv_core.Rect; // Un rectángulo (donde está la cara).
import org.bytedeco.opencv.opencv_core.RectVector; // Lista de rectángulos.
import org.bytedeco.opencv.opencv_core.Size; // Ancho y alto.
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer; // El algoritmo que compara rostros.
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier; // El detector de caras.
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1; // Tipo: números enteros (etiquetas).
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1; // Tipo: imagen en gris, 1 canal de 8 bits.
import static org.bytedeco.opencv.global.opencv_core.flip; // Voltea una imagen como un espejo.
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY; // Convertir de color a gris.
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor; // Función para convertir colores.
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist; // Mejora el contraste (ayuda con la luz).
import static org.bytedeco.opencv.global.opencv_imgproc.resize; // Cambia el tamaño de una imagen.

// Detecta caras y las compara usando OpenCV (JavaCV).
// Guarda cada rostro como una imagen en gris de 100x100 píxeles, convertida a texto (Base64) para la base de datos.
// Capa: Modelo. Necesita los dos archivos haarcascade en src/recursos/facial.
public class ReconocedorFacial {

    public static final int LADO = 100; // Lado en píxeles de cada rostro guardado (100x100).

    // true si hay que intercambiar izquierda y derecha en los giros (sirve para calibrar: ver LEEME, paso "Calibrar los giros").
    private static final boolean INVERTIR_PERFIL = false;

    // Hacia dónde está mirando la cabeza (en la imagen espejo que ve la persona, igual que en un espejo).
    public enum Pose { FRENTE, IZQUIERDA, DERECHA, NINGUNA }

    // Lo que se detectó en un cuadro: la pose y dónde está la cara.
    public static class Deteccion {
        public final Pose pose; // Hacia dónde mira.
        public final Rect cara; // Dónde está la cara (null si no hay).

        public Deteccion(Pose pose, Rect cara) { // Guarda los dos datos.
            this.pose = pose; // Guarda la pose.
            this.cara = cara; // Guarda el rectángulo.
        }
    }

    private final CascadeClassifier detector; // Encuentra caras de frente.
    private final CascadeClassifier detectorPerfil; // Encuentra caras de lado (de perfil).

    // Resultado de comparar: quién parece ser y qué tan seguro es (menor número = más parecido).
    public static class Resultado {
        public final Usuario usuario; // La persona más parecida.
        public final double confianza; // Distancia: 0 = idéntico; mientras más alto, menos parecido.

        public Resultado(Usuario usuario, double confianza) { // Guarda los dos datos.
            this.usuario = usuario; // Guarda la persona.
            this.confianza = confianza; // Guarda la distancia.
        }
    }

    // El reconocedor que usa todo el juego (se crea la primera vez que se pide, cargar los detectores tarda un poco).
    private static ReconocedorFacial compartido;

    // Entrega el reconocedor del juego con los detectores de src/recursos/facial. Lanza RuntimeException si no los encuentra.
    public static synchronized ReconocedorFacial compartido() {
        if (compartido == null) {
            compartido = new ReconocedorFacial(archivo("haarcascade_frontalface_default.xml"), archivo("haarcascade_profileface.xml"));
        }
        return compartido;
    }

    // Ruta de un detector. OpenCV necesita un archivo real, así que si el programa corre desde un .jar, se copia a una
    // carpeta temporal.
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

    public ReconocedorFacial(String rutaFrente, String rutaPerfil) { // Recibe las rutas de los dos detectores (de frente y de perfil).
        detector = new CascadeClassifier(rutaFrente); // Carga el detector de caras de frente.
        if (detector.empty()) { // Si no pudo cargarlo.
            throw new RuntimeException("No se pudo cargar el detector de caras: " + rutaFrente); // Avisa cuál archivo falta.
        }
        detectorPerfil = new CascadeClassifier(rutaPerfil); // Carga el detector de caras de perfil.
        if (detectorPerfil.empty()) { // Si no pudo cargarlo.
            throw new RuntimeException("No se pudo cargar el detector de perfil: " + rutaPerfil); // Avisa cuál archivo falta.
        }
    }

    // Convierte la imagen de la cámara (color) a gris y mejora el contraste.
    public Mat aGris(Mat color) { // Recibe la imagen en color.
        Mat gris = new Mat(); // Aquí queda la imagen en gris.
        cvtColor(color, gris, COLOR_BGR2GRAY); // Pasa de color a gris.
        equalizeHist(gris, gris); // Mejora el contraste.
        return gris; // La entrega.
    }

    // Busca la cara de frente más grande de la imagen. Entrega null si no hay ninguna.
    public Rect buscarCara(Mat gris) { // Recibe la imagen en gris.
        return buscarMayor(detector, gris); // Usa el detector de frente.
    }

    // Dice hacia dónde está mirando la cabeza: de frente, girada a un lado o al otro, o no se detecta.
    // Se prueba primero de frente; si no, de perfil, y de perfil también en la imagen volteada (el detector solo conoce un lado).
    public Deteccion detectarPose(Mat gris) { // Recibe la imagen en gris.
        Rect frente = buscarCara(gris); // Busca una cara de frente.
        if (frente != null) { // Si la encontró.
            return new Deteccion(Pose.FRENTE, frente); // La cabeza está de frente.
        }
        Rect ladoA = buscarMayor(detectorPerfil, gris); // Busca un perfil (el lado que conoce el detector).
        if (ladoA != null) { // Si lo encontró.
            return new Deteccion(INVERTIR_PERFIL ? Pose.IZQUIERDA : Pose.DERECHA, ladoA); // Es el lado A.
        }
        Mat espejo = new Mat(); // Imagen volteada.
        flip(gris, espejo, 1); // La voltea de izquierda a derecha.
        Rect volteada = buscarMayor(detectorPerfil, espejo); // Busca el perfil del otro lado.
        if (volteada != null) { // Si lo encontró.
            Rect real = new Rect(gris.cols() - volteada.x() - volteada.width(), volteada.y(), volteada.width(), volteada.height()); // Pasa el rectángulo a la imagen original.
            return new Deteccion(INVERTIR_PERFIL ? Pose.DERECHA : Pose.IZQUIERDA, real); // Es el lado B.
        }
        return new Deteccion(Pose.NINGUNA, null); // No se detectó cara.
    }

    // Busca con un detector la cara más grande. Entrega null si no hay ninguna.
    private Rect buscarMayor(CascadeClassifier cascada, Mat gris) { // Recibe el detector y la imagen.
        RectVector caras = new RectVector(); // Aquí se guardan las caras encontradas.
        cascada.detectMultiScale(gris, caras); // Busca caras.
        if (caras.size() == 0) { // Si no encontró ninguna.
            return null; // No hay cara.
        }
        Rect mayor = caras.get(0); // Empieza suponiendo que la primera es la más grande.
        for (long i = 1; i < caras.size(); i++) { // Revisa las demás.
            Rect r = caras.get(i); // Una cara.
            if (r.width() * r.height() > mayor.width() * mayor.height()) { // Si es más grande.
                mayor = r; // Ahora esa es la mayor.
            }
        }
        return new Rect(mayor.x(), mayor.y(), mayor.width(), mayor.height()); // Entrega una copia del rectángulo.
    }

    // Recorta la cara de la imagen y la deja en 100x100. Entrega sus bytes (10 000 números de 0 a 255).
    public byte[] recortar(Mat gris, Rect cara) { // Recibe la imagen en gris y dónde está la cara.
        Mat recorte = new Mat(gris, cara); // Se queda solo con la cara.
        Mat chica = new Mat(); // Aquí queda la cara en 100x100.
        resize(recorte, chica, new Size(LADO, LADO)); // Cambia el tamaño.
        byte[] bytes = new byte[LADO * LADO]; // Un byte por píxel.
        chica.data().get(bytes); // Copia los píxeles a la lista de bytes.
        return bytes; // Los entrega.
    }

    // Convierte los bytes de una cara de vuelta a una imagen de OpenCV.
    private static Mat aMat(byte[] bytes) { // Recibe los 10 000 bytes.
        Mat m = new Mat(LADO, LADO, CV_8UC1); // Imagen vacía en gris de 100x100.
        m.data().put(bytes); // Le pone los píxeles.
        return m; // La entrega.
    }

    // Convierte varias caras en un solo texto para guardar en la columna "rostro" (Base64 separado por comas).
    public static String codificar(List<byte[]> caras) { // Recibe las caras capturadas.
        StringBuilder sb = new StringBuilder(); // Aquí se arma el texto.
        for (byte[] cara : caras) { // Por cada cara.
            if (sb.length() > 0) { // Si no es la primera.
                sb.append(","); // Pone una coma de separación.
            }
            sb.append(Base64.getEncoder().encodeToString(cara)); // La convierte a texto.
        }
        return sb.toString(); // Entrega el texto completo.
    }

    // Hace lo contrario: del texto de la base de datos saca la lista de caras.
    public static List<byte[]> decodificar(String texto) { // Recibe el texto de la columna "rostro".
        List<byte[]> caras = new ArrayList<>(); // Aquí quedan las caras.
        if (texto == null || texto.isEmpty()) { // Si no hay rostro guardado.
            return caras; // Lista vacía.
        }
        for (String parte : texto.split(",")) { // Una parte por cada cara.
            byte[] bytes = Base64.getDecoder().decode(parte); // Texto a bytes.
            if (bytes.length == LADO * LADO) { // Solo si tiene el tamaño correcto.
                caras.add(bytes); // La agrega.
            }
        }
        return caras; // Entrega la lista.
    }

    // Compara las caras capturadas contra todos los usuarios registrados. Entrega el más parecido (o null si no hay a quién comparar).
    public Resultado identificar(List<Usuario> usuarios, List<byte[]> capturadas) { // Recibe los usuarios con rostro y las caras nuevas.
        MatVector imagenes = new MatVector(); // Todas las caras guardadas.
        List<Integer> dueno = new ArrayList<>(); // De quién es cada imagen (posición en la lista de usuarios).
        for (int i = 0; i < usuarios.size(); i++) { // Por cada usuario.
            for (byte[] cara : decodificar(usuarios.get(i).getRostro())) { // Por cada cara que guardó.
                imagenes.push_back(aMat(cara)); // Agrega la imagen.
                dueno.add(i); // Anota de quién es.
            }
        }
        if (dueno.isEmpty()) { // Si ninguna cara se pudo leer.
            return null; // No hay a quién comparar.
        }
        Mat etiquetas = new Mat(dueno.size(), 1, CV_32SC1); // Una etiqueta (número) por imagen.
        IntBuffer lista = etiquetas.createBuffer(); // Para llenarla.
        for (int i = 0; i < dueno.size(); i++) { // Por cada imagen.
            lista.put(i, dueno.get(i)); // Le pone el número de su dueño.
        }
        LBPHFaceRecognizer reconocedor = LBPHFaceRecognizer.create(); // Crea el comparador.
        reconocedor.train(imagenes, etiquetas); // Lo "entrena" con las caras guardadas (con pocos usuarios es instantáneo).

        Usuario mejorUsuario = null; // El más parecido hasta ahora.
        double mejorConfianza = Double.MAX_VALUE; // Su distancia (menor = mejor).
        for (byte[] cara : capturadas) { // Prueba cada cara capturada.
            IntPointer etiqueta = new IntPointer(1); // Aquí OpenCV dice a quién se parece.
            DoublePointer confianza = new DoublePointer(1); // Aquí OpenCV dice qué tan lejos está.
            reconocedor.predict(aMat(cara), etiqueta, confianza); // Compara.
            if (confianza.get(0) < mejorConfianza) { // Si es el mejor resultado hasta ahora.
                mejorConfianza = confianza.get(0); // Guarda la distancia.
                mejorUsuario = usuarios.get(etiqueta.get(0)); // Guarda a quién se parece.
            }
        }
        return new Resultado(mejorUsuario, mejorConfianza); // Entrega el resultado.
    }
}
