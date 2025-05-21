import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Cliente
 * 
 * Esta clase implementa un cliente de consola para comunicarse con un servidor usando sockets.
 * Permite enviar y recibir mensajes en tiempo real utilizando hilos.
 */
public class Cliente {
    // Socket y flujos de entrada/salida para la comunicación
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;

    /**
     * Inicia la conexión con el servidor y gestiona el envío y recepción de mensajes.
     */
    public void iniciar() {
        try {
            // Conectar al servidor en la IP y puerto especificados
            socket = new Socket("", 12345);
            System.out.println("Conectado al servidor");

            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());

            // Hilo para leer mensajes del servidor
            new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readUTF()) != null) {
                        System.out.println("Servidor dice: " + mensaje);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada.");
                }
            }).start();

            // Enviar mensajes desde la consola
            Scanner sc = new Scanner(System.in);
            while (true) {
                String mensaje = sc.nextLine();
                salida.writeUTF(mensaje);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal para iniciar el cliente.
     */
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
}