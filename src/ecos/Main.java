package ecos;

import ecos.app.modos.JuegoC.ControladorJuegoClasico;
import ecos.app.modos.JuegoC.JuegoClasico;

public class Main {
    public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() ->
        new ControladorJuegoClasico(new JuegoClasico()).iniciar());
}
}