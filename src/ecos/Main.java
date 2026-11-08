package ecos;

// Clases del propio proyecto que se usan aquí.
import ecos.app.Navegador;
import ecos.app.Ventana;
import ecos.componentes.BarraSuperior;
import ecos.componentes.BotonAcercaDe;
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
import ecos.pantallas.pantallaAcceso;
import ecos.pantallas.pantallaInicio;
import ecos.pantallas.pantallaIntro;
import ecos.recursos.Sprites;
// Librería Swing, SwingUtilities para ejecutar cosas en el hilo de Swing.
import javax.swing.SwingUtilities;

// Clase principal, arma la ventana, crea las pantallas, las registra en el navegador y les conecta su controlador.
public class Main {

    // Crea la ventana con su barra de arriba y el botón "i" de la esquina.
    public static Ventana crear() {
        // La ventana principal, la barra de arriba, el botón "i" de abajo a la derecha y el navegador donde se registran las
        // pantallas.
        Ventana ventana = new Ventana();
        ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador()));
        ventana.setEsquinaInferior(new BotonAcercaDe(ventana.getNavegador()));
        Navegador n = ventana.getNavegador();

        // Pantallas (la vista de cada una).
        // Se crea la vista de cada pantalla.
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
        // Se registran todas las pantallas para poder navegar entre ellas por su nombre.
        // Cada pantalla se registra en el navegador con su nombre.
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
        // Cada controlador se conecta con su vista; los que necesitan cambiar de pantalla reciben el navegador.
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

    // Arranca el programa, carga las imágenes, pide el dólar del día y abre la ventana en la intro.
    public static void main(String[] args) {
        // Carga las imágenes antes de abrir la ventana. Pide el dólar del día al abrir (en segundo plano, no atrasa el arranque).
        Sprites.precargar();
        Thread dolar = new Thread(ecos.modelo.TipoCambio::actualizarSiHaceFalta, "dolar-inicio");
        dolar.setDaemon(true);
        dolar.start();
        // Abre la ventana en la intro.
        SwingUtilities.invokeLater(() -> crear().iniciar("intro"));
    }
}
