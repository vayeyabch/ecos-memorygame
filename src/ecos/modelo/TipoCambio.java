package ecos.modelo;

// Lectura de archivos, IOException para los errores de lectura.
import java.io.IOException;
// Direcciones web, URI para las direcciones web y URLEncoder para codificar texto para una dirección web.
import java.net.URI;
import java.net.URLEncoder;
// Cliente HTTP, HttpClient para consultar por internet, HttpRequest para armar la consulta y HttpResponse para recibir
// la respuesta.
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
// Codificación de texto, StandardCharsets para el texto en UTF-8.
import java.nio.charset.StandardCharsets;
// Rutas y archivos, Files para leer y revisar archivos, Path para las rutas de archivos y Paths para armar rutas de
// archivos.
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// Fechas y tiempos, Duration para las duraciones y LocalDate para las fechas.
import java.time.Duration;
import java.time.LocalDate;
// Formato de fechas, DateTimeFormatter para dar formato a las fechas.
import java.time.format.DateTimeFormatter;
// Utilidades de Java, Properties para leer archivos .properties.
import java.util.Properties;
// Búsqueda de texto con patrones, Matcher para buscar con ese patrón y Pattern para definir el patrón de búsqueda.
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Consulta el tipo de cambio del dólar al BCCR UNA VEZ AL DÍA y guarda el último valor con su fecha.
// Si no hay internet (o falla el BCCR) usa el último valor guardado; si nunca se guardó nada, usa VALOR_INICIAL.
// Así el dólar nunca queda vacío ni da error.
// Indicadores del BCCR, 317 = compra, 318 = venta (cambiar INDICADOR si se quiere la venta; el método getCompra usa
// el que diga INDICADOR).
// El token NO va en el código, se lee de la variable de entorno BCCR_TOKEN o del archivo
// bccr.properties (línea token=...), que está en .gitignore para no subirlo a Git.
// A cargo de Allison.
// Modelo del dólar del BCCR.
public class TipoCambio {

    // Se usa solo si nunca se ha podido consultar el BCCR. 317 = dólar compra, 318 = dólar venta.
    public static final double VALOR_INICIAL = 487.20;
    public static final String INDICADOR = "317";
    static final String URL_SERVICIO = "https://apim.bccr.fi.cr/SDDE/api/Bccr.GE.SDDE.Publico.Indicadores.API/indicadoresEconomicos/";
    // Se pide la última semana porque fines de semana y feriados no traen dato.
    private static final int DIAS_ATRAS = 7;

