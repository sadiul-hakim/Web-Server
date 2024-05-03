package org.live_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Server {
    private static final String rootFolderText = "static";

    public static void start(int port) {
        try (ServerSocket server = new ServerSocket(port)) {

            System.out.println(STR."Running on port \{port}");
            URL resource = Server.class.getResource(STR."/\{rootFolderText}");
            assert resource != null;

            while (true) {
                try (Socket accept = server.accept()) {
                    Path rootPath = Path.of(resource.toURI());
                    if(!Files.exists(rootPath)){
                        sendResponse(accept.getOutputStream(),"404 Not Found!");
                    }

                    Map<String, Path> pathMap = generatePath(rootPath);

                    String requestUrl = requestedUrl(accept);

                    Path resultPath = pathMap.get(requestUrl);

                    if (resultPath != null) {
                        sendResponse(accept.getOutputStream(), pathMap.get(requestUrl));
                    } else {
                        sendResponse(accept.getOutputStream(), "404 Not Found");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String requestedUrl(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read the first line of the HTTP request (request line)
        String requestLine = in.readLine();

        // Extract the requested URL from the request line
        String[] requestLineParts = requestLine.split("\\s+");
        if (requestLineParts.length >= 2) {
            return requestLineParts[1].replaceFirst("/", "")
                    .replaceFirst("\\\\", "");
        }

        return "";
    }

    private static Map<String, Path> generatePath(Path rootPath) {

        Map<String, Path> pathMap = new HashMap<>();
        try (Stream<Path> pathStream = Files.walk(rootPath)) {
            List<Path> pathList = pathStream.toList();
            pathList.forEach(path -> {

                if (path.toString().endsWith(rootFolderText)) {
                    return;
                }

                int rootFolderIndex = path.toString().indexOf(rootFolderText);
                String nextToStatic = path.toString()
                        .substring(rootFolderIndex + (rootFolderText.length()) + 1);

                String[] split = nextToStatic.split("\\\\");
                StringBuilder pathBuilder = new StringBuilder();

                for (String s : split) {
                    pathBuilder.append("/")
                            .append(s);
                }

                pathMap.put(pathBuilder.toString().replaceFirst("/", ""), path);
            });
            return pathMap;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private static void sendResponse(OutputStream outputStream, Path path) throws IOException {

        byte[] bytes = Files.readAllBytes(path);
        sendResponse(outputStream, bytes);
    }

    private static void sendResponse(OutputStream outputStream, String text) throws IOException {

        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.write(text.getBytes());
        outputStream.flush();
    }

    private static void sendResponse(OutputStream outputStream, byte[] bytes) throws IOException {

        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }
}
