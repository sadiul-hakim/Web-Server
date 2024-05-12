package org.live_server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.live_server.ResponseType;
import org.live_server.annotations.Controller;
import org.live_server.annotations.GET;

import java.util.List;

@Controller
public class PageController {
    private final ObjectMapper mapper = new ObjectMapper();

    @GET(path = "/pages", produces = ResponseType.JSON)
    public String pages() {
        List<String> pages = List.of("Page1", "Page2", "Page3");
        try {
            return mapper.writeValueAsString(pages);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