    // Formato de fecha del BCCR, búsqueda del valor en el JSON y lugar donde se guarda el último valor.
    private static final DateTimeFormatter FECHA_BCCR = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Pattern PATRON_VALOR = Pattern.compile("\"valorDatoPorPeriodo\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
    private static final Path ARCHIVO = Paths.get(System.getProperty("user.home"), ".ecos", "tipo_cambio.properties");

    // Último dólar conocido, día en que se guardó (null si nunca) y una marca para leer el archivo una sola vez.
    private static double valor = VALOR_INICIAL;
    private static LocalDate fecha = null;
    private static boolean cargado = false;

    // Entrega el dólar que se debe usar ahora (el último guardado). Nunca falla ni tarda, no usa internet.
    // Entrega el dólar de compra.
    public static synchronized double getCompra() {
        // Lee el archivo la primera vez. Entrega el último valor conocido.
        cargar();
        return valor;
    }

    // Entrega la fecha del último valor guardado (null si nunca se consultó).
    // Entrega la fecha del último dólar.
    public static synchronized LocalDate getFecha() {
        // Lee el archivo la primera vez. Entrega la fecha.
        cargar();
        return fecha;
    }

    // Pide el dólar al BCCR solo si hoy todavía no se ha pedido. Puede tardar (internet), llamarlo fuera del hilo de la interfaz.
    // Devuelve true si consiguió un valor nuevo. Si algo falla, se queda con el último guardado.
    // Actualiza el dólar si hoy no se ha pedido.
    public static synchronized boolean actualizarSiHaceFalta() {
        // Lee el archivo la primera vez. Ya se consultó hoy, no se vuelve a pedir. Ya se pidió hoy, así que no hace nada.
        cargar();
        LocalDate hoy = LocalDate.now();
        if (hoy.equals(fecha)) {
            return false;
        }
        // Intenta pedirlo al BCCR. Guarda el valor nuevo. Guarda la fecha de hoy. Lo escribe en el archivo.
        try {
            valor = consultarBCCR(hoy);
            fecha = hoy;
            guardar();
            // Avisa que sí cambió. Sin internet, sin token, respuesta rara..., se usa el último valor. Si falla, muestra el
            // motivo en la consola. Avisa que no cambió.
            return true;
        } catch (Exception e) {
            System.err.println("TipoCambio: no se pudo consultar el BCCR (" + e.getMessage() + "). Se usa el último valor guardado.");
            return false;
        }
    }

    // Hace la consulta al BCCR y devuelve el valor más reciente de la última semana.
    // Pide el dólar al BCCR.
    private static double consultarBCCR(LocalDate hoy) throws IOException, InterruptedException {
        // Pide al API del BCCR los valores de la última semana, porque los fines de semana y feriados no traen dato. Busca
        // el token y, si no hay, lanza un error.
        String token = leerToken();
        if (token == null) {
            throw new IOException("falta el token del BCCR");
        }
        // Fechas de inicio y final ya codificadas para armar la dirección completa de la consulta.
        String desde = URLEncoder.encode(hoy.minusDays(DIAS_ATRAS).format(FECHA_BCCR), StandardCharsets.UTF_8);
        String hasta = URLEncoder.encode(hoy.format(FECHA_BCCR), StandardCharsets.UTF_8);
        String url = URL_SERVICIO + INDICADOR + "/series?fechaInicio=" + desde + "&fechaFin=" + hasta + "&idioma=ES";
        // Arma la solicitud con la dirección y el token, y la envía con un límite de 8 segundos para que no se quede pegada.
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer " + token)
                // Pide la respuesta en JSON. Es una consulta de tipo GET. Termina de armar la solicitud. Cliente que hace la consulta.
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        // Envía la solicitud y espera la respuesta.
        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
        // Si el BCCR no responde con éxito se lanza un error y se usa el último dólar guardado.
        // Si el BCCR no respondió bien. Lanza un error con el código.
        if (respuesta.statusCode() != 200) {
            throw new IOException("el BCCR respondió " + respuesta.statusCode());
        }
        // Saca el valor de la respuesta.
        return leerValor(respuesta.body());
    }

    // Saca del JSON el valor más reciente (el último que aparece). Visible dentro del paquete para poder probarlo.
    // Saca el valor más reciente del JSON.
    static double leerValor(String json) throws IOException {
        // Busca los valores en el texto, recorre todos y se queda con el último, que es el día más reciente.
        Matcher m = PATRON_VALOR.matcher(json);
        String ultimo = null;
        while (m.find()) {
            ultimo = m.group(1);
        }
        // Si no encontró ninguno. Lanza un error.
        if (ultimo == null) {
            throw new IOException("la respuesta del BCCR no trae valor");
        }
        // Convierte el texto a número y, si no es mayor que cero, lanza un error.
        double v = Double.parseDouble(ultimo);
        if (v <= 0) {
            throw new IOException("valor inválido: " + v);
        }
        // Entrega el valor.
        return v;
    }

    // Busca el token primero en la variable de entorno y luego en bccr.properties.
    // Busca el token del BCCR.
    private static String leerToken() {
        // El token se busca primero en la variable de entorno BCCR_TOKEN y, si existe y no está vacío, se entrega sin
        // espacios.
        String v = System.getenv("BCCR_TOKEN");
        if (v != null && !v.isBlank()) {
            return v.trim();
        }
        // Si no, lo busca en el archivo bccr.properties, si existe, y prepara las propiedades.
        Path p = Paths.get("bccr.properties");
        if (Files.exists(p)) {
            try (var in = Files.newInputStream(p)) {
                Properties props = new Properties();
                // Las lee del archivo. Toma la propiedad del token. Entrega el token o null si está vacío. Si no se pudo leer.
                props.load(in);
                String t = props.getProperty("token");
                return t == null || t.isBlank() ? null : t.trim();
            } catch (IOException e) {
                return null;
            }
        }
        // Si no hay token, entrega null.
        return null;
    }

    // Lee el último valor guardado (una sola vez).
    // Lee el último dólar guardado en el archivo de la computadora, una sola vez.
    // Lee el último dólar guardado.
    private static void cargar() {
        // Si ya lo leyó antes. No lo vuelve a leer.
        if (cargado) {
            return;
        }
        // Marca que ya lo leyó. Si el archivo no existe. No hay nada que leer.
        cargado = true;
        if (!Files.exists(ARCHIVO)) {
            return;
        }
        // Abre el archivo. Prepara las propiedades. Las lee del archivo. Lee el valor del dólar.
        try (var in = Files.newInputStream(ARCHIVO)) {
            Properties props = new Properties();
            props.load(in);
            double v = Double.parseDouble(props.getProperty("valor"));
            // Lee también la fecha.
            if (v > 0) {
                valor = v;
                fecha = LocalDate.parse(props.getProperty("fecha"));
            }
        // Archivo dañado, se ignora y se usa el valor inicial. Si falla, avisa en la consola.
        } catch (Exception e) {
            System.err.println("TipoCambio: no se pudo leer el valor guardado (" + e.getMessage() + ").");
        }
    }

    // Guarda el valor y su fecha para la próxima vez.
    // Guarda el dólar en el archivo.
    private static void guardar() {
        // Guarda el dólar y la fecha en el archivo para usarlos si después no hay internet.
        // Intenta guardarlo. Crea la carpeta si no existe. Prepara las propiedades. Guarda el valor.
        try {
            Files.createDirectories(ARCHIVO.getParent());
            Properties props = new Properties();
            props.setProperty("valor", Double.toString(valor));
            // Guarda la fecha. Abre el archivo para escribir. Escribe las propiedades.
            props.setProperty("fecha", fecha.toString());
            try (var out = Files.newOutputStream(ARCHIVO)) {
                props.store(out, "Ultimo tipo de cambio (dolar BCCR)");
            }
        // Avisa en la consola.
        } catch (IOException e) {
            System.err.println("TipoCambio: no se pudo guardar el valor (" + e.getMessage() + ").");
        }
    }
}