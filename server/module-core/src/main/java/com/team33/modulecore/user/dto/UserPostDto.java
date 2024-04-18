package com.team33.modulecore.user.dto;


import com.team33.modulecore.user.infra.NotSpace;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPostDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^+=-])(?=.*\\d).{8,25}$",
        message = "비밀번호는 숫자+영문자+특수문자 조합으로 8자리 이상이어야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    private String displayName;

    @NotBlank
    private String city;

    @NotBlank
    private String detailAddress;

    @NotSpace(message = "공백이 있어서는 안됩니다.")
    private String realName;

    @Pattern(
        regexp = "^01([0|1|6|7|8|9])-?(\\d{3,4})-?(\\d{4})$",
        message = "올바른 연락처 형식이 아닙니다."
    )
    private String phone;

    @Builder
    public UserPostDto(
        String email,
        String password,
        String displayName,
        String city,
        String detailAddress,
        String realName,
        String phone
    ) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.city = city;
        this.detailAddress = detailAddress;
        this.realName = realName;
        this.phone = phone;
    }
}
