package ecos.modelo;

// IOException: error de entrada/salida (red o archivo); se lanza cuando falla la consulta.
import java.io.IOException;
// URI: dirección web ya validada.
import java.net.URI;
// URLEncoder: convierte texto para que sea seguro dentro de una dirección web.
import java.net.URLEncoder;
// HttpClient: el cliente que hace la consulta por internet.
import java.net.http.HttpClient;
// HttpRequest: la solicitud que se manda al BCCR.
import java.net.http.HttpRequest;
// HttpResponse: la respuesta del BCCR.
import java.net.http.HttpResponse;
// StandardCharsets: codificación de texto (UTF-8).
import java.nio.charset.StandardCharsets;
// Files: leer, escribir y revisar archivos.
import java.nio.file.Files;
// Path: ruta de un archivo.
import java.nio.file.Path;
// Paths: crea rutas.
import java.nio.file.Paths;
// Duration: tiempos límite de espera.
import java.time.Duration;
// LocalDate: fecha sin hora.
import java.time.LocalDate;
// DateTimeFormatter: da formato a la fecha como la pide el BCCR.
import java.time.format.DateTimeFormatter;
// Properties: lee y guarda archivos de pares clave=valor.
import java.util.Properties;
// Matcher: busca coincidencias de un patrón en un texto.
import java.util.regex.Matcher;
// Pattern: el patrón (expresión regular) que se busca en la respuesta.
import java.util.regex.Pattern;

// Consulta el dólar al BCCR una vez al día y guarda el último valor con su fecha. Sin internet o si falla el BCCR usa el
// último valor guardado, y si nunca hubo uno usa VALOR_INICIAL. El indicador 317 es compra y el 318 es venta. El token
// no va en el código: se lee de BCCR_TOKEN o de bccr.properties, que está en .gitignore.
public class TipoCambio {

    // Valor que se usa solo si nunca se ha podido consultar el BCCR.
    public static final double VALOR_INICIAL = 487.20;
    public static final String INDICADOR = "317";
    // Sin public: solo se ve dentro del paquete (para poder probarlo).
    static final String URL_SERVICIO = "https://apim.bccr.fi.cr/SDDE/api/Bccr.GE.SDDE.Publico.Indicadores.API/indicadoresEconomicos/";
    // Se pide la última semana porque fines de semana y feriados no traen dato.
    private static final int DIAS_ATRAS = 7;

