package ecos;

// Los imports solo le dicen a Java en qué carpeta está cada clase. No ejecutan nada.
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
import ecos.controlador.ControladorModoClasico;
import ecos.controlador.ControladorMultijugador;
import ecos.controlador.ControladorPremios;
import ecos.controlador.ControladorRegistro;
import ecos.controlador.ControladorRegistroFacial;
import ecos.controlador.ControladorReglasClasico;
import ecos.controlador.ControladorReglasPatrones;
import ecos.pantallas.PantallaClasico;
import ecos.pantallas.PantallaLoginFacial;
import ecos.pantallas.PantallaMenu;
import ecos.pantallas.PantallaModoClasico;
import ecos.pantallas.PantallaMultijugador;
import ecos.pantallas.PantallaPatron;
import ecos.pantallas.PantallaPremios;
import ecos.pantallas.PantallaRegistro;
import ecos.pantallas.PantallaRegistroFacial;
import ecos.pantallas.PantallaReglasClasico;
import ecos.pantallas.PantallaReglasPatrones;
import ecos.pantallas.PantallaAcceso;
import ecos.pantallas.PantallaInicio;
import ecos.pantallas.PantallaIntro;
import ecos.recursos.Sprites;
import javax.swing.SwingUtilities;

// Esta clase arma todo el programa. Crea la ventana y las pantallas,
// registra las pantallas en el navegador y le conecta a cada una su controlador.
// Es la única clase que conoce a todas las demás, por eso tiene tantos imports.
public class Main {

