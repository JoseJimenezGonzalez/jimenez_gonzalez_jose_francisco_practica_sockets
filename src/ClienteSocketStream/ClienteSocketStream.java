package ClienteSocketStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClienteSocketStream {

    public static void main(String[] args) throws IOException {
        Scanner teclado = new Scanner(System.in);

        System.out.println("Creando el socket cliente");
        Socket cliente = new Socket();

        System.out.println("Establecemos conexion");
        //Le ponemos los datos que hemos introducido en el servidor
        InetSocketAddress dircServidor = new InetSocketAddress ("localhost", 55555);
        cliente.connect(dircServidor);

        //Creamos el flujo de salida
        OutputStream salida = cliente.getOutputStream();
        InputStream entrada = cliente.getInputStream();


        System.out.println("Ya se puede empezar a mandar mensajes");
        System.out.println("Introduzaca el numero entre corchete según la opción que desee");
        System.out.println("¿Qué desea hacer?\n[0] Mandar mensaje\n[1] Salir del programa");
        int opcion  = teclado.nextInt();
        teclado.nextLine();
        while (opcion == 0){
            System.out.println("¿Qué desea hacer?\n[0] Codificar mensaje\n[1] Descodificar mensaje");
            opcion = teclado.nextInt();
            teclado.nextLine();
            String mensaje;
            String mensajeCompleto;
            byte[] respuesta;
            String mensajeFinal;
            switch (opcion){
                case 0:
                    System.out.println("Introduce el mensaje que deseas codificar");
                    mensaje = teclado.nextLine();
                    mensajeCompleto = agregarSeparador(mensaje) + "codificar";
                    // enviamos la prengunta al servidor
                    salida.write(mensajeCompleto.getBytes());
                    respuesta = new byte[1024];
                    // almacenamos la respuesta del servidor
                    entrada.read(respuesta);
                    mensajeFinal = new String(respuesta).trim();
                    System.out.println("Mensaje inicial:" + mensaje);
                    System.out.println("Respuesta recibida(codificada): " + new String(mensajeFinal).trim());
                    break;
                case 1:
                    System.out.println("Introduce el mensaje que deseas descodificar");
                    mensaje = teclado.nextLine();
                    mensajeCompleto = agregarSeparador(mensaje) + "descodificar";
                    // enviamos la prengunta al servidor
                    salida.write(mensajeCompleto.getBytes());
                    respuesta = new byte[1024];
                    // almacenamos la respuesta del servidor
                    entrada.read(respuesta);
                    mensajeFinal = new String(respuesta).trim();
                    System.out.println("Mensaje inicial:" + mensaje);
                    System.out.println("Respuesta recibida(descodificada): " + new String(mensajeFinal).trim());
                    break;
                default:
                    mensajeFinal = "Opción no reconocida";
                    System.out.println(mensajeFinal);
                    break;
            }
            System.out.println("¿Qué desea hacer?\n[0] Mandar mensaje\n[1] Salir del programa");
            opcion  = teclado.nextInt();
        }


        System.out.println("Cerrando el socket Cliente");
        cliente.close();
        System.out.println("Conexión socket Cliente cerrada");

    }
    private static String agregarSeparador(String mensaje){
        return mensaje + "*|*";
    }
}
