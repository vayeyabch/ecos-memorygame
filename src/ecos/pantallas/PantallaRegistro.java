package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.Etiqueta;
import java.awt.GridBagLayout;

// Formulario de registro: nombre, apellidos, correo, usuario y contraseña. Después lleva al registro facial.
// Responsable: Valeria. Esqueleto: solo muestra el título, falta armar la pantalla.
public class PantallaRegistro extends Pantalla {

    public PantallaRegistro() {
        super("registro", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout());
        add(Etiqueta.titulo("Registro", 18f, Etiqueta.Rol.TEXTO)); // Título provisional.
    }
}
