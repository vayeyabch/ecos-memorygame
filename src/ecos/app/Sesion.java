package ecos.app;

// Esta clase no tiene imports porque solo usa String, int y boolean, que Java trae automáticos.

// Guarda los datos que varias pantallas necesitan al mismo tiempo: quién inició sesión, quiénes juegan la partida
// y el tamaño del tablero. Así no hay que pasarse esos datos de pantalla en pantalla: el login los guarda aquí
// y las demás pantallas los leen.
// Todo es static, por eso no se crea un objeto: se usa directo, escribiendo Sesion.getNombre() o Sesion.setNombre("Hellen").
public class Sesion {

    // Los atributos son private, así que solo Sesion los toca directo. Son static, así que hay una sola copia para todo el juego.
    // Empiezan con valores por defecto, que se usan mientras nadie los cambie.

    // Nombre de quien inició sesión. Empieza en "Jugador 1" porque todavía no hay nadie. Cuando alguien inicia sesión se reemplaza.
    private static String nombre = "Jugador 1";
    // Nombre del jugador 1 de la partida clásica. También se reemplaza si alguien inicia sesión.
    private static String jugador1 = "Jugador 1";
    // Nombre del jugador 2. Empieza como "Invitado" porque puede jugar sin cuenta.
    private static String jugador2 = "Invitado";
    // True si el jugador 2 juega sin cuenta. Si es invitado no se guardan monedas ni ranking.
    // Cuando el jugador 2 inicia sesión, el login lo cambia a false.
    private static boolean invitado2 = true;
    // Tamaño del tablero clásico, 4 (4x4) o 6 (6x6). Empieza en 6 y cambia si se elige 4.
    private static int tamano = 6;

    // Los get son para leer: devuelven el valor guardado, sin recibir nada.
    // Los set son para escribir: quien tiene el dato nuevo se lo entrega, y se guarda reemplazando el anterior.
    // La n, la i y la t son parámetros, o sea variables temporales que existen solo mientras corre el método.

    // Devuelve el nombre de quien inició sesión.
    public static String getNombre() { return nombre; }
    // Guarda el nombre que recibe como n. Lo llama el login al iniciar sesión.
    public static void setNombre(String n) { nombre = n; }
    // Devuelve el nombre del jugador 1.
    public static String getJugador1() { return jugador1; }
    // Guarda el nombre del jugador 1.
    public static void setJugador1(String n) { jugador1 = n; }
    // Devuelve el nombre del jugador 2.
    public static String getJugador2() { return jugador2; }
    // Guarda el nombre del jugador 2.
    public static void setJugador2(String n) { jugador2 = n; }
    // Dice si el jugador 2 es invitado. Se llama is y no get porque devuelve un boolean, y esa es la costumbre de Java:
    // se lee como una pregunta, ¿es invitado el 2? Al terminar la partida se usa para decidir si se guardan monedas.
    public static boolean isInvitado2() { return invitado2; }
    // Guarda si el jugador 2 es invitado. El login lo llama con false cuando el jugador 2 inicia sesión.
    public static void setInvitado2(boolean i) { invitado2 = i; }
    // Devuelve el tamaño del tablero.
    public static int getTamano() { return tamano; }
    // Guarda el tamaño del tablero que se eligió.
    public static void setTamano(int t) { tamano = t; }
}