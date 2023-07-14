package com.team33.modulecore.domain.payment.kakao.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class FailResponse {

    private int code;
    private String msg;
    private Map<String, String> extras = new HashMap<>(2);

}
