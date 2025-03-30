package com.team33.modulebatch.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

    private final static Logger log = LoggerFactory.getLogger("fileLog");

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        final HttpStatus statusCode = response.getStatusCode();
        return !statusCode.is2xxSuccessful();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final String error = getErrorAsString(response);
        log.error("======payment error=====");
        log.error("Headers: {}", response.getHeaders());
        log.error("Response Status : {}", response.getRawStatusCode());
        log.error("Response body: {}", error);
        log.error("================");
        ThreadLocalErrorMessage.set(error);
    }

    private String getErrorAsString(final ClientHttpResponse response) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getBody()))) {
            return br.readLine();
        }
    }

    public static class ThreadLocalErrorMessage {
        private static final ThreadLocal<String> errorMessage = new ThreadLocal<>();

        public static String get() {
            return errorMessage.get();
        }

        public static void set(String error) {
            errorMessage.set(error);
        }

        public static void clear(){
            errorMessage.remove();
        }
    }
}
