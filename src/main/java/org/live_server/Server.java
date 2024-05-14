package org.live_server;

import org.live_server.annotations.Controller;
import org.live_server.enumeration.HttpMethod;
import org.live_server.system_object.ServerRequest;
import org.live_server.system_object.ServerRequestUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final int port;

    public Server(int port) {
        this.port = port;
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

            System.out.printf("Running on port %s%n", port);

            // As the server is not live and user needs to restart everytime some is changes
            // There is no need to generate the root path on each iteration.
            Map<String, String> info;
            List<Class<?>> controllerClass = ClassUtil.getAnnotatedClass(Controller.class);
            while (true) {
                try (Socket accept = server.accept()) {

                    ServerRequest instance = ServerRequestUtility.getInstance(accept);
                    String result = (String) ClassUtil.handleRequest(instance,controllerClass);
                    sendResponse(accept.getOutputStream(), result.getBytes());

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
