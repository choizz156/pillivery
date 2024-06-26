package com.team33.modulecore.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    USER_NOT_FOUND(404, "회원 정보를 찾을 수 없습니다."),
    EXIST_EMAIL(409, "이미 가입한 e-mail입니다."),
    EXIST_DISPLAY_NAME(409, "이미 존재하는 닉네임입니다."),
    EXIST_PHONE_NUMBER(409, "이미 존재하는 연락처입니다."),
    ORDER_NOT_FOUND(404, "존재하지 않는 주문입니다."),
    CART_NOT_FOUND(404, "존재하지 않는 카트입니다."),
    ITEM_NOT_FOUND(404, "존재하지 않는 아이템입니다."),
    CART_ITEM_NOT_FOUND(404, "존재하지 않는 항목입니다."),
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰입니다."),
    TALK_NOT_FOUND(404, "존재하지 않는 토크입니다."),
    ACCESS_DENIED_USER(403, "권한이 없는 유저입니다."),
    SCHEDULE_CANCEL(500, "스케쥴이 취소되었습니다."),
    PAYMENT_FAIL(400, "결제에 실패했습니다."),
    ALREADY_LOGOUT(401, "로그아웃 됐습니다."),
    NOT_ORDERED_ITEM(404, "주문되지 않은 아이템입니다."),
    DUPLICATED_REVIEW(404, "이미 작성한 리뷰가 있습니다." );

    private final int code;
    private final String message;
}
