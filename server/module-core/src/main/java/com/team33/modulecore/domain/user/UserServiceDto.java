package com.team33.modulecore.domain.user;


import com.team33.modulecore.domain.order.value.Address;
import com.team33.modulecore.domain.user.dto.UserPostDto;
import lombok.Builder;
import lombok.Data;

@Data
public class UserServiceDto {

    private String email;
    private String displayName;
    private String password;
    private String realName;
    private String phone;
    private Address address;

    @Builder
    private UserServiceDto(
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

    public static UserServiceDto to(UserPostDto userDto) {
        Address address = new Address(userDto.getCity(), userDto.getDetailAddress());
        return UserServiceDto.builder().email(userDto.getEmail())
            .displayName(userDto.getDisplayName())
            .address(address)
            .password(userDto.getPassword())
            .realName(userDto.getRealName())
            .phone(userDto.getPhone())
            .build();
    }
}
