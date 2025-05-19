import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * InterfazCliente
 * 
 * Esta clase implementa una interfaz gráfica de cliente para un chat usando sockets.
 * Permite conectarse a un servidor, enviar y recibir mensajes en tiempo real utilizando hilos.
 */
public class InterfazCliente extends JFrame {
    // Área de texto para mostrar la conversación
    private JTextArea areaConversacion;
    // Campo de texto para escribir mensajes
    private JTextField campoMensaje;
    // Botón para enviar mensajes
    private JButton botonEnviar;

    // Socket y flujos de entrada/salida para la comunicación
    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;

    /**
     * Constructor: Inicializa la interfaz gráfica y conecta al servidor.
     */
    public InterfazCliente() {
        setTitle("Cliente - Chat");
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

        // Conectar al servidor en un hilo separado
        conectarAlServidor();

        setVisible(true);
    }

    /**
     * Conecta al servidor usando sockets y crea un hilo para recibir mensajes.
     */
    private void conectarAlServidor() {
        new Thread(() -> {
            try {
                // Solicita la IP del servidor al usuario
                String ip = JOptionPane.showInputDialog(this, "Ingresa la IP del servidor:", "127.0.0.1");
                socket = new Socket(ip, 12345);
                areaConversacion.append("Conectado al servidor.\n");

                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());

                // Hilo para recibir mensajes del servidor
                new Thread(() -> {
                    try {
                        String mensaje;
                        while ((mensaje = entrada.readUTF()) != null) {
                            areaConversacion.append("Servidor: " + mensaje + "\n");
                        }
                    } catch (IOException ex) {
                        areaConversacion.append("Conexión cerrada.\n");
                    }
                }).start();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar: " + ex.getMessage());
            }
        }).start();
    }

    /**
     * Envía el mensaje escrito al servidor y lo muestra en la conversación.
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
        SwingUtilities.invokeLater(InterfazCliente::new);
    }
}