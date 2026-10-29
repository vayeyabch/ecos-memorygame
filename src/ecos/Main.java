package ecos;

import ecos.app.Navegador; // Para registrar las pantallas.
import ecos.app.Ventana; // La ventana principal del juego.
import ecos.componentes.BarraSuperior; // Barra de arriba (Atrás, tema, música).
import ecos.app.modos.JuegoC.ControladorJuegoClasico;
import ecos.app.modos.Patrones.ControladorPatron;
import ecos.pantallas.PantallaClasico;
import ecos.pantallas.PantallaMenu;
import ecos.pantallas.PantallaPatron;
import ecos.pantallas.PantallaPremios;
import ecos.pantallas.PantallaReglasClasico;
import ecos.pantallas.PantallaReglasPatrones;
import ecos.pantallas.pantallaAcceso; // Pantalla de acceso (nombre y escáner).
import ecos.pantallas.pantallaInicio; // Pantalla de inicio (START).
import ecos.pantallas.pantallaIntro; // Pantalla de introducción.
import ecos.recursos.Sprites; // Para cargar las imágenes pesadas en segundo plano.
import javax.swing.SwingUtilities; // Para crear la interfaz en el hilo correcto de Swing.

public class Main {

    // Arma la ventana con todas las pantallas registradas (sin mostrarla todavía).
    public static Ventana crear() {
        Ventana ventana = new Ventana(); // Crea la ventana principal.
        ventana.setBarraSuperior(new BarraSuperior(ventana.getNavegador())); // Pone la barra de arriba.
        Navegador n = ventana.getNavegador(); // Guarda el navegador para registrar las pantallas.
        n.registrar(new pantallaIntro()); // Pantalla "intro".
        n.registrar(new pantallaInicio()); // Pantalla "inicio".
        n.registrar(new pantallaAcceso()); // Pantalla "acceso".
        n.registrar(new PantallaMenu()); // Pantalla "menu".
        n.registrar(new PantallaReglasClasico()); // Pantalla "reglas_clasico".
        n.registrar(new PantallaReglasPatrones()); // Pantalla "reglas_patrones".
        n.registrar(new PantallaPremios()); // Pantalla "premios".
        PantallaClasico clasico = new PantallaClasico(); // Vista del modo clásico.
        PantallaPatron patrones = new PantallaPatron(); // Vista del modo patrones.
        n.registrar(clasico); // Pantalla "clasico".
        n.registrar(patrones); // Pantalla "patrones".
        new ControladorJuegoClasico(clasico, n::ir); // Controlador temporal del clásico.
        new ControladorPatron(patrones, n::ir); // Controlador temporal de patrones.
        return ventana; // Entrega la ventana lista.
    }

    public static void main(String[] args) {
        Sprites.precargar(); // Mientras se ve la intro, carga las mascotas y los iconos en segundo plano.
        SwingUtilities.invokeLater(() -> crear().iniciar("intro")); // Crea la interfaz y abre la intro.
    }
}
