package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
import com.team33.modulebatch.exception.BatchApiException;

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

    @DisplayName("4xx 에러에서 extras.method_result_message가 있는 경우")
    @Test
    void test2() throws Exception {
        // given
        String errorBody = objectMapper.writeValueAsString(Map.of(
            "extras", Map.of(
                "method_result_message", "잘못된 요청입니다"
            )
        ));
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));
        when(response.getHeaders()).thenReturn(new HttpHeaders());

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(response))
            .isInstanceOf(BatchApiException.class)
            .hasMessage("잘못된 요청입니다");
    }

    @DisplayName("4xx 에러에서 JSON 파싱 실패시 기본 메시지 반환")
    @Test
    void test3() throws Exception {
        // given
        String errorBody = "test";
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(response))
            .isInstanceOf(BatchApiException.class)
            .hasMessage("알 수 없는 에러가 발생했습니다.");
    }

    @DisplayName("4xx 에러에서 extras 필드가 없는 경우")
    @Test
    void test4() throws Exception {
        // given
        String errorBody = objectMapper.writeValueAsString(Map.of(
            "message", "error"
        ));
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getBody()).thenReturn(
            new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(response))
            .isInstanceOf(BatchApiException.class)
            .hasMessage("알 수 없는 에러가 발생했습니다.");
    }
}