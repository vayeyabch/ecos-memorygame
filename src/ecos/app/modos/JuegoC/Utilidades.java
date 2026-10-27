package ecos.app.modos.JuegoC;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
 
/**
 * Métodos de ayuda que se pueden usar desde cualquier clase.
 */
public class Utilidades {

    private static final Logger LOGGER = Logger.getLogger(Utilidades.class.getName());
 
    public static Font cargarFuentePixel(float tamano) {
        try {
            File archivoFuente = new File("src/recursos/fuentes/PixelifySans.ttf");
            Font fuentePersonalizada = Font.createFont(Font.TRUETYPE_FONT, archivoFuente);
            return fuentePersonalizada.deriveFont(tamano);
        } catch (FontFormatException | IOException e) {
            LOGGER.log(Level.WARNING, "No se pudo cargar la fuente personalizada.", e);
            return new Font("Arial", Font.BOLD, (int) tamano);
        }
    }
 
    public static ImageIcon cargarIconoRedimensionado(String ruta, int ancho, int alto) {
        try {
            ImageIcon iconOriginal = new ImageIcon(ruta);
            if (iconOriginal.getIconWidth() <= 0) {
                return null;
            }
            Image imagenEscalada = iconOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } catch (Exception e) {
            return null;
        }
    }
}
 