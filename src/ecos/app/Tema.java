
package ecos.app;

// Color es el tipo que representa un color. Cada método de colores devuelve uno.
import java.awt.Color;
// ArrayList es la clase concreta de la lista. Sirve para crear la lista de escuchas y para sacar una copia de ella.
import java.util.ArrayList;
// List es el tipo de la lista de escuchas, o sea las acciones que se ejecutan cuando cambia el tema.
import java.util.List;

// Guarda los colores del juego en modo claro y oscuro, y avisa a las piezas cuando el modo cambia para que se repinten.
// Casi todo es static, así que no se crea un objeto Tema: se usa escribiendo Tema.metodo().
// El tema es uno solo para todo el juego. Si se cambia en una pantalla, queda cambiado en todas.
public class Tema {

    // Modo actual. False es claro y true es oscuro. Empieza en claro. Al ser static es una sola para todo el juego.
    private static boolean oscuro = false;
    // Lista de acciones guardadas, que se ejecutan cuando cambia el modo.
    // Runnable es un tipo de Java que guarda una acción lista para ejecutarse. No recibe datos ni devuelve nada,
    // solo se ejecuta cuando alguien llama a run. Casi siempre la acción guardada es repintarse.
    // Es final porque siempre es la misma lista, aunque se le agreguen o quiten acciones. Va en mayúsculas por ser constante.
    private static final List<Runnable> ESCUCHAS = new ArrayList<>();

    // Dice en qué modo está el juego. Devuelve true si está oscuro. Lo usan otras clases, como Sprites para elegir el fondo.
    public static boolean esOscuro() {
        return oscuro;
    }

    // Cambia de claro a oscuro, o al revés, y avisa a todas las piezas suscritas. Lo llama el botón de tema de la barra superior.
    public static void alternar() {
        // El signo de exclamación significa no, así que invierte el valor. Si era false pasa a true y al revés.
        oscuro = !oscuro;
        // Cambiar la variable no repinta nada, por eso se recorre la lista y se ejecuta cada acción con run.
        // Así cada pieza se repinta con los colores del modo nuevo.
        // Se recorre una copia de la lista por si alguien se agrega mientras se ejecutan las acciones, lo que daría error.
        for (Runnable r : new ArrayList<>(ESCUCHAS)) {
            r.run();
        }
    }

    // Sirve para suscribirse. Una pieza le dice a Tema: avisame cuando cambie el modo.
    // La acción que se guarda casi siempre es repintarse. Aquí no se ejecuta, solo se guarda.
    public static void alCambiar(Runnable accion) {
        // Solo la guarda si todavía no está en la lista, para que una misma acción no se ejecute dos veces.
        if (!ESCUCHAS.contains(accion)) {
            ESCUCHAS.add(accion);
        }
    }

    // Es lo contrario de alCambiar. Saca la acción de la lista.
    // Se usa cuando una pieza desaparece, como un diálogo que se cierra. Si no se quitara, Tema seguiría
    // repintando algo que nadie ve y gastando memoria. No cambia el modo de nadie, el modo es uno solo.
    public static void quitar(Runnable accion) {
        ESCUCHAS.remove(accion);
    }

    // Desde aquí cada método es un color con nombre. El tipo Color antes del nombre dice que devuelve un color,
    // y los paréntesis vacíos dicen que no recibe nada. Panel, sombra o dorado no son partes, son nombres de colores.
    // El signo de pregunta es un if corto. Se lee: condición, signo de pregunta, color si es verdadero,
    // dos puntos, color si es falso. Entonces devuelve el primer color en modo oscuro y el segundo en modo claro.
    // Los tres números de new Color son cuánto rojo, verde y azul tiene, de 0 a 255.

    // Fondo de los paneles.
    public static Color panel()      { return oscuro ? new Color(43, 31, 56)    : new Color(255, 243, 230); }
    // Línea clara dentro del borde de los paneles.
    public static Color panelAlto()  { return oscuro ? new Color(61, 45, 79)    : new Color(255, 255, 255); }
    // Cajas y zonas internas, como el fondo de las barras y los campos.
    public static Color caja()       { return oscuro ? new Color(28, 19, 39)    : new Color(244, 217, 196); }
    // Letras normales y letras secundarias, más apagadas.
    public static Color texto()      { return oscuro ? new Color(255, 240, 228) : new Color(58, 27, 38); }
    public static Color textoSuave() { return oscuro ? new Color(207, 174, 192) : new Color(138, 85, 102); }
    // Color de resalte para detalles importantes.
    public static Color acento()     { return oscuro ? new Color(255, 170, 90)  : new Color(194, 74, 0); }
    // Borde de paneles y botones, y la sombra dura de debajo.
    public static Color borde()      { return oscuro ? new Color(11, 7, 16)     : new Color(91, 35, 51); }
    public static Color sombra()     { return oscuro ? new Color(7, 4, 12)      : new Color(91, 35, 51); }
    // Los siguientes no llevan el signo de pregunta cuando son iguales en los dos modos.
    // Naranja que marca al jugador con el turno.
    public static Color bordeExterior() { return new Color(255, 122, 31); }
    // Durazno dorado.
    public static Color dorado()     { return new Color(255, 204, 128); }
    // Botones principales.
    public static Color boton()      { return new Color(255, 204, 128); }
    public static Color textoBoton() { return new Color(194, 74, 0); }
    // Botones secundarios. Estos sí cambian. Por ejemplo boton2 es morado en oscuro y gris claro en claro.
    public static Color boton2()     { return oscuro ? new Color(61, 45, 79)    : new Color(247, 247, 247); }
    public static Color textoBoton2() { return oscuro ? new Color(252, 201, 205) : new Color(0, 71, 181); }
    // Rosa, rojo carmesí y naranja de la paleta, iguales en los dos modos.
    public static Color rosa()       { return new Color(252, 165, 171); }
    public static Color carmesi()    { return new Color(194, 63, 82); }
    public static Color naranja()    { return new Color(255, 122, 31); }
    // Cara de las cartas.
    public static Color carta()      { return oscuro ? new Color(255, 241, 220) : new Color(255, 248, 234); }
    // Verde para acierto, amarillo para avisos y rojo para errores o peligro.
    public static Color acierto()    { return oscuro ? new Color(95, 217, 138)  : new Color(63, 191, 111); }
    public static Color alerta()     { return oscuro ? new Color(255, 209, 92)  : new Color(255, 194, 51); }
    public static Color peligro()    { return oscuro ? new Color(255, 91, 114)  : new Color(229, 52, 76); }
    // Cortina semitransparente que oscurece la pantalla detrás de un diálogo. El cuarto número es la transparencia.
    public static Color velo()       { return oscuro ? new Color(8, 5, 14, 184) : new Color(91, 35, 51, 148); }
}