package com.rev.facade;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class ServiceBase {

    private static final Logger LOGGER = Logger.getLogger(ServiceBase.class);

    protected String uri;

    public ServiceBase() {
        this.uri = "";
    }

    private HttpClient getHttpClientTrustingAllSSLCertsQuietly() {
        try {
            return getHttpClientTrustingAllSSLCerts();
        } catch (Exception e) {
            throw new RuntimeException("While obtaining the HTTP client", e);
        }
    }

    protected String executeHttpPost(final URI uri, final String json) {
        LOGGER.debug("executeHttpPost: uri " + uri.toString());

        final HttpPost httpPost = new HttpPost(uri);

        return new AbstractHttpExecutor() {
            @Override
            protected CloseableHttpClient getHttpClient() {

                CloseableHttpClient client = (CloseableHttpClient) getHttpClientTrustingAllSSLCertsQuietly();

                httpPost.addHeader("Content-Type", "application/json");
                try {
                    httpPost.setEntity(new StringEntity(json));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Error setting POST entity: " + json, e);
                }
                return client;
            }

            @Override
            protected String handleResponse(HttpResponse httpResponse) {
                StatusLine statusLine = httpResponse.getStatusLine();
                if (statusLine.getStatusCode() != 200) {
                    fail("Received [" + statusLine.getStatusCode() + "] from [" + httpPost.getMethod() + "] to ["
                            + uri.toString() + "] because of [" + statusLine.getReasonPhrase() + "].");
                }

                return getResponse(httpResponse);
            }
        }.execute(httpPost);
    }

    protected URI getUri(String fullPath) {
        Map<String, String> parameters = new HashMap<String, String>();
        return getUri(fullPath, parameters);
    }

    protected URI getUri(String fullPath, Map<String, String> parameters) {
        URI uri = null;
        try {
            URL url = new URL(fullPath);
            URIBuilder uriBuilder = getUriBuilder(url);
            setParameters(uriBuilder, parameters);
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return uri;
    }

    private URIBuilder getUriBuilder(URL url) {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(url.getProtocol()).setHost(url.getHost()).setPort(url.getPort()).setPath(url.getPath());
        return uriBuilder;
    }

    private void setParameters(URIBuilder uriBuilder, Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            uriBuilder.setParameter(entry.getKey(), entry.getValue());
        }
    }


    private HttpClient getHttpClientTrustingAllSSLCerts() throws NoSuchAlgorithmException, KeyManagementException,
            UnrecoverableKeyException, KeyStoreException {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        SSLConnectionSocketFactory sslConnectionFactory = null;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, getTrustingManager(), new java.security.SecureRandom());
        } catch (Exception e) {
            fail("An exception was thrown while getting the SSL certificate trust manager [" + e.getMessage() + "].");
        }

        try {
            sslConnectionFactory = new SSLConnectionSocketFactory(sc,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            fail("An exception was thrown while creating the SSL socket factory [" + e.getMessage() + "].");
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionFactory)
                .build();

        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
        httpClientBuilder.setConnectionManager(ccm);
        return httpClientBuilder.build();
    }

    private TrustManager[] getTrustingManager() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }
        }};
    }
}
