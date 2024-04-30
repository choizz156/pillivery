package com.team33.modulecore.order.dto;

import groovy.util.logging.Slf4j;
import lombok.Setter;
@Setter
public class OrderPatchDto { // 이름, 주소, 번호를 변경하는 경우

    private long orderId;
    private String name;
    private String address;
    private String detailAddress;
    private String phone;
}
