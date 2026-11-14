package ecos.modelo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Consulta el dólar al BCCR una vez al día y guarda el último valor con su fecha. Sin internet o si falla el BCCR usa el
// último valor guardado, y si nunca hubo uno usa VALOR_INICIAL. El indicador 317 es compra y el 318 es venta. El token
// no va en el código: se lee de BCCR_TOKEN o de bccr.properties, que está en .gitignore.
public class TipoCambio {

    // Valor que se usa solo si nunca se ha podido consultar el BCCR.
    public static final double VALOR_INICIAL = 487.20;
    public static final String INDICADOR = "317";
    static final String URL_SERVICIO = "https://apim.bccr.fi.cr/SDDE/api/Bccr.GE.SDDE.Publico.Indicadores.API/indicadoresEconomicos/";
    // Se pide la última semana porque fines de semana y feriados no traen dato.
    private static final int DIAS_ATRAS = 7;

    // Formato de fecha del BCCR, búsqueda del valor en el JSON y archivo donde se guarda el último valor.
    private static final DateTimeFormatter FECHA_BCCR = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Pattern PATRON_VALOR = Pattern.compile("\"valorDatoPorPeriodo\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
    private static final Path ARCHIVO = Paths.get(System.getProperty("user.home"), ".ecos", "tipo_cambio.properties");

    // Último dólar conocido, día en que se guardó y marca para leer el archivo una sola vez.
    private static double valor = VALOR_INICIAL;
    private static LocalDate fecha = null;
    private static boolean cargado = false;

    public static synchronized double getCompra() {
        cargar();
        return valor;
    }

    public static synchronized LocalDate getFecha() {
        cargar();
        return fecha;
    }

    // Pide el dólar al BCCR solo si hoy todavía no se pidió. Puede tardar, llamarlo fuera del hilo de la interfaz.
    // Devuelve true si consiguió un valor nuevo.
    public static synchronized boolean actualizarSiHaceFalta() {
        cargar();
        LocalDate hoy = LocalDate.now();
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
    private static double consultarBCCR(LocalDate hoy) throws IOException, InterruptedException {
        String token = leerToken();
        if (token == null) {
            throw new IOException("falta el token del BCCR");
        }
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
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
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
        while (m.find()) {
            ultimo = m.group(1);
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

    private static String leerToken() {
        // Primero la variable de entorno BCCR_TOKEN, si existe y no está vacía.
        String v = System.getenv("BCCR_TOKEN");
        if (v != null && !v.isBlank()) {
            return v.trim();
        }
        // Si no, el archivo bccr.properties, si existe.
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
