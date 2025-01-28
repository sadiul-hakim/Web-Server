package org.live_server.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseUtil {

    private ResponseUtil(){}

    public static void sendResponse(OutputStream outputStream, byte[] bytes) throws IOException {

        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
        outputStream.write("Cache-Control: no-cache, no-store, must-revalidate\r\n".getBytes());  // Prevent caching
        outputStream.write("Pragma: no-cache\r\n".getBytes());  // HTTP 1.0
        outputStream.write("Expires: 0\r\n".getBytes());  // Proxies
        outputStream.write("\r\n".getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }

    public static void sendResponse(OutputStream outputStream, Path path) throws IOException {

        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            sendResponse(outputStream, bytes);
        } else {
            sendResponse(outputStream, "404 Not Found!".getBytes());
        }
    }
}
