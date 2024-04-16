package com.team33.modulecore.domain.user.dto;

import com.team33.modulecore.domain.order.value.Address;
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

    public static OAuthUserServiceDto to(UserPostOauthDto userDto) {
        return OAuthUserServiceDto.builder()
            .email(userDto.getEmail())
            .address(new Address(userDto.getCity(), userDto.getDetailAddress()))
            .displayName(userDto.getDisplayName())
            .phone(userDto.getPhone())
            .build();
    }
}
