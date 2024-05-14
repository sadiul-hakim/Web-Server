package org.live_server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.live_server.enumeration.ResponseType;
import org.live_server.annotations.Controller;
import org.live_server.annotations.GET;
import org.live_server.system_object.ServerRequest;

import java.util.Collections;

@Controller
public class HomeController {
    private final ObjectMapper mapper = new ObjectMapper();
    @GET(path = "/home",produces = ResponseType.JSON)
    public String home(ServerRequest request){
        try {
            return mapper.writeValueAsString(Collections.singletonMap("message",STR."Welcome, \{request.getParam("name")}"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GET(path = "/greet")
    public String greet(ServerRequest request){
        try {
            return mapper.writeValueAsString(Collections.singletonMap("message","Hello Hakim"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}