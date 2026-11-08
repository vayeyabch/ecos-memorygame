package ecos.modelo;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Sesion;

// Modelo de PantallaMenu, guarda el nombre de quien juega y el tamaño de tablero elegido (4 o 6).
// A cargo de Hellen.
public class ModeloMenu {

    // Nombre de quien inició sesión.
    public String getNombre() {
        // Lo pide a Sesion.
        return Sesion.getNombre();
    }

    // Tamaño del tablero elegido, 4 (4x4) o 6 (6x6).
    public int getTamano() {
        // Lo pide a Sesion.
        return Sesion.getTamano();
    }

    // Guarda el tamaño elegido; solo acepta 4 o 6.
    public void elegirTamano(int n) {
        // Ignora cualquier otro número. Lo guarda para que el modo clásico lo use.
        if (n == 4 || n == 6) {
            Sesion.setTamano(n);
        }
    }

    // Texto del saludo de la pantalla.
    public String saludo() {
        // Une el saludo con el nombre.
        return "Hola, " + getNombre() + "!";
    }
}
