package ServidorSocketStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServidorSocketStream {

    //Creo este diccionario porque en la tabla ascii la ñ no está entre los caracteres "normales"
    static HashMap<String, Integer> diccionario;

    static {
        diccionario = new HashMap<>();
        diccionario.put("a", 0);
        diccionario.put("b", 1);
        diccionario.put("c", 2);
        diccionario.put("d", 3);
        diccionario.put("e", 4);
        diccionario.put("f", 5);
        diccionario.put("g", 6);
        diccionario.put("h", 7);
        diccionario.put("i", 8);
        diccionario.put("j", 9);
        diccionario.put("k", 10);
        diccionario.put("l", 11);
        diccionario.put("m", 12);
        diccionario.put("n", 13);
        diccionario.put("ñ", 14);
        diccionario.put("o", 15);
        diccionario.put("p", 16);
        diccionario.put("q", 17);
        diccionario.put("r", 18);
        diccionario.put("s", 19);
        diccionario.put("t", 20);
        diccionario.put("u", 21);
        diccionario.put("v", 22);
        diccionario.put("w", 23);
        diccionario.put("x", 24);
        diccionario.put("y", 25);
        diccionario.put("z", 26);
    }

    public static void main(String[] args) throws IOException {

        //En esta parte se crea el socket servidor
        System.out.println("Creando socket servidor");
        ServerSocket servidor = new ServerSocket();

        //En esta parte se le asigna una ip y un puerto al servidor
        System.out.println("Asignar IP y puerto al servidor");
        //Se crea un objeto del tipo InetSocketAdress que representa la direccion y el puerto
        InetSocketAddress direccionServidor = new InetSocketAddress("localhost", 55555);
        //Se le asigna la direccion y el puerto al servidor que hemos creado antes, el servidor estará esperando conexiones
        //en la direccion localhost y el puerto 55555
        servidor.bind(direccionServidor);

        //En esta parte el servidor estará esperando conexion
        System.out.println("En este punto se esta esperando a que tenga conexion, está listo para aceptar conexiones");
        Socket socketCliente = servidor.accept();
        //Esta línea de código bloquea la ejecución del programa hasta que
        //se establece una conexión entrante. Cuando un cliente se conecta al servidor, el método accept() se
        //desbloquea y devuelve un nuevo objeto Socket que representa la conexión establecida con ese cliente. Este
        //objeto Socket (socketCliente) se utiliza para la comunicación con ese cliente específico.
        //Si sigue ejecutandose el codigo en este punto es porque se ha establecido una conexion
        System.out.println("Se ha aceptado la conexion con el cliente");

        //Flujo de datos entre cliente y servidor
        //Se crean objetos de entrada y salida, que estan asociados al socketCliente, entrada se usa para leer los datos
        //que el cliente envia al servidor y la salida se usa para enviar datos desde el servidor al cliente
        InputStream entrada = socketCliente.getInputStream();
        OutputStream salida = socketCliente.getOutputStream();

        //En esta parte se crea el "objeto" donde se guardaran los datos para comunicarse el servidor.
        //Aqui se almacenaran los datos que el servidor lee del fluejo de entrada asociado al socketCliente
        byte[] buffer= new byte[1024];
        // Mientras haya datos en el flujo de entrada, sigue leyendo
        //El bucle lee los datos del flujo de entrada asociado al socketCliente
        //Lo que hace read(buffer) es leer los datos del flujo de entrada y los almacena en el buffer
        //Si no hay datos que leer devuelve -1
        //bytesRead indica la cantidad de bytes que hay que leer
        int bytesRead;
        while ((bytesRead = entrada.read(buffer)) != -1) {
            //Se crea mensajeCompleto a partir de los bytes en el array buffer, empezando desde
            //el primer byte (índice 0) y tomando tantos bytes como indique bytesRead.
            String mensajeCompleto = new String(buffer, 0, bytesRead).trim();

            // Dividir la cadena usando el delimitador "*||||||*"
            String[] partes = mensajeCompleto.split("\\*\\|\\*");

            if (partes.length == 2) {
                String mensajeRecibido = partes[0];
                String modoUsoRecibido = partes[1];

                // Ahora puedes utilizar mensajeRecibido y modoUsoRecibido en tu lógica
                respuesta(mensajeRecibido, modoUsoRecibido, salida);
            } else {
                // Manejar caso donde el formato no es el esperado
                System.out.println("Formato no válido: " + mensajeCompleto);
            }

            // Limpia el buffer para la próxima lectura
            buffer = new byte[1024];
        }

        //En esta parte es para leer dichos datos, mientras tenga algo que leer
        System.out.println("Cerramos conexion SUBSOCKET del cliente");
        socketCliente.close();

        System.out.println("Cerramos Socket servidor");
        servidor.close();
        System.out.println("Finalizada");

    }


    public static void respuesta(String mensaje, String modoFuncionamiento, OutputStream salida) throws IOException{

        String respuesta;
        switch (modoFuncionamiento) {
            case "codificar":
                respuesta = codificarMensaje(mensaje);
                break;
            case "descodificar":
                respuesta = descodificarMensaje(mensaje);
                break;
            default:
                respuesta = "No hay ninguna opción con ese nombre!!";
                break;
        }
        salida.write(respuesta.getBytes());
    }

    private static String codificarMensaje(String mensaje) {
        StringBuilder respuesta = new StringBuilder();
        StringBuilder mensajeCodificado = new StringBuilder();

        for (int i = 0; i < mensaje.length(); i++) {
            char letraActual = mensaje.charAt(i);
            boolean esMayus = Character.isUpperCase(letraActual);

            // Convertir la letra a minúsculas
            char letraMinusculas = Character.toLowerCase(letraActual);

            // Verificar caracteres especiales 'x', 'y', 'z'
            if (letraMinusculas == 'x') {
                mensajeCodificado.append('a');
            } else if (letraMinusculas == 'y') {
                mensajeCodificado.append('b');
            } else if (letraMinusculas == 'z') {
                mensajeCodificado.append('c');
            } else {
                // Verificar si la letra está en el diccionario
                if (diccionario.containsKey(String.valueOf(letraMinusculas))) {
                    int valorLetraActual = diccionario.get(String.valueOf(letraMinusculas)) + 3;
                    String letraCodificada = encontrarClave(diccionario, valorLetraActual);

                    // Verificar si se encontró la clave
                    if (letraCodificada != null) {
                        mensajeCodificado.append(letraCodificada);
                    } else {
                        // No se encontró la clave, mantener la letra original sin codificar
                        mensajeCodificado.append(letraActual);
                    }
                } else {
                    // La letra no está en el diccionario, mantenerla sin codificar
                    mensajeCodificado.append(letraActual);
                }
            }

            if (esMayus) {
                char ultimoCaracter = mensajeCodificado.charAt(mensajeCodificado.length() - 1);
                // Convertir la última letra a mayúsculas y actualizar el StringBuilder
                mensajeCodificado.setCharAt(mensajeCodificado.length() - 1, Character.toUpperCase(ultimoCaracter));
            }

            respuesta.append(mensajeCodificado);
            // Reiniciar el StringBuilder para la siguiente palabra
            mensajeCodificado.setLength(0);
        }

        return respuesta.toString();
    }

    private static String descodificarMensaje(String mensaje) {
        StringBuilder respuesta = new StringBuilder();
        StringBuilder mensajeDescodificado = new StringBuilder();

        for (int i = 0; i < mensaje.length(); i++) {
            char letraActual = mensaje.charAt(i);
            boolean esMayus = Character.isUpperCase(letraActual);

            // Convertir la letra a minúsculas
            char letraMinusculas = Character.toLowerCase(letraActual);

            // Verificar caracteres especiales 'x', 'y', 'z'
            if (letraMinusculas == 'a') {
                mensajeDescodificado.append('x');
            } else if (letraMinusculas == 'b') {
                mensajeDescodificado.append('y');
            } else if (letraMinusculas == 'c') {
                mensajeDescodificado.append('z');
            } else {
                // Verificar si la letra está en el diccionario
                if (diccionario.containsKey(String.valueOf(letraMinusculas))) {
                    int valorLetraActual = diccionario.get(String.valueOf(letraMinusculas)) - 3;
                    String letraCodificada = encontrarClave(diccionario, valorLetraActual);

                    // Verificar si se encontró la clave
                    if (letraCodificada != null) {
                        mensajeDescodificado.append(letraCodificada);
                    } else {
                        // No se encontró la clave, mantener la letra original sin codificar
                        mensajeDescodificado.append(letraActual);
                    }
                } else {
                    // La letra no está en el diccionario, mantenerla sin codificar
                    mensajeDescodificado.append(letraActual);
                }
            }
            if (esMayus) {
                char ultimoCaracter = mensajeDescodificado.charAt(mensajeDescodificado.length() - 1);
                // Convertir la última letra a mayúsculas y actualizar el StringBuilder
                mensajeDescodificado.setCharAt(mensajeDescodificado.length() - 1, Character.toUpperCase(ultimoCaracter));
            }

            respuesta.append(mensajeDescodificado);
            // Reiniciar el StringBuilder para la siguiente palabra
            mensajeDescodificado.setLength(0);
        }
        return respuesta.toString();
    }

    private static String encontrarClave(Map<String, Integer> diccionario, int valorBuscado) {
        for (Map.Entry<String, Integer> entry : diccionario.entrySet()) {
            if (entry.getValue() == valorBuscado) {
                // Devuelve la clave cuando se encuentra el valor
                return entry.getKey();
            }
        }
        // Devuelve null si no se encuentra el valor
        return null;
    }
}
