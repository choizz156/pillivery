package com.team33.modulecore.core.user.domain;

import lombok.Getter;

public enum UserStatus {

    USER_ACTIVE("활동 중"),
    USER_WITHDRAWAL("회원 탈퇴");

    @Getter
    private String status;

    UserStatus( String status ){
        this.status = status;
    }
}
