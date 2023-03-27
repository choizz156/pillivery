package server.team33.domain.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
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
}
