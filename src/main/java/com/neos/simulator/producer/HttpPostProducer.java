/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neos.simulator.producer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;

import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpPostProducer extends EventProducer {

    private static final Logger log = LogManager.getLogger(HttpPostProducer.class);
    public static final String URL_PROP_NAME = "url";

    private String url;
    private CloseableHttpClient httpClient;

    public HttpPostProducer(Map<String, Object> producerConfig) throws NoSuchAlgorithmException {
    	super();
    	this.url = (String) producerConfig.get(URL_PROP_NAME);
        SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(SSLContext.getDefault(), new NoopHostnameVerifier());
        this.httpClient = HttpClientBuilder.create().setSSLSocketFactory(sf).build();
    }

    @Override
    public void publish(String event) {
    	try {
            HttpPost request = new HttpPost(url);
            StringEntity input = new StringEntity(event);
            input.setContentType("application/json");
            request.setEntity(input);

//            log.debug("executing request " + request);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(request);
            } catch (IOException ex) {
                log.error("Error Posting Event", ex);
            }
            if (response != null) {
                try {
//                    log.debug("----------------------------------------");
//                    log.debug(response.getStatusLine().toString());
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
//                        log.debug("Response content length: " + resEntity.getContentLength());
                    }
                    EntityUtils.consume(resEntity);
                } catch (IOException ioe) {
                    //oh well
                } finally {
                    try {
                        response.close();
                    } catch (IOException ex) {
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void stop() {
        try {
            httpClient.close();
        } catch (IOException ex) {
        }
    }

}
