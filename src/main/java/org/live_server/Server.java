package org.live_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    // This method returns requested url
    private String requestedUrl(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Extract the requested URL from the request first line
        String[] requestLineParts = bufferedReader.readLine().split("\\s+");
        if (requestLineParts.length >= 2) {
            return requestLineParts[1];
        }

        return "";
    }

    private void sendResponse(OutputStream outputStream, Path path) throws IOException {

        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            sendResponse(outputStream, bytes);
        } else {
            sendResponse(outputStream, "404 Not Found!".getBytes());
        }
    }

    private void sendResponse(OutputStream outputStream, byte[] bytes) throws IOException {

        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {

            System.out.printf("Running on port %s%n",port);

            // As the server is not live and user needs to restart everytime some is changes
            // There is no need to generate the root path on each iteration.
            String requestUrl;
            while (true) {
                try (Socket accept = server.accept()) {

                    requestUrl = requestedUrl(accept);
                    String result = (String) ClassUtil.handleRequest(requestUrl);
                    sendResponse(accept.getOutputStream(),result.getBytes());

                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, ex.getMessage());
                    break;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
    }
}