    // Formato de fecha del BCCR, búsqueda del valor en el JSON y archivo donde se guarda el último valor.
    private static final DateTimeFormatter FECHA_BCCR = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    // Busca en la respuesta el texto "valorDatoPorPeriodo": número (con decimales opcionales) y captura el número.
    private static final Pattern PATRON_VALOR = Pattern.compile("\"valorDatoPorPeriodo\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
    // Archivo en la carpeta del usuario: ~/.ecos/tipo_cambio.properties.
    private static final Path ARCHIVO = Paths.get(System.getProperty("user.home"), ".ecos", "tipo_cambio.properties");

    // Último dólar conocido, día en que se guardó y marca para leer el archivo una sola vez.
    private static double valor = VALOR_INICIAL;
    private static LocalDate fecha = null;
    private static boolean cargado = false;

    // Devuelve el dólar de compra. synchronized: dos hilos no pueden entrar a la vez.
    public static synchronized double getCompra() {
        cargar();
        return valor;
    }

    // Devuelve la fecha del último valor guardado (puede ser null si nunca se consultó).
    public static synchronized LocalDate getFecha() {
        cargar();
        return fecha;
    }

    // Pide el dólar al BCCR solo si hoy todavía no se pidió. Puede tardar, llamarlo fuera del hilo de la interfaz.
    // Devuelve true si consiguió un valor nuevo.
    public static synchronized boolean actualizarSiHaceFalta() {
        cargar();
        LocalDate hoy = LocalDate.now();
        // Si ya se consultó hoy, no se vuelve a pedir.
        if (hoy.equals(fecha)) {
            return false;
        }
        try {
            valor = consultarBCCR(hoy);
            fecha = hoy;
            guardar();
            // Si falla se usa el último valor y se muestra el motivo en la consola.
            return true;
        } catch (Exception e) {
            System.err.println("TipoCambio: no se pudo consultar el BCCR (" + e.getMessage() + "). Se usa el último valor guardado.");
            return false;
        }
    }

    // Hace la consulta al BCCR y devuelve el valor más reciente de la última semana.
    // throws: puede lanzar IOException o InterruptedException; quien la llama debe manejarlas.
    private static double consultarBCCR(LocalDate hoy) throws IOException, InterruptedException {
        String token = leerToken();
        if (token == null) {
            throw new IOException("falta el token del BCCR");
        }
        // Fechas desde (hace 7 días) y hasta (hoy), codificadas para ir en la dirección.
        String desde = URLEncoder.encode(hoy.minusDays(DIAS_ATRAS).format(FECHA_BCCR), StandardCharsets.UTF_8);
        String hasta = URLEncoder.encode(hoy.format(FECHA_BCCR), StandardCharsets.UTF_8);
        String url = URL_SERVICIO + INDICADOR + "/series?fechaInicio=" + desde + "&fechaFin=" + hasta + "&idioma=ES";
        // Arma la solicitud con la dirección y el token, con un límite de 8 segundos.
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();
        // El cliente espera máximo 5 segundos para conectarse.
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        // Envía la solicitud y recibe la respuesta como texto.
        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
        // Si el BCCR no responde bien se lanza un error y se usa el último valor guardado.
        if (respuesta.statusCode() != 200) {
            throw new IOException("el BCCR respondió " + respuesta.statusCode());
        }
        return leerValor(respuesta.body());
    }

    // Saca del JSON el último valor, que es el día más reciente. Es visible en el paquete para poder probarlo.
    static double leerValor(String json) throws IOException {
        Matcher m = PATRON_VALOR.matcher(json);
        String ultimo = null;
        // Recorre todas las coincidencias y se queda con la última (el día más reciente).
        while (m.find()) {
            ultimo = m.group(1);
        }
        if (ultimo == null) {
            throw new IOException("la respuesta del BCCR no trae valor");
        }
        // Convierte el texto a número y verifica que sea positivo.
        double v = Double.parseDouble(ultimo);
        if (v <= 0) {
            throw new IOException("valor inválido: " + v);
        }
        return v;
    }

    // Busca el token del BCCR sin ponerlo en el código.
    private static String leerToken() {
        // Primero la variable de entorno BCCR_TOKEN, si existe y no está vacía.
        String v = System.getenv("BCCR_TOKEN");
        if (v != null && !v.isBlank()) {
            return v.trim();
        }
        // Si no, el archivo bccr.properties, si existe.
        Path p = Paths.get("bccr.properties");
        if (Files.exists(p)) {
            // try-with-resources: cierra el archivo solo al terminar.
            try (var in = Files.newInputStream(p)) {
                Properties props = new Properties();
                props.load(in);
                String t = props.getProperty("token");
                return t == null || t.isBlank() ? null : t.trim();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    // Lee el último dólar guardado en el archivo, una sola vez.
    private static void cargar() {
        if (cargado) {
            return;
        }
        cargado = true;
        if (!Files.exists(ARCHIVO)) {
            return;
        }
        try (var in = Files.newInputStream(ARCHIVO)) {
            Properties props = new Properties();
            props.load(in);
            double v = Double.parseDouble(props.getProperty("valor"));
            if (v > 0) {
                valor = v;
                fecha = LocalDate.parse(props.getProperty("fecha"));
            }
        // Si el archivo está dañado se ignora, se usa el valor inicial y se avisa en la consola.
        } catch (Exception e) {
            System.err.println("TipoCambio: no se pudo leer el valor guardado (" + e.getMessage() + ").");
        }
    }

    // Guarda el valor y la fecha en el archivo para usarlos la próxima vez (o sin internet).
    private static void guardar() {
        try {
            // Crea la carpeta ~/.ecos si no existe.
            Files.createDirectories(ARCHIVO.getParent());
            Properties props = new Properties();
            props.setProperty("valor", Double.toString(valor));
            props.setProperty("fecha", fecha.toString());
            try (var out = Files.newOutputStream(ARCHIVO)) {
                props.store(out, "Ultimo tipo de cambio (dolar BCCR)");
            }
        } catch (IOException e) {
            System.err.println("TipoCambio: no se pudo guardar el valor (" + e.getMessage() + ").");
        }
    }
}