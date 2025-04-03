package com.team33.modulebatch.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team33.modulebatch.exception.BatchApiException;

@ExtendWith(MockitoExtension.class)
class RestTemplateErrorHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ClientHttpResponse response;
    @InjectMocks
    private RestTemplateErrorHandler errorHandler;

    @DisplayName("4xx 상태 코드일 때 에러로 인식한다.")
    @Test
    void test1() throws IOException {
        // given
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // when
        boolean result = errorHandler.hasError(response);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("2xx 상태 코드일 때 정상으로 인식한다.")
    @Test
    void test2() throws IOException {
        // given
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        // when
        boolean result = errorHandler.hasError(response);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("4xx 에러 발생 시 예외를 던진다.")
    @Test
    void test3() throws IOException {
        // given
        Map<String, Map<String, String>> errorResponse = new HashMap<>();
        Map<String, String> extras = new HashMap<>();
        extras.put("method_result_message", "Invalid request");
        errorResponse.put("extras", extras);
        
        String errorBody = objectMapper.writeValueAsString(errorResponse);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        // when then
        assertThatThrownBy(() -> errorHandler.handleError(response))
            .isInstanceOf(BatchApiException.class)
            .hasMessage("알 수 없는 에러가 발생했습니다.");
    }

    @DisplayName("JSON 파싱 실패 시 기본 예외 메시지를 반환한다.")
    @Test
    void test4() throws IOException {
        // given
        String errorBody = "invalid_json";
        when(response.getBody()).thenReturn(new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        // when then
        assertThatThrownBy(() -> errorHandler.handleError(response))
            .isInstanceOf(BatchApiException.class)
            .hasMessage("알 수 없는 에러가 발생했습니다.");
    }
}