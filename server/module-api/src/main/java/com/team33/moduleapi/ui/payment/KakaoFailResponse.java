package com.team33.moduleapi.ui.payment;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoFailResponse {

    private int code;
    private String msg;
    private Map<String, String> extras = new HashMap<>(2);

}
