import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        FTPClient cliente=new FTPClient();
        String serverFTP="192.168.1.31";
        boolean anonimo = false;
        boolean conectado = false;

        String nombreUsu;
        String pass;
        System.out.println("Nos vamos a conectar a "+serverFTP);
        try {
            cliente.connect(serverFTP);
            System.out.println(cliente.getReplyString());
            int codigo=cliente.getReplyCode();
            System.out.println("Código:"+codigo);

            System.out.print("Introduce el nombre de usuario: ");
            nombreUsu = sc.next();
            System.out.print("\nIntroduce la contraseña: ");
            pass = sc.next();

            while (!conectado) {
                if (!FTPReply.isPositiveCompletion(codigo)) {
                    System.err.println("Conexión rechazada");
                } else {
                    if (nombreUsu.equals("anonymous")) {
                        anonimo = true;
                        pass = "";
                    }

                    if (cliente.login(nombreUsu, pass)) {
                        conectado = true;
                        System.out.println("Usuario conectado");
                    } else {
                        System.err.println("Usuario incorrecto");
                    }
                }
            }

            System.out.println("¿Que deseas hacer?" +
                    "\n\t0-Subir archivo" +
                    "\n\t1-Descargar archivo" +
                    "\n\t2-Listar archivos" +
                    "\n\t3-Salir");

            int opcion = sc.nextInt();
            boolean exit = false;
            while(!exit) {
                switch (opcion) {
                    case 0:
                        if (!anonimo) {
                            cliente.setFileType(FTPClient.BINARY_FILE_TYPE);

                            System.out.println("¿Que archivo quieres descargarte?(Indicar también la extensión)");
                            String nombre = sc.next();

                            BufferedOutputStream bus = new BufferedOutputStream(new FileOutputStream(nombre));
                            if (cliente.retrieveFile(nombre, bus))
                                System.out.println("Se ha descargado el archivo correctamente");
                            else
                                System.err.println("Ha habido un error en la descarga del archivo, compruebe el nombre del archivo");
                            bus.close();
                        }
                        System.out.println("¿Que deseas hacer?" +
                                "\n\t0-Subir archivo" +
                                "\n\t1-Descargar archivo" +
                                "\n\t2-Listar archivos" +
                                "\n\t3-Salir");

                        opcion = sc.nextInt();
                        break;
                    case 1:
                        try {
                            cliente.setFileType(FTPClient.BINARY_FILE_TYPE);

                            System.out.println("¿Como se llama el archivo que vas a subir?(Indicar también la extensión)");
                            String archivo = sc.next();

                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo));

                            cliente.storeFile(archivo, bis);
                        } catch (IOException e) {
                            System.err.println("No existe el archivo");
                        }
                        System.out.println("¿Que deseas hacer?" +
                                "\n\t0-Subir archivo" +
                                "\n\t1-Descargar archivo" +
                                "\n\t2-Listar archivos" +
                                "\n\t3-Salir");

                        opcion = sc.nextInt();
                        break;
                    case 2:
                        cliente.enterLocalPassiveMode();
                        FTPFile[] archivos = cliente.listFiles();
                        System.out.println(cliente.printWorkingDirectory());
                        for (FTPFile a : archivos) {
                            System.out.println("\t" + a.getName() + " => " + a.getType());
                        }
                        System.out.println("¿Que deseas hacer?" +
                                "\n\t0-Subir archivo" +
                                "\n\t1-Descargar archivo" +
                                "\n\t2-Listar archivos" +
                                "\n\t3-Salir");

                        opcion = sc.nextInt();
                        break;
                    case 3:
                        exit = true;
                        System.out.println("Saliendo de la conexión");
                        break;
                    default:
                        System.err.println("Opción no valida");
                        System.out.println("¿Que deseas hacer?" +
                                "\n\t0-Subir archivo" +
                                "\n\t1-Descargar archivo" +
                                "\n\t2-Listar archivos" +
                                "\n\t3-Salir");

                        opcion = sc.nextInt();
                }
            }
            if (cliente.logout())
                System.out.println("Logout del servidor");
            else
                System.err.println("Error al hacer logout");

            cliente.disconnect();
            System.out.println("Fin de la conexión");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