    // Arma la aplicación completa y devuelve la ventana lista para abrirse.
    // Es static para llamarlo directo, sin crear un objeto Main.
    // El tipo Ventana antes del nombre es lo que devuelve al final.
    public static Ventana crear() {

        // Crea la ventana. Al crearse, ella misma crea su Navegador por dentro.
        Ventana ventana = new Ventana();

        // Esta línea tiene tres pasos y Java los hace de adentro hacia afuera.
        // Uno: getNavegador le pide a la ventana su Navegador. Los métodos get devuelven algo.
        // Dos: new BarraSuperior crea la barra y recibe ese Navegador para poder navegar.
        // Tres: setBarraSuperior le entrega la barra a la ventana. Los métodos set reciben algo y lo guardan.
        ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador()));

        // Igual que la anterior pero con el botón i de la esquina.
        // Es el mismo Navegador para los dos, no se crea uno nuevo.
        ventana.setEsquinaInferior(new BotonAcercaDe(ventana.getNavegador()));

        // Guarda el Navegador en una variable corta llamada n.
        // Es solo para no escribir ventana.getNavegador cada vez que lo necesite abajo.
        Navegador n = ventana.getNavegador();

        // Aquí se crea cada pantalla. Todavía no se ve ninguna,
        // solo existen en memoria. Cada línea sigue el mismo patrón.
        // Tipo, nombre de la variable, igual, new y la clase.
        PantallaIntro intro = new PantallaIntro();
        PantallaInicio inicio = new PantallaInicio();
        PantallaAcceso acceso = new PantallaAcceso();
        PantallaRegistro registro = new PantallaRegistro();
        PantallaRegistroFacial registroFacial = new PantallaRegistroFacial();
        PantallaLoginFacial loginFacial = new PantallaLoginFacial();
        PantallaMenu menu = new PantallaMenu();
        PantallaReglasClasico reglasClasico = new PantallaReglasClasico();
        PantallaReglasPatrones reglasPatrones = new PantallaReglasPatrones();
        PantallaPremios premios = new PantallaPremios();
        PantallaClasico clasico = new PantallaClasico();
        PantallaPatron patrones = new PantallaPatron();
        PantallaMultijugador multijugador = new PantallaMultijugador();
        PantallaModoClasico modoClasico = new PantallaModoClasico();

        // Ahora el Navegador guarda cada pantalla junto con su id.
        // Después, cuando alguien diga ir a menu, el Navegador busca entre las que tiene guardadas.
        // Si me olvido de registrar una, esa pantalla nunca se va a poder abrir.
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
        n.registrar(multijugador);
        n.registrar(modoClasico);

        // Ahora se crea el controlador de cada pantalla.
        // No se guardan en variables porque no hace falta. Al crearse se conectan
        // solos a su vista, y la vista se queda con las acciones de sus botones.
        //
        // Lo que va entre paréntesis son los datos que el controlador necesita para crearse.
        // El primero siempre es la vista a la que se conecta.
        // El segundo, cuando existe, es n::ir.
        // n::ir es un atajo que significa llamar al método ir del Navegador n.
        // Es lo mismo que escribir texto flecha n.ir de texto.
        // Con eso el controlador puede cambiar de pantalla sin conocer al Navegador.
        new ControladorIntro(intro, n::ir);
        new ControladorInicio(inicio, n::ir);

        // Estos reciben solo la vista porque su pantalla no cambia a otra por su cuenta.
        new ControladorAcceso(acceso);
        new ControladorRegistro(registro);
        new ControladorRegistroFacial(registroFacial);
        new ControladorLoginFacial(loginFacial);

        // Estos sí llevan n::ir porque sus pantallas tienen botones que llevan a otras.
        new ControladorMenu(menu, n::ir);
        new ControladorReglasClasico(reglasClasico, n::ir);
        new ControladorReglasPatrones(reglasPatrones, n::ir);

        // Premios solo muestra información. Volver atrás lo hace la barra de arriba.
        new ControladorPremios(premios);

        new ControladorJuegoClasico(clasico, n::ir);
        new ControladorPatron(patrones, n::ir);

        // Multijugador tampoco cambia de pantalla por sí solo.
        new ControladorMultijugador(multijugador);

        new ControladorModoClasico(modoClasico, n::ir);

        // Entrega la ventana ya armada a quien llamó a crear.
        return ventana;
    }

    // Aquí empieza el programa. Java siempre ejecuta primero el main.
    // void significa que no devuelve nada.
    public static void main(String[] args) {

        // Carga las imágenes antes de abrir la ventana para que no haya retraso después.
        // Se llama con el nombre de la clase y sin new porque es un método static.
        Sprites.precargar();

        // Esta línea solo crea el hilo. Todavía no hace nada.
        // Un hilo es un trabajo que corre al mismo tiempo que el resto del programa.
        // Recibe dos datos. El primero es la tarea y el segundo es el nombre del hilo.
        // La tarea es ecos.modelo.TipoCambio::actualizarSiHaceFalta.
        // Los dos puntos dobles son un atajo que significa llamar a ese método.
        // Ese método pide el dólar del día solo si no está actualizado.
        // Se escribe con la ruta completa porque esa clase no está en los imports.
        Thread dolar = new Thread(ecos.modelo.TipoCambio::actualizarSiHaceFalta, "dolar-inicio");

        // Daemon significa que este hilo no impide que el programa se cierre.
        // Sin esto, si internet se queda colgado, el programa podría no terminar nunca.
        dolar.setDaemon(true);

        // Hasta aquí el hilo estaba solo creado. Con start empieza a ejecutar su tarea.
        // Desde este momento consulta el dólar y el programa sigue sin esperarlo.
        dolar.start();

        // Esta línea abre la ventana.
        // Los métodos que aparecen son invokeLater, crear e iniciar.
        // La parte con la flecha es una lambda. Es un bloque de código guardado para ejecutarse después.
        // Los paréntesis vacíos a la izquierda significan que no recibe datos.
        // A la derecha de la flecha está lo que hace. Primero crear, que arma todo
        // y devuelve la ventana, y luego iniciar con el texto intro, que la abre en esa pantalla.
        // En dos líneas normales sería Ventana v igual a crear, y después v.iniciar con intro.
        // invokeLater recibe ese bloque y se lo pasa a Swing para que lo ejecute en su propio hilo.
        // Swing exige que todo lo que se dibuja se haga ahí.
        SwingUtilities.invokeLater(() -> crear().iniciar("intro"));
    }
}