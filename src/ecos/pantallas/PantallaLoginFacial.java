package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.Etiqueta;
import java.awt.GridBagLayout;

// Escaneo del rostro para iniciar sesión con Face ID.
// Responsable: Valeria. Esqueleto: solo muestra el título, falta armar la pantalla.
public class PantallaLoginFacial extends Pantalla {

    public PantallaLoginFacial() {
        super("login_facial", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout());
        add(Etiqueta.titulo("Iniciar sesión con Face ID", 18f, Etiqueta.Rol.TEXTO)); // Título provisional.
    }
}
