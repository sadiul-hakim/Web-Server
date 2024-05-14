package org.live_server.system_object;

import java.util.HashMap;
import java.util.Map;

public class ServerRequest {
    private String url;
    private String method;
    private Map<String, Object> params = new HashMap<>();
    private Object body;
    private Map<String, Object> headers = new HashMap<>();

    public ServerRequest(String url, String method, Map<String, Object> params, Object body,
                         Map<String, Object> headers) {
        this.url = url;
        this.method = method;
        this.params = params;
        this.body = body;
        this.headers = headers;
    }

    public ServerRequest() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }
    public Object getParam(String name) {
        return params.get(name);
    }
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Object getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
