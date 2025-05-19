import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public void iniciar() {
        try {
            socket = new Socket("192.168.100.37", 12345);
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
        new Cliente().iniciar();
    }
}
