import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * InterfazServidor
 * 
 * Esta clase implementa una interfaz gráfica de servidor para un chat usando sockets.
 * Permite aceptar la conexión de un cliente, enviar y recibir mensajes en tiempo real utilizando hilos.
 */
public class InterfazServidor extends JFrame {
    // Área de texto para mostrar la conversación
    private JTextArea areaConversacion;
    // Campo de texto para escribir mensajes
    private JTextField campoMensaje;
    // Botón para enviar mensajes
    private JButton botonEnviar;

    // Socket del servidor y del cliente, y flujos de entrada/salida
    private ServerSocket servidor;
    private Socket cliente;
    private DataOutputStream salida;
    private DataInputStream entrada;

    /**
     * Constructor: Inicializa la interfaz gráfica y arranca el servidor.
     */
    public InterfazServidor() {
        setTitle("Servidor - Chat");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        areaConversacion = new JTextArea();
        areaConversacion.setEditable(false);
        areaConversacion.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(areaConversacion);

        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Acción al presionar el botón enviar
        botonEnviar.addActionListener(e -> enviarMensaje());

        // Iniciar el servidor en un hilo separado
        iniciarServidor();

        setVisible(true);
    }

    /**
     * Inicia el servidor y espera la conexión de un cliente.
     * Crea un hilo para recibir mensajes.
     */
    private void iniciarServidor() {
        new Thread(() -> {
            try {
                servidor = new ServerSocket(12345);
                areaConversacion.append("Servidor iniciado en puerto 12345\nEsperando conexión...\n");
                cliente = servidor.accept();
                areaConversacion.append("Cliente conectado desde: " + cliente.getInetAddress() + "\n");

                entrada = new DataInputStream(cliente.getInputStream());
                salida = new DataOutputStream(cliente.getOutputStream());

                // Hilo para recibir mensajes del cliente
                new Thread(() -> {
                    try {
                        String mensaje;
                        while ((mensaje = entrada.readUTF()) != null) {
                            areaConversacion.append("Cliente: " + mensaje + "\n");
                        }
                    } catch (IOException ex) {
                        areaConversacion.append("Conexión cerrada.\n");
                    }
                }).start();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al iniciar el servidor: " + ex.getMessage());
            }
        }).start();
    }

    /**
     * Envía el mensaje escrito al cliente y lo muestra en la conversación.
     */
    private void enviarMensaje() {
        String mensaje = campoMensaje.getText();
        if (!mensaje.isEmpty()) {
            try {
                salida.writeUTF(mensaje);
                areaConversacion.append("Tú: " + mensaje + "\n");
                campoMensaje.setText("");
            } catch (IOException e) {
                areaConversacion.append("No se pudo enviar el mensaje.\n");
            }
        }
    }

    /**
     * Método principal para iniciar la interfaz gráfica.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazServidor::new);
    }
}