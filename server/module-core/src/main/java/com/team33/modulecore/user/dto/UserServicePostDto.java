package com.team33.modulecore.user.dto;


import com.team33.modulecore.order.domain.Address;
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

    public static UserServicePostDto to(UserPostDto userDto) {
        Address address = new Address(userDto.getCity(), userDto.getDetailAddress());
        return UserServicePostDto.builder().email(userDto.getEmail())
            .displayName(userDto.getDisplayName())
            .address(address)
            .password(userDto.getPassword())
            .realName(userDto.getRealName())
            .phone(userDto.getPhone())
            .build();
    }
}
