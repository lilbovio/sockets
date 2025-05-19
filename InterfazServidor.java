import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class InterfazServidor extends JFrame {
    private JTextArea areaConversacion;
    private JTextField campoMensaje;
    private JButton botonEnviar;

    private ServerSocket servidor;
    private Socket cliente;
    private DataOutputStream salida;
    private DataInputStream entrada;

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

        botonEnviar.addActionListener(e -> enviarMensaje());

        iniciarServidor();

        setVisible(true);
    }

    private void iniciarServidor() {
        new Thread(() -> {
            try {
                servidor = new ServerSocket(12345);
                areaConversacion.append("Servidor iniciado en puerto 12345\nEsperando conexión...\n");
                cliente = servidor.accept();
                areaConversacion.append("Cliente conectado desde: " + cliente.getInetAddress() + "\n");

                entrada = new DataInputStream(cliente.getInputStream());
                salida = new DataOutputStream(cliente.getOutputStream());

                // Hilo para recibir mensajes
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
        SwingUtilities.invokeLater(InterfazServidor::new);
    }
}
