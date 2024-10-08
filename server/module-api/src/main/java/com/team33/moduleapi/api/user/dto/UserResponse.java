package com.team33.moduleapi.api.user.dto;

import java.time.ZonedDateTime;

import com.team33.modulecore.core.user.domain.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
public class UserResponse {

    private String email;
    private String displayName;
    private String city;
    private String detailAddress;
    private String realName;
    private String phone;
    private boolean social;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createAt;

    private UserResponse() {
    }

    @Builder
    private UserResponse(
        String email,
        String displayName,
        String city,
        String detailAddress,
        String realName,
        String phone,
        String password,
        boolean social,
        ZonedDateTime updatedAt,
        ZonedDateTime createAt
    ) {
        this.email = email;
        this.displayName = displayName;
        this.city = city;
        this.detailAddress = detailAddress;
        this.realName = realName;
        this.phone = phone;
        this.social = social;
        this.updatedAt = updatedAt;
        this.createAt = createAt;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .city(user.getAddress().getCity())
            .realName(user.getRealName())
            .detailAddress(user.getAddress().getDetailAddress())
            .phone(user.getPhone())
            .social(user.getOauthId() != null)
            .updatedAt(user.getUpdatedAt())
            .createAt(user.getCreatedAt())
            .build();
    }
}
