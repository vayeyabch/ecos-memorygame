package ecos;

import ecos.app.Navegador; // Para registrar las pantallas.
import ecos.app.Ventana; // La ventana principal del juego.
import ecos.app.modos.JuegoC.ControladorJuegoClasico; // Controlador del modo clásico.
import ecos.app.modos.Patrones.ControladorPatron; // Controlador del modo patrones.
import ecos.componentes.BarraSuperior; // Barra de arriba (Atrás, tema, música).
import ecos.pantallas.PantallaClasico; // Pantalla del modo clásico.
import ecos.pantallas.PantallaMenu; // Pantalla del menú principal.
import ecos.pantallas.PantallaPatron; // Pantalla del modo patrones.
import ecos.pantallas.PantallaPremios; // Pantalla de premios.
import ecos.pantallas.PantallaReglasClasico; // Reglas del modo clásico.
import ecos.pantallas.PantallaReglasPatrones; // Reglas del modo patrones.
import ecos.pantallas.pantallaAcceso; // Pantalla de acceso (nombre y escáner).
import ecos.pantallas.pantallaInicio; // Pantalla de inicio (START).
import ecos.pantallas.pantallaIntro; // Pantalla de introducción.
import javax.swing.SwingUtilities; // Para crear la interfaz en el hilo correcto de Swing.

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Toda la interfaz se arma dentro de este bloque.
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

            PantallaClasico clasico = new PantallaClasico(); // Crea la pantalla del modo clásico.
            PantallaPatron patron = new PantallaPatron(); // Crea la pantalla del modo patrones.
            n.registrar(clasico); // Pantalla "clasico".
            n.registrar(patron); // Pantalla "patrones".
            new ControladorJuegoClasico(clasico).iniciar(); // Conecta y arranca el controlador del clásico.
            new ControladorPatron(patron).iniciar(); // Conecta y arranca el controlador de patrones.

            ventana.iniciar("intro"); // Abre la ventana en la pantalla de intro.
        });
    }
}