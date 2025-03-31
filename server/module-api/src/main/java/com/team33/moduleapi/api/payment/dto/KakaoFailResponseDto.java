package com.team33.moduleapi.api.payment.dto;

    
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class KakaoFailResponseDto {

    private Integer error_code;
    private String error_message;
    private Extras extras;

    @NoArgsConstructor
    @Getter
    public static class Extras {
        private String method_result_code;
        private String method_result_message;
    }
}
