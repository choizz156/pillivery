package com.team33.modulecore.domain.user.entity;

import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@Embeddable
public class Address {

    private String city;
    private String detailAddress;

    public Address(String city, String detailAddress) {
        this.city = city;
        this.detailAddress = detailAddress;
    }
}
