package com.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Server {
    private String[] arrayString;
    private DataOutputStream out;
    private String stringaRicevuta;
    private File f;
    private ServerSocket server;
    private BufferedReader in;
    private Socket s;

    public Server() {
        try {
            server = new ServerSocket(8080);
            System.out.println("Server in ascolto sulla porta 8080");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nella creazione del server");
        }
    }

    public void start() {
        Boolean exit = false;
        try {
            while (!exit) {
                System.out.println("Server Started");
                this.s = server.accept();
                this.in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
                this.out = new DataOutputStream(new DataOutputStream(s.getOutputStream()));
                try {
                    stringaRicevuta = in.readLine();
                    System.out.println(stringaRicevuta);
                    if (stringaRicevuta != null && !stringaRicevuta.isEmpty()) {
                        this.arrayString = stringaRicevuta.split(" ");
                        if (arrayString.length == 3 && arrayString[2].contains("HTTP")) {
                            f = new File("htdocs/" + arrayString[1]);
                            if (f.exists()) {
                                if (arrayString[1].split("\\.")[1].equals("jpeg")) {
                                    invioImmagine(f);
                                } else {
                                    String textFile = readFile(f, arrayString[1].split("\\.")[1]);
                                    sendResponse("200", "OK", textFile);
                                }
                            } else {
                                File fileErrore = new File("htdocs/404.html");
                                String fErrore = readFile(fileErrore, "html");
                                sendResponse("404", "Not Found", fErrore);
                                System.out.println("Errore: file non trovato");
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Errore generico " + e.getMessage());
                    s.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Errore generico " + e.getMessage());
        }
        try {
            s.close();
        } catch (IOException e) {
            System.out.println("Errore nella chiusura del socket");
        }
    }

    public static String readFile(File f, String ex) throws IOException {
        StringBuilder content = new StringBuilder();
        if (ex.equals("html") || ex.equals("htm") || ex.equals("css")) {
            try {
                Scanner myReader = new Scanner(f);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    content.append(data);
                    System.out.println(data);
                }
                myReader.close();
            } catch (Exception e) {
                System.out.println("Errore");
            }
        }
        return content.toString();
    }

    public void sendResponse(String statusCode, String statusPhrase, String body) {
        try {
            out.writeBytes("HTTP/1.1 " + statusCode + " " + statusPhrase + "\r\n");
            System.out.println("HTTP/1.1 " + statusCode + " " + statusPhrase + "\r\n");
            out.writeBytes("Date: " + LocalDateTime.now().toString() + "\r\n");
            System.out.println("Date: " + LocalDateTime.now().toString() + "\r\n");
            out.writeBytes("Server: server + '\n");
            System.out.println("Server: server + '\n");
            out.writeBytes("Content-Type: text/html;charset=UTF-8\r\n");
            System.out.println("Content-Type: text/html;charset=UTF-8\r\n");
            out.writeBytes("Content-Length: " + body.length() + "\n");
            System.out.println("Content-Length: " + body.length() + "\n");
            out.writeBytes("\n");
            out.writeBytes(body);
            out.writeBytes("\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Errore risposta");
        }
    }

    public void close() {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void invioImmagine(File f) throws IOException {
        out.writeBytes("HTTP/1.1 200 OK\r\n");
        System.out.println("HTTP/1.1 200 OK\r\n");
        out.writeBytes("Date: " + LocalDateTime.now().toString() + "\r\n");
        System.out.println("Date: " + LocalDateTime.now().toString() + "\r\n");
        out.writeBytes("Server: server + '\n");
        System.out.println("Server: server + '\n");
        out.writeBytes("Content-Type: image/jpeg\r\n");
        System.out.println("Content-Type: image/jpeg\r\n");
        out.writeBytes("Content-Length: " + f.length() + "\n");
        System.out.println("Content-Length: " + f.length() + "\n");
        out.writeBytes("\n");
        InputStream in = new FileInputStream(f);
        byte[] buffer = new byte[98304];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        in.close();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
