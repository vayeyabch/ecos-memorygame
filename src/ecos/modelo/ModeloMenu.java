package ecos.modelo;

import ecos.app.Sesion;

// Modelo de PantallaMenu, guarda el nombre de quien juega y el tamaño de tablero elegido (4 o 6).
public class ModeloMenu {

    public String getNombre() {
        return Sesion.getNombre();
    }

    public int getTamano() {
        return Sesion.getTamano();
    }

    // Guarda el tamaño elegido (solo 4 o 6) para que lo use el modo clásico.
    public void elegirTamano(int n) {
        if (n == 4 || n == 6) {
            Sesion.setTamano(n);
        }
    }

    public String saludo() {
        return "Hola, " + getNombre() + "!";
    }
}
