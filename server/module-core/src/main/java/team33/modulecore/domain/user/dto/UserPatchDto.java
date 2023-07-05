package team33.modulecore.domain.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPatchDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String displayName;
    @NotBlank
    private String city;
    @NotBlank
    private String detailAddress;
    @NotBlank
    private String realName;
    @NotBlank
    private String phone;

    @Builder
    public UserPatchDto(
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
