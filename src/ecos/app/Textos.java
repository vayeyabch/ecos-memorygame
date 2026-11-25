package ecos.app;

// ArrayList es la clase concreta de la lista. Se usa para la lista de plantillas.
import java.util.ArrayList;
// HashMap es la clase concreta del diccionario donde se guardan los textos fijos.
import java.util.HashMap;
// List es el tipo de la lista de plantillas, o sea los textos que llevan datos.
import java.util.List;
// Map es el tipo del diccionario, con una clave en español y su valor en inglés.
import java.util.Map;
// Matcher recorre un texto buscando coincidencias con un patrón y entrega los pedazos encontrados.
import java.util.regex.Matcher;
// Pattern es el patrón de búsqueda, o sea la expresión regular.
import java.util.regex.Pattern;

// Es el diccionario de español a inglés del juego. Idioma decide cuándo traducir y Textos sabe cómo.
// Hay dos tipos de texto. Los fijos van directo a una tabla. Los que llevan datos, como "Hola, %s!", van a una lista
// de plantillas, donde el %s marca el lugar del dato.
// Si un texto no está en ningún lado, se queda en español, así una traducción que falte no rompe nada.
public class Textos {

    // Tabla de textos fijos. La clave es el texto en español y el valor es el texto en inglés.
    private static final Map<String, String> TABLA = new HashMap<>();
    // Lista de textos con datos. Cada uno guarda su patrón de búsqueda y su versión en inglés.
    private static final List<Plantilla> PLANTILLAS = new ArrayList<>();
    // Patrón que encuentra el texto que está entre etiquetas HTML.
    // Se lee así: el símbolo >, un grupo con uno o más caracteres que no sean < ni >, y el símbolo <.
    // Los paréntesis forman el grupo, que es lo que se saca después con group(1).
    private static final Pattern ENTRE_ETIQUETAS = Pattern.compile(">([^<>]+)<");

    // Un record es una clase corta que solo guarda datos. Java crea solo el constructor y los métodos patron() e ingles().
    // Cada plantilla guarda el patrón que reconoce el texto en español y cómo queda en inglés, con sus %s.
    private record Plantilla(Pattern patron, String ingles) { }

    // Devuelve la traducción al inglés de un texto en español. Lo llama Idioma.t cuando el idioma es inglés.
    // Prueba en orden: texto completo, plantillas con datos, HTML, y si nada coincide devuelve el español.
    public static String ingles(String es) {
        // Primero busca el texto completo en la tabla. Si está, lo devuelve y termina.
        String directo = TABLA.get(es);
        if (directo != null) {
            return directo;
        }
        // Si no está, prueba las plantillas una por una.
        for (Plantilla pl : PLANTILLAS) {
            // Prepara la búsqueda de ese patrón dentro del texto.
            Matcher m = pl.patron().matcher(es);
            // matches pregunta si el texto completo encaja con el patrón.
            if (m.matches()) {
                // groupCount es cuántos datos hay en el texto, o sea cuántos %s tenía.
                Object[] datos = new Object[m.groupCount()];
                for (int i = 0; i < datos.length; i++) {
                    // group(i + 1) es el dato encontrado, el grupo 1 es el primero.
                    // Se traduce también con ingles, por si es una palabra conocida como Invitado, que pasa a Guest.
                    datos[i] = ingles(m.group(i + 1));
                }
                // format reemplaza cada %s del texto en inglés por su dato, en orden.
                return String.format(pl.ingles(), datos);
            }
        }
        // Si es un texto HTML, traduce solo lo que está entre etiquetas. Si no, devuelve el español sin tocar.
        // El signo de pregunta es un if corto.
        return es.startsWith("<html>") ? traducirHtml(es) : es;
    }

