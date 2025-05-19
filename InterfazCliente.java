import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class InterfazCliente extends JFrame {
    private JTextArea areaConversacion;
    private JTextField campoMensaje;
    private JButton botonEnviar;

    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;

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

        botonEnviar.addActionListener(e -> enviarMensaje());

        conectarAlServidor();

        setVisible(true);
    }

    private void conectarAlServidor() {
        new Thread(() -> {
            try {
                String ip = JOptionPane.showInputDialog(this, "Ingresa la IP del servidor:", "127.0.0.1");
                socket = new Socket(ip, 12345);
                areaConversacion.append("Conectado al servidor.\n");

                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());

                // Hilo para recibir mensajes
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazCliente::new);
    }
}
