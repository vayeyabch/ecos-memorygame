package ecos;

import ecos.app.Navegador; // Para registrar las pantallas.
import ecos.app.Ventana; // La ventana principal.
import ecos.componentes.BarraSuperior; // La barra de arriba (atrás, idioma, tema, música).
import ecos.componentes.BotonAcercaDe; // El botón "i" de la esquina inferior.
import ecos.app.modos.JuegoC.ControladorJuegoClasico;
import ecos.app.modos.Patrones.ControladorPatron;
import ecos.controlador.ControladorAcceso;
import ecos.controlador.ControladorInicio;
import ecos.controlador.ControladorIntro;
import ecos.controlador.ControladorLoginFacial;
import ecos.controlador.ControladorMenu;
import ecos.controlador.ControladorPremios;
import ecos.controlador.ControladorRegistro;
import ecos.controlador.ControladorRegistroFacial;
import ecos.controlador.ControladorReglasClasico;
import ecos.controlador.ControladorReglasPatrones;
import ecos.pantallas.PantallaClasico;
import ecos.pantallas.PantallaLoginFacial;
import ecos.pantallas.PantallaMenu;
import ecos.pantallas.PantallaPatron;
import ecos.pantallas.PantallaPremios;
import ecos.pantallas.PantallaRegistro;
import ecos.pantallas.PantallaRegistroFacial;
import ecos.pantallas.PantallaReglasClasico;
import ecos.pantallas.PantallaReglasPatrones;
import ecos.pantallas.pantallaAcceso;
import ecos.pantallas.pantallaInicio;
import ecos.pantallas.pantallaIntro;
import ecos.recursos.Sprites;
import javax.swing.SwingUtilities;

public class Main {

    public static Ventana crear() {
        Ventana ventana = new Ventana(); // La ventana principal.
        ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador())); // La barra de arriba.
        ventana.setEsquinaInferior(new BotonAcercaDe(ventana.getNavegador())); // El botón "i" abajo a la derecha.
        Navegador n = ventana.getNavegador(); // El navegador, para registrar las pantallas.

        // Pantallas (la vista de cada una).
        pantallaIntro intro = new pantallaIntro();
        pantallaInicio inicio = new pantallaInicio();
        pantallaAcceso acceso = new pantallaAcceso();
        PantallaRegistro registro = new PantallaRegistro();
        PantallaRegistroFacial registroFacial = new PantallaRegistroFacial();
        PantallaLoginFacial loginFacial = new PantallaLoginFacial();
        PantallaMenu menu = new PantallaMenu();
        PantallaReglasClasico reglasClasico = new PantallaReglasClasico();
        PantallaReglasPatrones reglasPatrones = new PantallaReglasPatrones();
        PantallaPremios premios = new PantallaPremios();
        PantallaClasico clasico = new PantallaClasico();
        PantallaPatron patrones = new PantallaPatron();
        n.registrar(intro);
        n.registrar(inicio);
        n.registrar(acceso);
        n.registrar(registro);
        n.registrar(registroFacial);
        n.registrar(loginFacial);
        n.registrar(menu);
        n.registrar(reglasClasico);
        n.registrar(reglasPatrones);
        n.registrar(premios);
        n.registrar(clasico);
        n.registrar(patrones);

        // Controladores (uno por pantalla). Los nuevos todavía no tienen lógica.
        new ControladorIntro(intro, n::ir);
        new ControladorInicio(inicio, n::ir);
        new ControladorAcceso(acceso);
        new ControladorRegistro(registro);
        new ControladorRegistroFacial(registroFacial);
        new ControladorLoginFacial(loginFacial);
        new ControladorMenu(menu, n::ir);
        new ControladorReglasClasico(reglasClasico, n::ir);
        new ControladorReglasPatrones(reglasPatrones, n::ir);
        new ControladorPremios(premios);
        new ControladorJuegoClasico(clasico, n::ir);
        new ControladorPatron(patrones, n::ir);
        return ventana;
    }

    public static void main(String[] args) {
        Sprites.precargar(); // Carga las imágenes antes de abrir la ventana.
        SwingUtilities.invokeLater(() -> crear().iniciar("intro")); // Abre la ventana en la intro.
    }
}
