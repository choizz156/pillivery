package com.team33.modulecore.order.dto;

import lombok.Setter;
@Setter
public class OrderPatch { // 이름, 주소, 번호를 변경하는 경우

    private long orderId;
    private String name;
    private String address;
    private String detailAddress;
    private String phone;
}
