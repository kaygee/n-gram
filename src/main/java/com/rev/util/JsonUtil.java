package com.rev.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.beans.Check;
import com.rev.beans.Response;

import java.io.IOException;

public class JsonUtil {

    public JsonUtil() {
    }

    public String getNgramQuery(Check check) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(check);
    }

    public Response getResponse(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Response.class);
    }
}
