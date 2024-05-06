package org.live_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String rootFolderText = "static";
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    private String requestedUrl(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Extract the requested URL from the request first line
        String[] requestLineParts = bufferedReader.readLine().split("\\s+");
        if (requestLineParts.length >= 2) {
            return requestLineParts[1].replaceFirst("/", "")
                    .replaceFirst("\\\\", "");
        }

        return "";
    }

    private Map<String, Path> generatePath(Path rootPath) {

        Map<String, Path> pathMap = new HashMap<>();
        try (Stream<Path> pathStream = Files.walk(rootPath)) {
            pathStream.toList().forEach(path -> {

                if (path.toString().endsWith(rootFolderText)) {
                    return;
                }
                int rootFolderIndex = path.toString().indexOf(rootFolderText);
                String nextToStatic = path.toString()
                        .substring(rootFolderIndex + (rootFolderText.length()) + 1);
                String[] actualFolderPath = nextToStatic.split("\\\\");
                StringBuilder pathBuilder = new StringBuilder();
                for (String s : actualFolderPath) {
                    pathBuilder.append("/")
                            .append(s);
                }
                if (!pathMap.containsKey(pathBuilder.toString().replaceFirst("/", ""))) {
                    pathMap.put(pathBuilder.toString().replaceFirst("/", ""), path);
                }
            });
            return pathMap;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
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

            System.out.println(STR."Running on port \{port}");
            URL resource = Server.class.getResource(STR."/\{rootFolderText}");
            assert resource != null;

            Path rootPath = Path.of(resource.toURI());

            // As the server is not live and user needs to restart everytime some is changes
            // There is no need to generate the root path on each iteration.
            Map<String, Path> pathMap = generatePath(rootPath);;
            String requestUrl;
            Path resultPath;
            while (true) {
                try (Socket accept = server.accept()) {
                    if (!Files.exists(rootPath)) {
                        sendResponse(accept.getOutputStream(), "404 Not Found!".getBytes());
                    }
                    requestUrl = requestedUrl(accept);
                    resultPath = pathMap.get(requestUrl);

                    // If the request url does not contain '.' that means it is not a file
                    if (resultPath != null && requestUrl.contains(".")) {
                        sendResponse(accept.getOutputStream(), pathMap.get(requestUrl));
                    } else {
                        sendResponse(accept.getOutputStream(), "404 Not Found".getBytes());
                    }
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
