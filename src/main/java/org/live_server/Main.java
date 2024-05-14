package org.live_server;

public class Main {
    public static void main(String[] args) {

        Thread serverThread = new Thread(new Server(9095));
        serverThread.setName("#Server Thread");
        serverThread.start();
    }
}