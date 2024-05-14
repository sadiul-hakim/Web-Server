package org.live_server.system_object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerRequestUtility {
    public static ServerRequest getInstance(Socket socket) throws Exception {
        return requestInfo(socket);
    }

    private static ServerRequest requestInfo(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ServerRequest request = new ServerRequest();

        // Read the request line
        String requestLine = bufferedReader.readLine();
        if (requestLine != null) {
            String[] requestLineParts = requestLine.split("\\s+");
            if (requestLineParts.length >= 2) {
                String url = requestLineParts[1];
                request.setMethod(requestLineParts[0]);

                if (url.contains("?")) {
                    request.setUrl(url.substring(0, url.indexOf("?")));
                } else {
                    request.setUrl(url);
                }

                request.getParams().putAll(parseParams(url));
            }
        }

        // Read and parse headers
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                request.getHeaders().put(headerName, headerValue);
            }
        }

        return request;
    }

    private static Map<String, Object> parseParams(String url) {
        Map<String, Object> params = new HashMap<>();

        if (!url.contains("?")) {
            return params;
        }

        String paramsText = url.substring(url.indexOf("?") + 1);
        String[] paramsArr = paramsText.split("\\?");
        Arrays.stream(paramsArr).forEach(param -> {
            params.put(param.substring(0, param.indexOf("=")), param.substring(param.indexOf("=") + 1));
        });

        return params;
    }
}
