package ecos.app.modos.Patrones;

// Clases del propio proyecto que se usan aquí.
import ecos.app.modos.JuegoC.BarraTiempo;

// Barra delgada de 2 segundos (tiempo entre casillas) del modo patrones.
// extends = hereda todo lo de BarraTiempo; esta clase solo la usa en versión delgada.
public class BarraSegmentos extends BarraTiempo {

    // Constructor que no recibe nada y avisa a BarraTiempo que sea delgada.
    public BarraSegmentos() {
        // Super = constructor de BarraTiempo; true = delgada.
        super(true);
    }
}
