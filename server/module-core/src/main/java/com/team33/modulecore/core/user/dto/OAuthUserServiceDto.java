package com.team33.modulecore.core.user.dto;

import com.team33.modulecore.core.user.domain.Address;
import lombok.Builder;
import lombok.Data;

@Data
public class OAuthUserServiceDto {

    private String email;
    private Address address;
    private String phone;
    private String displayName;

    @Builder
    private OAuthUserServiceDto(String email, Address address, String phone, String displayName) {
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.displayName = displayName;
    }
}
