package ecos.pantallas;

import ecos.app.Pantalla; // Clase base de todas las pantallas.
import ecos.app.Sesion; // Datos compartidos temporales (nombre del jugador).
import ecos.componentes.BotonMenu; // Botón grande con estilo pixel.
import ecos.componentes.CampoClavePixel; // Campo de contraseña.
import ecos.componentes.CampoTextoPixel; // Campo de texto.
import ecos.componentes.DesfileMascotas; // Mascotas que desfilan arriba.
import ecos.componentes.Etiqueta; // Texto con estilo.
import ecos.componentes.PanelPixel; // Caja con borde pixelado.
import ecos.recursos.Sonido; // Para los efectos de sonido.
import java.awt.Component; // Tipo general de componente.
import java.awt.Dimension; // Ancho y alto.
import java.awt.FlowLayout; // Layout que acomoda uno al lado del otro.
import javax.swing.BorderFactory; // Fábrica de bordes.
import javax.swing.Box; // Para dejar espacios vacíos.
import javax.swing.BoxLayout; // Layout que apila en vertical.
import javax.swing.JComponent; // Componente básico de Swing.
import javax.swing.JPanel; // Panel de Swing.
import java.awt.GridBagLayout; // Layout para centrar.

public class pantallaAcceso extends Pantalla {

    private final DesfileMascotas desfile = new DesfileMascotas(); // Mascotas que desfilan arriba de la tarjeta.
    private final CampoTextoPixel campoUsuario = new CampoTextoPixel("Tu usuario", 16); // Campo del usuario.
    private final CampoClavePixel campoClave = new CampoClavePixel("Tu contraseña", 16); // Campo de la contraseña.

    public pantallaAcceso() { // Arma la pantalla.
        super("acceso", "acceso"); // Id de la pantalla y música que usa.
        setLayout(new GridBagLayout()); // Centra todo en la pantalla.
        setBorder(BorderFactory.createEmptyBorder(MARGEN_SUPERIOR, 16, 28, 16)); // Deja espacio para la barra de arriba.
        campoUsuario.setLimite(20); // Máximo 20 letras en el usuario.

        BotonMenu entrar = new BotonMenu("Entrar", true); // Botón de entrar con usuario y contraseña.
        entrar.addActionListener(e -> entrarTemporal()); // Por ahora entra directo; la lógica real viene después.
        BotonMenu faceId = new BotonMenu("Entrar con Face ID", BotonMenu.Estilo.SECUNDARIO); // Botón para entrar con la cara.
        faceId.addActionListener(e -> { Sonido.efecto("click"); irA("login_facial"); }); // Va a la pantalla del escaneo.
        BotonMenu registrarse = new BotonMenu("Registrarse", BotonMenu.Estilo.ROSA); // Botón para crear una cuenta.
        registrarse.setPequeno(true); // Versión chica.
        registrarse.addActionListener(e -> { Sonido.efecto("click"); irA("registro"); }); // Va a la pantalla de registro.

        JPanel filaRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Fila: "¿No tenés cuenta?" + botón.
        filaRegistro.setOpaque(false); // Transparente.
        filaRegistro.add(Etiqueta.texto("¿No tenés cuenta?", 20f, Etiqueta.Rol.SUAVE)); // Texto.
        filaRegistro.add(Box.createHorizontalStrut(12)); // Espacio entre el texto y el botón.
        filaRegistro.add(registrarse); // Botón.

        PanelPixel tarjeta = new PanelPixel(); // Tarjeta de acceso.
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS)); // Apila todo en vertical.
        tarjeta.setPadding(22); // Margen interno.
        tarjeta.add(alinear(Etiqueta.texto("Iniciar sesión", 34f, Etiqueta.Rol.TEXTO))); // Título.
        tarjeta.add(Box.createVerticalStrut(16)); // Espacio.
        tarjeta.add(alinear(Etiqueta.titulo("USUARIO", 10f, Etiqueta.Rol.SUAVE))); // Etiqueta del usuario.
        tarjeta.add(alinear(campoUsuario)); // Campo del usuario.
        tarjeta.add(Box.createVerticalStrut(12)); // Espacio.
        tarjeta.add(alinear(Etiqueta.titulo("CONTRASEÑA", 10f, Etiqueta.Rol.SUAVE))); // Etiqueta de la contraseña.
        tarjeta.add(alinear(campoClave)); // Campo de la contraseña.
        tarjeta.add(Box.createVerticalStrut(18)); // Espacio.
        tarjeta.add(alinear(entrar.anchoCompleto())); // Botón Entrar.
        tarjeta.add(Box.createVerticalStrut(10)); // Espacio.
        tarjeta.add(alinear(faceId.anchoCompleto())); // Botón Entrar con Face ID.
        tarjeta.add(Box.createVerticalStrut(16)); // Espacio.
        tarjeta.add(alinear(filaRegistro)); // "¿No tenés cuenta?" y Registrarse.
        tarjeta.setMaximumSize(new Dimension(480, Integer.MAX_VALUE)); // Ancho máximo de la tarjeta.

        JPanel columna = new JPanel(); // Columna: mascotas arriba y tarjeta abajo.
        columna.setOpaque(false); // Transparente.
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS)); // Apila en vertical.
        desfile.setAlignmentX(Component.CENTER_ALIGNMENT); // Mascotas al centro.
        tarjeta.setAlignmentX(Component.CENTER_ALIGNMENT); // Tarjeta al centro.
        columna.add(desfile); // Agrega las mascotas.
        columna.add(Box.createVerticalStrut(18)); // Espacio.
        columna.add(tarjeta); // Agrega la tarjeta.
        add(columna); // Agrega todo a la pantalla.
    }

    @Override // Indica que se reemplaza un método de Pantalla.
    public void alEntrar() { // Se ejecuta cada vez que se entra a esta pantalla.
        super.alEntrar(); // Pone la música.
        campoClave.setText(""); // Borra la contraseña anterior.
        desfile.iniciar(); // Empieza el desfile de mascotas.
    }

    @Override // Indica que se reemplaza un método de Pantalla.
    public void alSalir() { // Se ejecuta cuando se sale de esta pantalla.
        desfile.detener(); // Detiene el desfile.
    }

    // TEMPORAL: entra al menú con el usuario escrito. Se reemplaza cuando exista el login de verdad.
    private void entrarTemporal() { // Entra sin revisar nada.
        Sonido.efecto("click"); // Suena el clic.
        String usuario = campoUsuario.getText().trim(); // Lee el usuario.
        Sesion.setNombre(usuario.isEmpty() ? "Jugador 1" : usuario); // Guarda el nombre (o "Jugador 1" si lo dejó vacío).
        irA("menu"); // Va al menú.
    }

    private static JComponent alinear(JComponent c) { // Alinea un componente a la izquierda de la tarjeta.
        c.setAlignmentX(Component.LEFT_ALIGNMENT); // Lo alinea.
        return c; // Lo entrega.
    }
}
