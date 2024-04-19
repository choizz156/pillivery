package com.team33.modulecore.user.dto;

import com.team33.modulecore.order.domain.Address;
import lombok.Builder;
import lombok.Data;

@Data
public class UserServicePatchDto {

    private Address address;
    private String displayName;
    private String phone;
    private String realName;
    private String password;

    @Builder
    private UserServicePatchDto(
        Address address,
        String displayName,
        String phone,
        String realName,
        String password
    ) {
        this.address = address;
        this.displayName = displayName;
        this.phone = phone;
        this.realName = realName;
        this.password = password;
    }

    public static UserServicePatchDto to(UserPatchDto userDto) {
        return UserServicePatchDto.builder()
            .password(userDto.getPassword())
            .address(new Address(userDto.getCity(), userDto.getDetailAddress()))
            .displayName(userDto.getDisplayName())
            .realName(userDto.getRealName())
            .phone(userDto.getPhone())
            .build();
    }
}
