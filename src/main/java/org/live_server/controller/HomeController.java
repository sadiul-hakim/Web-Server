package org.live_server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.live_server.ResponseType;
import org.live_server.annotations.Controller;
import org.live_server.annotations.GET;

import java.util.Collections;

@Controller
public class HomeController {
    private final ObjectMapper mapper = new ObjectMapper();
    @GET(path = "/home",produces = ResponseType.JSON)
    public String home(){
        try {
            return mapper.writeValueAsString(Collections.singletonMap("message","Welcome, Home!"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}