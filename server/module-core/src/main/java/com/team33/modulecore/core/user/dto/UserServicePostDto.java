package com.team33.modulecore.core.user.dto;


import com.team33.modulecore.core.user.domain.Address;
import lombok.Builder;
import lombok.Data;

@Data
public class UserServicePostDto {

    private String email;
    private String displayName;
    private String password;
    private String realName;
    private String phone;
    private Address address;

    @Builder
    private UserServicePostDto(
        String email,
        String displayName,
        String password,
        String realName,
        String phone,
        Address address
    ) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
        this.address = address;
    }
}
