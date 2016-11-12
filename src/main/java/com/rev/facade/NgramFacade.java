package com.rev.facade;

import com.rev.beans.Check;
import com.rev.beans.Response;
import com.rev.util.JsonUtil;

import java.io.IOException;

public class NgramFacade extends ServiceBase {

    private String endpoint;

    public NgramFacade(String endpoint) {
        this.endpoint = endpoint;
    }

    public Response checkForMatches(Check check) throws IOException {
        String json = new JsonUtil().getNgramQuery(check);
        String responseJson = executeHttpPost(getUri(endpoint), json);
        return new JsonUtil().getResponse(responseJson);
    }
}
