package org.live_server;

import org.live_server.util.FileUtil;
import org.live_server.util.RequestUtil;
import org.live_server.util.ResponseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String rootFolderText = "static";

    public Server() {
    }

    public static void run(int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Running on port : " + port);

            URL resource = Server.class.getResource("/" + rootFolderText);
            assert resource != null;

            Path rootPath = Path.of(resource.toURI());
            if (!Files.exists(rootPath)) {
                throw new RuntimeException("Could not find root path (/static)!");
            }

            // As the server is not live and user needs to restart everytime some is changes
            // There is no need to generate the root path on each iteration.
            ConcurrentHashMap<String, Path> pathConcurrentHashMap = FileUtil.generatePath(rootPath, rootFolderText);
            while (true) {

                try (Socket connection = server.accept()) {
                    server(connection, pathConcurrentHashMap);
                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, ex.getMessage());
                    break;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
    }

    private static void server(Socket connection, ConcurrentHashMap<String, Path> pathConcurrentHashMap) throws IOException {

        String requestUrl = RequestUtil.requestedUrl(connection);
        Path resultPath = pathConcurrentHashMap.get(requestUrl);

        // If the request url does not contain '.' that means it is not a file
        if (resultPath != null && requestUrl.contains(".")) {
            ResponseUtil.sendResponse(connection.getOutputStream(), pathConcurrentHashMap.get(requestUrl));
        } else {
            ResponseUtil.sendResponse(connection.getOutputStream(), "404 Not Found".getBytes());
        }
    }
}
