package com.team33.modulecore.domain.order.value;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Address {
    @Column(nullable = false)
    private String address;
    private String detailAddress;

    @Builder
    public Address(String address, String detailAddress) {
        this.address = address;
        this.detailAddress = detailAddress;
    }
}
