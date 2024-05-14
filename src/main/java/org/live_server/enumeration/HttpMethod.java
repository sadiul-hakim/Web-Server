package org.live_server.enumeration;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod of(String method){
        for(HttpMethod value:values()){
            if(value.name().equals(method)){
                return value;
            }
        }

        return null;
    }
}
