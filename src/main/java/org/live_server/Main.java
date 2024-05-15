package org.live_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class Main {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {
        try {
            var server = HttpServer.create(new InetSocketAddress(9095),0);
            System.out.println("Server is running on port : 9095");

            var context = server.createContext("/");
            context.setHandler(Main::handleRequests);
            server.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleRequests(HttpExchange exchange) throws IOException{
        String response = "<h1 style='background:crimson;color:white;text-align:center;padding:20px;'>Welcome To the Home Page</h1>";
        exchange.sendResponseHeaders(200,response.getBytes().length);
        exchange.setAttribute("Content-Type","text/html");
        var os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}