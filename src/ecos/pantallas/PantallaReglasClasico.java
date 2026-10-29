package ecos.pantallas;

import ecos.app.Pantalla;
import ecos.app.Sesion;
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

    public PantallaReglasClasico() {
        super("reglas_clasico", "menu");
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 24, 16));

        campo1.setLimite(14);
        campo2.setLimite(14);
        BotonMenu comenzar = new BotonMenu("¡Comenzar!", true);
        comenzar.addActionListener(e -> comenzar());

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
            Etiqueta.titulo("JUGADOR 2", 10f, Etiqueta.Rol.SUAVE), campo2,
            reglas, comenzar.anchoCompleto()};
        for (JComponent c : piezas) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(c);
            tarjeta.add(Box.createVerticalStrut(c instanceof Etiqueta ? (c == piezas[0] ? 14 : 6) : 14));
        }
        add(tarjeta);
    }

    @Override
    public void alEntrar() {
        super.alEntrar();
        campo1.setText(Sesion.getNombre());
        campo2.setText("Invitado");
        reglas.setRegla(0, "Cada jugador tiene su propio tablero (<b>" + Sesion.getTamano() + "x" + Sesion.getTamano() + "</b>) con las parejas en otro orden.");
    }

    private void comenzar() {
        String n1 = campo1.getText().trim();
        String n2 = campo2.getText().trim();
        Sesion.setJugador1(n1.isEmpty() ? "Jugador 1" : n1);
        Sesion.setJugador2(n2.isEmpty() ? "Invitado" : n2);
        Sonido.efecto("click");
        irA("clasico");
    }
}
