package com.team33.modulecore.core.user.domain;

import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Embeddable
public class Address {

    private String city;
    private String detailAddress;

    @Builder
    public Address(String city, String detailAddress) {
        this.city = city;
        this.detailAddress = detailAddress;
    }
}
