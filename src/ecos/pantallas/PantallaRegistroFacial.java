package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.Etiqueta;
import java.awt.GridBagLayout;

// Captura del rostro para terminar el registro.
// Responsable: Valeria. Esqueleto: solo muestra el título, falta armar la pantalla.
public class PantallaRegistroFacial extends Pantalla {

    public PantallaRegistroFacial() {
        super("registro_facial", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout());
        add(Etiqueta.titulo("Registro facial", 18f, Etiqueta.Rol.TEXTO)); // Título provisional.
    }
}
