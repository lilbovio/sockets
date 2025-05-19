import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Servidor
 * 
 * Esta clase implementa un servidor de consola para comunicarse con un cliente usando sockets.
 * Permite enviar y recibir mensajes en tiempo real utilizando hilos.
 */
public class Servidor {
    // ServerSocket para aceptar conexiones y flujos de entrada/salida
    private ServerSocket servidor;
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;

    /**
     * Inicia el servidor, acepta la conexión de un cliente y gestiona la comunicación.
     */
    public void iniciar() {
        try {
            // Iniciar el servidor en el puerto 5000
            servidor = new ServerSocket(5000);
            System.out.println("Servidor esperando conexión...");
            socket = servidor.accept();
            System.out.println("Cliente conectado");

            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());

            // Hilo para leer mensajes del cliente
            new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readUTF()) != null) {
                        System.out.println("Cliente dice: " + mensaje);
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
     * Método principal para iniciar el servidor.
     */
    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}