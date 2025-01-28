package org.live_server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestUtil {
    private RequestUtil() {
    }

    // This method returns requested url
    public static String requestedUrl(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Extract the requested URL from the request first line
        String line = bufferedReader.readLine();
        if (line == null)
            return "";
        String[] requestLineParts = line.split("\\s+");
        if (requestLineParts.length >= 2) {
            return requestLineParts[1].replaceFirst("/", "")
                    .replaceFirst("\\\\", "");
        }

        return "";
    }
}
