package ecos.modelo;

import ecos.app.Sesion; // Aquí viven por ahora el nombre y el tablero elegido.

// Modelo de PantallaMenu: guarda el nombre de quien juega y el tamaño de tablero elegido (4 o 6).
// Responsable: Hellen.
public class ModeloMenu {

    public String getNombre() { // Nombre de quien inició sesión.
        return Sesion.getNombre(); // Lo pide a Sesion.
    }

    public int getTamano() { // Tamaño del tablero elegido: 4 (4x4) o 6 (6x6).
        return Sesion.getTamano(); // Lo pide a Sesion.
    }

    public void elegirTamano(int n) { // Guarda el tamaño elegido; solo acepta 4 o 6.
        if (n == 4 || n == 6) { // Ignora cualquier otro número.
            Sesion.setTamano(n); // Lo guarda para que el modo clásico lo use.
        }
    }

    public String saludo() { // Texto del saludo de la pantalla.
        return "Hola, " + getNombre() + "!"; // Une el saludo con el nombre.
    }
}
