package org.live_server;

public class Main {
    public static void main(String[] args) {

        Thread serverThread = new Thread(new Server(8085));
        serverThread.setName("#Server Thread");
        serverThread.start();
    }
}