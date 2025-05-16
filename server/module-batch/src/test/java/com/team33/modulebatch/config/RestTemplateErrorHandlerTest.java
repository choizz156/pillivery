package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class RestTemplateErrorHandlerTest {

    private RestTemplateErrorHandler errorHandler;
    private ObjectMapper objectMapper;

    @Mock
    private ClientHttpResponse response;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        errorHandler = new RestTemplateErrorHandler(objectMapper);
    }

    @DisplayName("5xx 에러 발생시 HttpServerErrorException를 던진다.")
    @Test
    void test1() throws Exception {
        // given
        String errorBody = "Internal Server Error";
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(response.getStatusText()).thenReturn("Internal Server Error");
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(response))
            .isInstanceOf(HttpServerErrorException.class)
            .hasMessageContaining("Internal Server Error");
    }
}