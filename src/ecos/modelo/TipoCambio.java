package ecos.modelo;

import java.io.IOException; // Error de lectura/escritura o de internet.
import java.net.URI; // Para armar la dirección del servicio.
import java.net.URLEncoder; // Para escribir bien las fechas dentro de la dirección.
import java.net.http.HttpClient; // Cliente de internet de Java.
import java.net.http.HttpRequest; // La petición que se envía.
import java.net.http.HttpResponse; // La respuesta que llega.
import java.nio.charset.StandardCharsets; // Para escribir en UTF-8.
import java.nio.file.Files; // Para guardar y leer archivos.
import java.nio.file.Path; // Ruta de un archivo.
import java.nio.file.Paths; // Para armar rutas.
import java.time.Duration; // Tiempo máximo de espera.
import java.time.LocalDate; // La fecha de hoy.
import java.time.format.DateTimeFormatter; // Para escribir la fecha como pide el BCCR.
import java.util.Properties; // Archivo simple de clave=valor.
import java.util.regex.Matcher; // Para buscar el valor dentro del JSON.
import java.util.regex.Pattern; // Patrón de búsqueda.

// Consulta el tipo de cambio del dólar al BCCR UNA VEZ AL DÍA y guarda el último valor con su fecha.
// Si no hay internet (o falla el BCCR) usa el último valor guardado; si nunca se guardó nada, usa VALOR_INICIAL.
// Así el dólar nunca queda vacío ni da error.
// Indicadores del BCCR: 317 = compra, 318 = venta (cambiar INDICADOR si se quiere la venta; el método getCompra usa el que diga INDICADOR).
// El token NO va en el código: se lee de la variable de entorno BCCR_TOKEN o del archivo
// bccr.properties (línea token=...), que está en .gitignore para no subirlo a Git.
// Responsable: Allison.
public class TipoCambio {

    public static final double VALOR_INICIAL = 487.20; // Se usa solo si nunca se ha podido consultar el BCCR.
    public static final String INDICADOR = "317"; // 317 = dólar compra, 318 = dólar venta.
    static final String URL_SERVICIO = "https://apim.bccr.fi.cr/SDDE/api/Bccr.GE.SDDE.Publico.Indicadores.API/indicadoresEconomicos/";
    private static final int DIAS_ATRAS = 7; // Se pide la última semana porque fines de semana y feriados no traen dato.

    private static final DateTimeFormatter FECHA_BCCR = DateTimeFormatter.ofPattern("yyyy/MM/dd"); // Formato de fecha del BCCR.
    private static final Pattern PATRON_VALOR = Pattern.compile("\"valorDatoPorPeriodo\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)"); // Busca el valor en el JSON.
    private static final Path ARCHIVO = Paths.get(System.getProperty("user.home"), ".ecos", "tipo_cambio.properties"); // Dónde se guarda el último valor.

    private static double valor = VALOR_INICIAL; // Último dólar conocido.
    private static LocalDate fecha = null; // Día en que se guardó ese valor (null = nunca).
    private static boolean cargado = false; // Para leer el archivo una sola vez.

    // Entrega el dólar que se debe usar ahora (el último guardado). Nunca falla ni tarda: no usa internet.
    public static synchronized double getCompra() {
        cargar();
        return valor;
    }

    // Entrega la fecha del último valor guardado (null si nunca se consultó).
    public static synchronized LocalDate getFecha() {
        cargar();
        return fecha;
    }

    // Pide el dólar al BCCR solo si hoy todavía no se ha pedido. Puede tardar (internet): llamarlo fuera del hilo de la interfaz.
    // Devuelve true si consiguió un valor nuevo. Si algo falla, se queda con el último guardado.
    public static synchronized boolean actualizarSiHaceFalta() {
        cargar();
        LocalDate hoy = LocalDate.now();
        if (hoy.equals(fecha)) { // Ya se consultó hoy: no se vuelve a pedir.
            return false;
        }
        try {
            valor = consultarBCCR(hoy);
            fecha = hoy;
            guardar();
            return true;
        } catch (Exception e) { // Sin internet, sin token, respuesta rara...: se usa el último valor.
            System.err.println("TipoCambio: no se pudo consultar el BCCR (" + e.getMessage() + "). Se usa el último valor guardado.");
            return false;
        }
    }

    // Hace la consulta al BCCR y devuelve el valor más reciente de la última semana.
    private static double consultarBCCR(LocalDate hoy) throws IOException, InterruptedException {
        String token = leerToken();
        if (token == null) {
            throw new IOException("falta el token del BCCR");
        }
        String desde = URLEncoder.encode(hoy.minusDays(DIAS_ATRAS).format(FECHA_BCCR), StandardCharsets.UTF_8);
        String hasta = URLEncoder.encode(hoy.format(FECHA_BCCR), StandardCharsets.UTF_8);
        String url = URL_SERVICIO + INDICADOR + "/series?fechaInicio=" + desde + "&fechaFin=" + hasta + "&idioma=ES";
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("el BCCR respondió " + respuesta.statusCode());
        }
        return leerValor(respuesta.body());
    }

    // Saca del JSON el valor más reciente (el último que aparece). Visible dentro del paquete para poder probarlo.
    static double leerValor(String json) throws IOException {
        Matcher m = PATRON_VALOR.matcher(json);
        String ultimo = null;
        while (m.find()) {
            ultimo = m.group(1); // Se queda con el último: es el día más reciente.
        }
        if (ultimo == null) {
            throw new IOException("la respuesta del BCCR no trae valor");
        }
        double v = Double.parseDouble(ultimo);
        if (v <= 0) {
            throw new IOException("valor inválido: " + v);
        }
        return v;
    }

    // Busca el token primero en la variable de entorno y luego en bccr.properties.
    private static String leerToken() {
        String v = System.getenv("BCCR_TOKEN");
        if (v != null && !v.isBlank()) {
            return v.trim();
        }
        Path p = Paths.get("bccr.properties");
        if (Files.exists(p)) {
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

    // Lee el último valor guardado (una sola vez).
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
        } catch (Exception e) { // Archivo dañado: se ignora y se usa el valor inicial.
            System.err.println("TipoCambio: no se pudo leer el valor guardado (" + e.getMessage() + ").");
        }
    }

    // Guarda el valor y su fecha para la próxima vez.
    private static void guardar() {
        try {
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