package ecos;

import ecos.app.Ventana;
import ecos.app.modos.JuegoC.ControladorJuegoClasico;
import ecos.app.modos.JuegoC.JuegoClasico;
import ecos.componentes.BarraSuperior;
import ecos.pantallas.PantallaMenu;
import ecos.pantallas.PantallaPremios;
import ecos.pantallas.PantallaReglasClasico;
import ecos.pantallas.PantallaReglasPatrones;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() ->
            new ControladorJuegoClasico(new JuegoClasico()).iniciar());

        Ventana ventana = new Ventana();
        ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador()));
        ventana.getNavegador().registrar(new PantallaMenu());
        ventana.getNavegador().registrar(new PantallaReglasClasico());
        ventana.getNavegador().registrar(new PantallaReglasPatrones());
        ventana.getNavegador().registrar(new PantallaPremios());
        javax.swing.SwingUtilities.invokeLater(() -> ventana.iniciar("menu"));
    }
}
