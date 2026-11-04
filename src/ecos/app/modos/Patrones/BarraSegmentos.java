package ecos.app.modos.Patrones;

import ecos.app.modos.JuegoC.BarraTiempo;

// Barra delgada de 2 segundos (tiempo entre casillas) del modo patrones.
// extends = hereda todo lo de BarraTiempo; esta clase solo la usa en versión delgada.
public class BarraSegmentos extends BarraTiempo {

    public BarraSegmentos() {   // Constructor: no recibe nada y avisa a BarraTiempo que sea delgada.
        super(true); // super = constructor de BarraTiempo; true = delgada
    }
}
