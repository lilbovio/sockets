import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Servidor {
    private ServerSocket servidor;
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public void iniciar() {
        try {
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

            // Enviar mensajes desde consola
            Scanner sc = new Scanner(System.in);
            while (true) {
                String mensaje = sc.nextLine();
                salida.writeUTF(mensaje);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
