package team33.modulecore.domain.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPostOauthDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String displayName;
    @NotBlank
    private String city;
    @NotBlank
    private String detailAddress;
    @NotBlank
    private String phone;

    @Builder
    public UserPostOauthDto(
        String email,
        String displayName,
        String city,
        String detailAddress,
        String phone
    ) {
        this.email = email;
        this.displayName = displayName;
        this.city = city;
        this.detailAddress = detailAddress;
        this.phone = phone;
    }
}
