package com.team33.moduleapi.ui.payment.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoFailResponseDto {

    private int code;
    private String msg;
    private Map<String, String> extras = new HashMap<>(2);

}
