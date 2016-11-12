package com.rev.facade;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class AbstractHttpExecutor {

    private HttpContext httpContext = null;
    private boolean keepClientOpen = false;

    /**
     * Template uses this method to procure an {@link HttpClient}.
     *
     * @return {@link CloseableHttpClient} used to make HTTP calls
     */
    protected abstract CloseableHttpClient getHttpClient();

    /**
     * Template uses this method to handle the response and determine what should be returned by the execute method.
     *
     * @param httpResponse {@link HttpResponse} to be consumed.
     * @return
     */
    protected abstract String handleResponse(HttpResponse httpResponse);

    /**
     * Execute an HTTP Request and return a String response as handled by {@link AbstractHttpExecutor#handleResponse
     * (HttpResponse)}
     *
     * @param request {@link HttpUriRequest} to be executed
     * @return String representation as determined by handleResponse
     */
    public String execute(HttpUriRequest request) {
        try {
            return getClientAndExecute(request);
        } catch (Exception e) {
            throw new RuntimeException("Error executing: " + request.getMethod() + " " + request.getURI(), e);
        }
    }

    /**
     * Set the httpContext (otherwise uses new BasicHttpContext()).
     *
     * @param httpContext to set.
     * @return instance of {AbstractHttpExecutor}. Enables chaining.
     */
    public AbstractHttpExecutor context(HttpContext httpContext) {
        this.httpContext = httpContext;
        return this;
    }

    /**
     * Instruct the executor to keep the {@link HttpClient} open after execution.
     *
     * @return instance of {AbstractHttpExecutor}. Enables chaining.
     */
    public AbstractHttpExecutor keepClientOpen() {
        this.keepClientOpen = true;
        return this;
    }


    private String getClientAndExecute(HttpUriRequest request) throws IOException {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = getHttpClient();
            return executeAndReturnResponse(request, httpClient);
        } finally {
            if (!keepClientOpen && httpClient != null) {
                httpClient.close();
            }
        }
    }

    private String executeAndReturnResponse(HttpUriRequest request, CloseableHttpClient httpClient) throws IOException {
        CloseableHttpResponse response = httpClient.execute(request, getHttpContext());
        String responseText = handleResponse(response);
        EntityUtils.consume(response.getEntity());
        return responseText;
    }

    /**
     * Transform HTTP Response Body into a String. Transforms IOExceptions to runtime *and* closes stream safely.
     *
     * @param httpResponse to be transformed
     * @return String representation of the response body
     */
    protected String getResponse(HttpResponse httpResponse) {
        try {
            return getResponseMightThrowIOException(httpResponse);
        } catch (IOException e) {
            throw new RuntimeException("Error transforming response body to string.", e);
        }
    }

    private String getResponseMightThrowIOException(HttpResponse httpResponse) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent()), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            builder.append(output);
        }
        String unescapedJson = StringEscapeUtils.unescapeJson(builder.toString());
        return unescapedJson.replaceAll("^\"|\"$", "");
    }

    private HttpContext getHttpContext() {
        if (this.httpContext != null) {
            this.httpContext = new BasicHttpContext();
        }
        return httpContext;
    }
}
