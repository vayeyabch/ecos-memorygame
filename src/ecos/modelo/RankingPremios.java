package ecos.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Modelo de PantallaPremios, el top 5 real. Lee de la tabla partidas la mejor partida de cada usuario, ordenada por premio
// en dólares (las monedas no influyen). Los premios se muestran como se guardaron: con el dólar de ese día.
// cargar() usa la base de datos, llamarlo fuera del hilo de la interfaz. Si la base falla, se queda con el último top
// leído (o vacío si nunca se pudo leer).
// A cargo de Allison.
public class RankingPremios {

    // Cuántos puestos tiene el ranking.
    public static final int PUESTOS = 5;

    private final RepositorioPartidas repo;
    // Último top leído (se reemplaza completo, nunca se modifica).
    private volatile List<Partida> top = Collections.emptyList();

    public RankingPremios() {
        this(new RepositorioPartidas());
    }

    public RankingPremios(RepositorioPartidas repo) {
        this.repo = repo;
    }

    // Vuelve a leer el top 5 de la base. true si lo consiguió.
    public boolean cargar() {
        try {
            top = Collections.unmodifiableList(new ArrayList<>(repo.top(PUESTOS)));
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cuántos puestos hay llenos (0 a 5).
    public int cantidad() {
        return top.size();
    }

    // Nombre del jugador del puesto i (0 = primero).
    public String nombre(int i) {
        return top.get(i).getUsuario();
    }

    // Intentos de la mejor partida del puesto i.
    public int intentos(int i) {
        return top.get(i).getIntentos();
    }

    // Premio en colones del puesto i, sin símbolo. Por ejemplo "2 320".
    public String colones(int i) {
        return CalculoPremio.numeroColones(top.get(i).getPremioColones());
    }

    // Premio en dólares del puesto i. Por ejemplo "$4.76".
    public String dolares(int i) {
        return CalculoPremio.dolares(top.get(i).getPremioDolares());
    }

    // El dólar del día para mostrar arriba de la tabla.
    public String dolarDelDia() {
        return CalculoPremio.textoDolar();
    }
}
