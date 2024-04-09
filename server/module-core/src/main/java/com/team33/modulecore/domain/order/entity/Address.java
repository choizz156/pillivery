package com.team33.modulecore.domain.order.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Address {
    @Column(nullable = false)
    private String address;
    private String detailAddress;

    public Address(String address, String detailAddress) {
        this.address = address;
        this.detailAddress = detailAddress;
    }
}