    // Traduce solo las partes de un texto HTML que están entre etiquetas, y deja las etiquetas igual.
    // Resuelve que los textos con <html>, <b> o <br> también se traduzcan sin romper su formato.
    private static String traducirHtml(String html) {
        // Prepara la búsqueda de los textos entre etiquetas.
        Matcher m = ENTRE_ETIQUETAS.matcher(html);
        // Un texto que se va armando por pedazos. Aquí se arma el resultado.
        StringBuilder sb = new StringBuilder();
        // find busca la siguiente coincidencia y devuelve true si la encontró. El while repite hasta que no haya más.
        while (m.find()) {
            // group(1) es el texto de adentro, sin los símbolos > y <.
            String parte = m.group(1);
            // strip quita los espacios de los extremos, y se traduce ese texto limpio.
            String traducida = ingles(parte.strip());
            // Reemplaza solo la parte limpia, para conservar los espacios de los extremos.
            String nueva = parte.replace(parte.strip(), traducida);
            // Copia lo que había antes de la coincidencia y agrega el texto nuevo entre sus símbolos.
            // quoteReplacement protege los símbolos $ y \, que si no tendrían un significado especial.
            m.appendReplacement(sb, Matcher.quoteReplacement(">" + nueva + "<"));
        }
        // Copia lo que quedó después de la última coincidencia.
        m.appendTail(sb);
        return sb.toString();
    }

    // Agrega un par español e inglés. Es el método que se usa en cada línea del bloque static de abajo.
    // Si el texto no tiene %s, va directo a la tabla. Si lo tiene, arma un patrón que reconoce el texto con cualquier dato.
    private static void p(String es, String en) {
        // Sin %s es un texto fijo. Lo guarda en la tabla y termina con return.
        if (!es.contains("%s")) {
            TABLA.put(es, en);
            return;
        }
        // Con %s, va armando el patrón por pedazos.
        StringBuilder regex = new StringBuilder();
        // Parte el texto por cada %s. El -1 hace que no se pierdan los pedazos vacíos del final.
        // Ejemplo: "Hola, %s!" queda en dos partes, "Hola, " y "!".
        String[] partes = es.split("%s", -1);
        for (int i = 0; i < partes.length; i++) {
            // quote hace que ese pedazo se busque tal cual, aunque tenga símbolos especiales como ? o .
            regex.append(Pattern.quote(partes[i]));
            // Entre un pedazo y el siguiente va el hueco del dato.
            // (.+?) es un grupo que captura cualquier carácter, al menos uno, y el ? hace que tome lo mínimo posible.
            if (i < partes.length - 1) {
                regex.append("(.+?)");
            }
        }
        // DOTALL hace que el punto también capture los saltos de línea, para textos largos de varias líneas.
        // Guarda la plantilla con su patrón y su versión en inglés.
        PLANTILLAS.add(new Plantilla(Pattern.compile(regex.toString(), Pattern.DOTALL), en));
    }

