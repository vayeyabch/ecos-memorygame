package ecos;

import ecos.app.Navegador;
import ecos.app.Ventana;
import ecos.app.modos.JuegoC.ControladorJuegoClasico;
import ecos.app.modos.JuegoC.JuegoClasico;
import ecos.componentes.BarraSuperior;
import ecos.pantallas.PantallaMenu;
import ecos.pantallas.PantallaPremios;
import ecos.pantallas.PantallaReglasClasico;
import ecos.pantallas.PantallaReglasPatrones;
import ecos.pantallas.pantallaAcceso;
import ecos.pantallas.pantallaInicio;
import ecos.pantallas.pantallaIntro;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ventana ventana = new Ventana();
            ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador()));
            Navegador n = ventana.getNavegador();

            n.registrar(new pantallaIntro());
            n.registrar(new pantallaInicio());
            n.registrar(new pantallaAcceso());
            n.registrar(new PantallaMenu());
            n.registrar(new PantallaReglasClasico());
            n.registrar(new PantallaReglasPatrones());
            n.registrar(new PantallaPremios());

            JuegoClasico vista = new JuegoClasico();
            new ControladorJuegoClasico(vista);        // el controlador que tengan (temporal o real)

            ventana.iniciar("intro");
        });
    }
}