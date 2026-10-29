package ecos.recursos;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga las dos tipografias pixel art una sola vez.
 *   titulo(16f) -> Press Start 2P (titulos y botones; usar tamanos 8, 16, 24...)
 *   texto(18f)  -> Pixelify Sans (texto normal)
 */
public class Fuentes {

    private static final Map<String, Font> BASE = new HashMap<>();

    public static Font titulo(float tamano) {
        return obtener("fuentes/PressStart2P-Regular.ttf", tamano);
    }

    public static Font texto(float tamano) {
        return obtener("fuentes/PixelifySans.ttf", tamano);
    }

    private static Font obtener(String ruta, float tamano) {
        Font base = BASE.get(ruta);
        if (base == null) {
            try (InputStream in = Recursos.abrir(ruta)) {
                base = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage());
                base = new Font(Font.MONOSPACED, Font.BOLD, 12);
            }
            BASE.put(ruta, base);
        }
        return base.deriveFont(tamano);
    }
}