    // Todas las traducciones, en orden de pantalla.
    // Un bloque static se ejecuta una sola vez, cuando la clase se usa por primera vez. Aquí llena la tabla y las plantillas.
    static {
        // Nombres y palabras sueltas que se repiten en varias pantallas.
        p("Jugador 1", "Player 1");
        p("Invitado", "Guest");
        p("JUGADOR 1", "PLAYER 1");
        p("JUGADOR 2", "PLAYER 2");
        p("INVITADO", "GUEST");
        p("USUARIO", "USERNAME");
        p("CONTRASEÑA", "PASSWORD");
        p("Cerrar", "Close");
        p("Cancelar", "Cancel");
        p("Entrar", "Enter");
        p("Iniciar sesión", "Log in");
        p("< Atrás", "< Back");
        p("Entendido", "Got it");

        // Idiomas y Acerca de.
        p("IDIOMAS DISPONIBLES", "AVAILABLE LANGUAGES");
        p("ACERCA DE ECOS", "ABOUT ECOS");
        p("Proyecto Memory Game", "Memory Game Project");
        p("Proyecto final del curso Desarrollo de Software II, realizado por estudiantes de Informática Empresarial.", "Final project for the Software Development II course, made by Business Computing students.");

        // Intro.
        p("De los creadores de AvatarsVsRooks", "From the creators of AvatarsVsRooks");
        p("Una colaboración entre Gravita y Eva Solutions", "A collaboration between Gravita and Eva Solutions");
        p("Estamos despertando a las mascotas", "We're waking up the pets");
        p("Saltar >>", "Skip >>");

        // Acceso (iniciar sesión).
        p("Tu usuario", "Your username");
        p("Tu contraseña", "Your password");
        p("Entrar con Face ID", "Log in with Face ID");
        p("Registrarse", "Sign up");
        p("¿No tenés cuenta?", "Don't have an account?");
        p("Registrar mi rostro", "Register my face");
        p("Esta cuenta no tiene rostro registrado.", "This account has no face registered.");
        p("No se pudo conectar con la base de datos.", "Could not connect to the database.");
        p("Escribí tu usuario.", "Type your username.");
        p("Ese usuario no existe.", "That username doesn't exist.");
        p("Escribí tu usuario y tu contraseña.", "Type your username and password.");
        p("Usuario o contraseña incorrectos.", "Wrong username or password.");

        // Registro (paso 1).
        p("Completá todos los campos para continuar.", "Fill in all the fields to continue.");
        p("Tu nombre", "Your first name");
        p("Tus apellidos", "Your last names");
        p("tucorreo@ejemplo.com", "youremail@example.com");
        p("6 o más", "6 or more");
        p("Otra vez", "Again");
        p("NOMBRE", "FIRST NAME");
        p("APELLIDOS", "LAST NAMES");
        p("CORREO ELECTRÓNICO", "EMAIL");
        p("REPETIR CONTRASEÑA", "REPEAT PASSWORD");
        p("Mi rostro (opcional)", "My face (optional)");
        p("Crear cuenta", "Create account");
        p("PASO 1 DE 2", "STEP 1 OF 2");
        p("Completá todos los campos.", "Fill in all the fields.");
        p("Algún campo es demasiado largo.", "A field is too long.");
        p("El correo no es válido.", "The email is not valid.");
        p("El usuario no puede tener espacios.", "The username can't have spaces.");
        p("La contraseña necesita %s caracteres o más.", "The password needs %s characters or more.");
        p("Las contraseñas no son iguales.", "The passwords don't match.");
        p("Ese usuario ya existe. Probá con otro.", "Username already taken. Try another.");

        // Registro del rostro (paso 2).
        p("Colocá tu rostro dentro del marco.", "Place your face inside the frame.");
        p("Mirá al frente", "Look straight ahead");
        p("Omitir por ahora", "Skip for now");
        p("¡Cuenta creada! Ya podés iniciar sesión.", "Account created! You can log in now.");
        p("LA CAPTURA TIENE 1 PASO", "THE CAPTURE HAS 1 STEP");
        p("Es opcional: si lo omitís, entrás con tu contraseña y podés registrar tu rostro después.", "It's optional: if you skip it, you log in with your password and can register your face later.");
        p("Registrá tu rostro", "Register your face");
        p("PASO 2 DE 2 · OPCIONAL", "STEP 2 OF 2 · OPTIONAL");
        p("Capturando rostro…", "Capturing face…");
        p("Primero completá tus datos en el paso 1.", "First fill in your details in step 1.");
        p("Creando tu cuenta…", "Creating your account…");
        p("¡Cuenta creada! Podés registrar tu rostro después.", "Account created! You can register your face later.");
        p("Guardando tu cuenta…", "Saving your account…");
        p("¡Rostro registrado! Ya podés iniciar sesión.", "Face registered! You can log in now.");
        p("No se pudo preparar el reconocimiento facial.", "Could not set up face recognition.");
        p("Volvé al paso 1 y completá tus datos.", "Go back to step 1 and fill in your details.");
        p("Ese usuario ya existe. Volvé al paso 1 y elegí otro.", "That username already exists. Go back to step 1 and pick another.");
        p("No se pudo guardar en la base de datos.", "Could not save to the database.");
        p("No se capturó ningún rostro. Intentá de nuevo.", "No face was captured. Try again.");

        // Entrar con Face ID.
        p("Escanear mi rostro", "Scan my face");
        p("Usar usuario y contraseña", "Use username and password");
        p("Escaneando…", "Scanning…");
        p("¡Rostro reconocido!", "Face recognized!");
        p("Reconociendo tu rostro…", "Recognizing your face…");
        p("Falló el reconocimiento facial.", "Face recognition failed.");
        p("¡Rostro reconocido! Hola, %s.", "Face recognized! Hi, %s.");
        p("Todavía no hay rostros registrados. Entrá con tu contraseña y registrá el tuyo.", "No faces are registered yet. Log in with your password and register yours.");
        p("No te reconocí. Probá de nuevo o entrá con tu contraseña. Si no tenés rostro registrado, escribí tu usuario y contraseña y tocá «Registrar mi rostro».", "Face not recognized. Try again or log in with your password. If you haven't registered your face, type your username and password and tap “Register my face”.");

        // Mensajes de la cámara.
        p("No se pudo abrir la cámara. Revisá que esté conectada y que otro programa no la esté usando.", "Could not open the camera. Check that it is connected and that no other program is using it.");
        p("No se detectó el movimiento a tiempo. Intentá de nuevo.", "The movement wasn't detected in time. Try again.");
        p("Mirá al frente… foto %s de %s", "Look straight ahead… photo %s of %s");
        p("¡Bien! Mantené el giro un momento…", "Good! Hold the turn for a moment…");
        p("Falló la cámara. Intentá de nuevo.", "The camera failed. Try again.");
        p("Mirá de frente a la cámara.", "Look straight at the camera.");
        p("Paso 1 de 3: mirá de frente a la cámara.", "Step 1 of 3: look straight at the camera.");
        p("Paso 2 de 3: girá la cabeza hacia tu IZQUIERDA.", "Step 2 of 3: turn your head to your LEFT.");
        p("Paso 3 de 3: girá la cabeza hacia tu DERECHA.", "Step 3 of 3: turn your head to your RIGHT.");
        p("No veo tu rostro, mirá a la cámara con buena luz.", "I can't see your face, look at the camera with good light.");
        p("No te veo bien: girá un poco más, o volvé al frente.", "I can't see you well: turn a bit more, or go back to the front.");
        p("Girá más, casi de perfil.", "Turn more, almost in profile.");
        p("Volvé a mirar de frente.", "Look straight ahead again.");
        p("Giraste hacia el otro lado.", "You turned the other way.");

        // Menú.
        p("Hola, %s!", "Hi, %s!");
        p("Clásico", "Classic");
        p("2 jugadores · parejas", "2 players · pairs");
        p("Patrones", "Patterns");
        p("1 jugador · memoriza", "1 player · memorize");
        p("Premios", "Prizes");
        p("Salir", "Log out");
        p("Tablero", "Board");

        // Reglas del modo clásico y login del jugador 2.
        p("Nueva partida clásica", "New classic game");
        p("Jugar sin cuenta", "Play as guest");
        p("¡Comenzar!", "Start!");
        p("Cada jugador tiene su tablero con las parejas en otro orden.", "Each player has their own board with the pairs in a different order.");
        p("Cada jugador tiene su tablero (<b>%sx%s</b>) con las parejas en otro orden.", "Each player has their own board (<b>%sx%s</b>) with the pairs in a different order.");
        p("Tenés 10 segundos por jugada. Si encontrás una pareja, se te suman 7 segundos y seguís jugando; si fallás, pasa el turno.", "You have 10 seconds per move. If you find a pair, you get 7 more seconds and keep playing; if you miss, the turn passes.");
        p("Gana quien encuentre todas las parejas con menos intentos. El ranking y el premio en dinero salen de esos intentos (solo con cuenta).", "Whoever finds all the pairs with the fewest attempts wins. The ranking and the cash prize come from those attempts (account only).");
        p("Cada victoria te da monedas para comprar el Ojo espía.", "Every win gives you coins to buy the Spy Eye.");
        p("Cada victoria te da <b>%s monedas</b>. Con <b>%s</b> comprás el Ojo espía. Sin cuenta no hay premio ni monedas.", "Each win gives you <b>%s coins</b>. With <b>%s</b> you can buy the Spy Eye. Without an account there are no prizes or coins.");
        p("Jugador 2: %s. Suma monedas y entra al ranking.", "Player 2: %s. Earns coins and enters the ranking.");
        p("Jugador 2: Invitado. Sin cuenta no hay monedas, ranking ni premio.", "Player 2: Guest. Without an account there are no coins, ranking or prize.");
        p("Primero elegí cómo juega el jugador 2: iniciar sesión o jugar sin cuenta.", "First choose how player 2 plays: log in or play without an account.");
        p("Esa cuenta ya es la del jugador 1. Usá otra.", "That's player 1's account. Use another.");
        p("JUGADOR 2, ¡INICIÁ SESIÓN!", "PLAYER 2, LOG IN!");
        p("Así sumás monedas y entrás al ranking", "Earn coins and join the ranking");

        // Reglas del modo patrones.
        p("Modo patrones", "Patterns mode");
        p("Mirá cómo se iluminan las casillas de la matriz de 4x4 y memorizá el orden.", "Watch how the tiles of the 4x4 grid light up and memorize the order.");
        p("Empezás con 3 casillas. Cada nivel que superás suma 1 casilla más.", "You start with 3 tiles. Each level you beat adds 1 more tile.");
        p("Tenés 12 segundos en total para repetir el patrón y 2 segundos entre casilla y casilla.", "You have 12 seconds in total to repeat the pattern and 2 seconds between tiles.");
        p("Si te equivocás o se acaba el tiempo, termina la partida.", "If you make a mistake or time runs out, the game ends.");

        // Partida de patrones.
        p("Nivel %s · %s casillas", "Level %s · %s tiles");
        p("Pulsá empezar", "Press start");
        p("Empezar", "Start");
        p("Memoriza el patrón", "Memorize the pattern");
        p("¡Tu turno!", "Your turn!");
        p("¡Muy bien!", "Nice one!");
        p("Fin del juego", "Game over");
        p("Esa casilla no era la del patrón", "That tile wasn't in the pattern");
        p("Se acabaron los 12 segundos", "The 12 seconds are up");
        p("Pasaron más de 2 segundos entre casillas", "More than 2 seconds passed between tiles");
        p("¡NIVEL %s!", "LEVEL %s!");
        p("Niveles superados", "Levels cleared");
        p("Casillas del último patrón", "Tiles in the last pattern");
        p("¡%s, llegaste al nivel %s!", "%s, you reached level %s!");
        p("Volver al menú", "Back to menu");
        p("Jugar otra vez", "Play again");
        p("Mejor nivel", "Best level");
        p("Fin del patrón", "Pattern over");
        p("Reintentar", "Retry");
        p("Menú", "Menu");

        // Partida clásica, Ojo espía y victoria.
        p("Turno de %s", "%s's turn");
        p("TURNO DE %s", "%s'S TURN");
        p("Intentos %s", "Attempts %s");
        p("Parejas %s/%s", "Pairs %s/%s");
        p("Ojo espía", "Spy Eye");
        p("¡Te faltan monedas!", "Not enough coins!");
        p("Estás jugando sin cuenta, así que no tenés monedas. Iniciá sesión en la próxima partida para ganarlas y comprar el Ojo espía.", "You're playing without an account, so you have no coins. Log in next game to earn them and buy the Spy Eye.");
        p("Tenés <b>%s</b> monedas y el Ojo espía cuesta <b>%s</b>. ¡Seguí jugando y ganá partidas para conseguir más!", "You have <b>%s</b> coins and the Spy Eye costs <b>%s</b>. Keep playing and win games to earn more!");
        p("Solo se puede usar en tu turno y antes de voltear una carta.", "It can only be used on your turn and before flipping a card.");
        p("Comprar", "Buy");
        p("Voltea todas las cartas de tu tablero durante 1.5 segundos.", "Flips all the cards on your board for 1.5 seconds.");
        p("¡Ganó %s!", "%s wins!");
        p("Premio: %s", "Prize: %s");
        p("Ver premios", "See prizes");
        p("intentos", "attempts");
        p("Sin cuenta no hay premio ni monedas.", "Guests don't earn prizes or coins.");
        p("+%s monedas para %s", "+%s coins for %s");
        p("No se pudo guardar la victoria (revisá la conexión).", "Could not save the win (check your connection).");

        // Premios y ranking.
        p("Mejores 5 jugadores", "Top 5 players");
        p("JUGADOR", "PLAYER");
        p("INTENTOS", "ATTEMPTS");
        p("PREMIO", "PRIZE");
        p("Monedas de %s: %s", "%s's coins: %s");
        p("Monedas: %s", "Coins: %s");
        p("Las monedas no cambian el ranking: sirven para comprar el Ojo espía.", "Coins don't change the ranking: they're for buying the Spy Eye.");

        // Elegir cómo jugar el modo clásico (local o en red).
        p("Elegí cómo querés jugar", "Choose how you want to play");
        p("Multijugador en red", "Network multiplayer");
        p("Local o en red · parejas", "Local or network · pairs");
        p("2 jugadores · una computadora", "2 players · one computer");
        p("2 jugadores · 2 computadoras", "2 players · 2 computers");
        p("Cada jugador tiene su tablero con las parejas en otro orden y juegan por turnos.", "Each player has their own board with the pairs in a different order, and you take turns.");
        p("Gana quien encuentre todas las parejas con menos intentos.", "Whoever finds all the pairs with the fewest attempts wins.");
        p("Con cuenta ganás <b>premio en dinero</b>, entrás al <b>ranking</b> y sumás monedas.", "With an account you win <b>cash prizes</b>, join the <b>ranking</b> and earn coins.");
        p("Uno crea la partida y el otro se une con su IP, en la misma red.", "One player hosts and the other joins with their IP, on the same network.");
        p("Comparten un solo tablero y gana quien junte más parejas.", "You share one board and whoever collects the most pairs wins.");
        p("No hay <b>Ojo espía</b>, porque los dos ven el mismo tablero.", "There's no <b>Spy Eye</b>, since you both see the same board.");
        p("Ganar suma <b>monedas</b> para la partida clásica, pero no cuenta para el ranking ni para el premio.", "Winning earns <b>coins</b> for the classic game, but it doesn't count toward the ranking or the prize.");

        // Multijugador en red (lobby).
        p("Dos computadoras y un solo tablero, por eso aquí no hay Ojo espía. Si ganás sumás monedas para la partida clásica, pero no cuenta para el ranking.", "Two computers and one board, so there's no Spy Eye here. If you win you earn coins for the classic game, but it doesn't count toward the ranking.");
        p("TABLERO", "BOARD");
        p("SI VAS A CREAR LA PARTIDA", "IF YOU'RE CREATING THE GAME");
        p("SI TE VAS A UNIR", "IF YOU'RE JOINING");
        p("IP del anfitrión (o IP:puerto)", "Host IP (or IP:port)");
        p("Crear partida", "Create game");
        p("Unirse", "Join");
        p("Volver", "Back");
        p("Salir de la partida", "Leave game");
        p("Esperando al otro jugador…", "Waiting for the other player…");
        p("IP local (misma wifi):", "Local IP (same wifi):");
        p("No se pudo abrir el puerto %s. ¿Ya hay una partida creada?", "Could not open port %s. Is there already a game created?");
        p("Escribí la IP del anfitrión.", "Type the host's IP.");
        p("Conectando…", "Connecting…");
        p("Conectado, esperando que empiece la partida…", "Connected, waiting for the game to start…");
        p("No se pudo conectar. Revisá la IP, el puerto y que el anfitrión haya creado la partida.", "Could not connect. Check the IP, the port, and that the host has created the game.");

        // Partida en red.
        p("Jugador", "Player");
        p("Rival", "Opponent");
        p("¡Es tu turno!", "It's your turn!");
        p("TU TURNO", "YOUR TURN");
        p("¡Terminó la partida!", "The game is over!");
        p("¡Empate!", "It's a tie!");
        p("Ganó %s.", "%s wins.");
        p("Sin cuenta no se guardan monedas.", "Without an account, coins aren't saved.");
        p("No se pudo guardar la partida.", "Could not save the game.");
        p("¡Ganaste %s monedas!", "You won %s coins!");
        p("Esta vez no hay monedas.", "No coins this time.");
        p("Usá «Salir de la partida» para volver.", "Use “Leave game” to go back.");
        p("Se cortó la conexión", "The connection was lost");
        p("Se cortó la conexión.", "The connection was lost.");
        p("El otro jugador se desconectó. Usá «Salir de la partida» para volver.", "The other player disconnected. Use “Leave game” to go back.");
    }
}