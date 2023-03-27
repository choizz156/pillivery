package server.team33.domain.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
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

}
