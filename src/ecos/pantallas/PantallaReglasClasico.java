package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.componentes.BotonMenu;
import ecos.componentes.CampoTextoPixel;
import ecos.componentes.Etiqueta;
import ecos.componentes.ListaReglas;
import ecos.componentes.PanelPixel;
import ecos.recursos.Sonido;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

public class PantallaReglasClasico extends Pantalla {

    private final CampoTextoPixel campo1 = new CampoTextoPixel("Jugador 1", 14); // nombre del jugador 1
    private final CampoTextoPixel campo2 = new CampoTextoPixel("Invitado", 14); // nombre del jugador 2
    private final ListaReglas reglas = new ListaReglas(460,
            "Cada jugador tiene su propio tablero (<b>6x6</b>) con las parejas en otro orden.",
            "10 segundos por jugada. Si acertás ganás 7 segundos más. Si fallás, pasa el turno.",
            "Gana quien use menos intentos para encontrar todas las parejas.",
            "El Ojo espía voltea todo tu tablero un momento. Lo comprás con monedas durante la partida.");

    private Runnable alComenzar = () -> { }; // Qué hacer al tocar Comenzar (lo pone el controlador).

    public PantallaReglasClasico() {
        super("reglas_clasico", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        campo1.setLimite(14);
        campo2.setLimite(14);
        BotonMenu comenzar = new BotonMenu("¡Comenzar!", true);
        comenzar.addActionListener(e -> { Sonido.efecto("click"); alComenzar.run(); });

        PanelPixel tarjeta = new PanelPixel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Math.max(560, super.getPreferredSize().width), super.getPreferredSize().height);
            }
        };
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPadding(18);
        JComponent[] piezas = {
            Etiqueta.titulo("Nueva partida clásica", 16f, Etiqueta.Rol.TEXTO),
            Etiqueta.titulo("JUGADOR 1", 10f, Etiqueta.Rol.SUAVE), campo1,
            Etiqueta.titulo("JUGADOR 2 (APODO)", 10f, Etiqueta.Rol.SUAVE), campo2,
            Etiqueta.texto("<html><div style='width:460px'>Es solo un apodo. Si ganás, te pediremos tu usuario y contraseña para sumar tus puntos.</div></html>", 20f, Etiqueta.Rol.ACENTO),
            reglas, comenzar.anchoCompleto()};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(c instanceof Etiqueta ? (c == piezas[0] ? 14 : 6) : 14));
        }
        add(tarjeta);
    }

    // ---- Para el controlador (ControladorReglasClasico) ----

    public void setAlComenzar(Runnable accion) { alComenzar = accion; } // Se ejecuta al tocar Comenzar.

    public String getNombre1() { return campo1.getText(); } // Nombre escrito del jugador 1.

    public String getNombre2() { return campo2.getText(); } // Nombre escrito del jugador 2.

    public void setNombres(String n1, String n2) { // Escribe los nombres en los campos.
        campo1.setText(n1);
        campo2.setText(n2);
    }

    public void setReglaTablero(String texto) { reglas.setRegla(0, texto); } // Cambia la primera regla.
}
