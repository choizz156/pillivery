package server.team33.user.dto;


import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPostDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String displayName;
    @NotBlank
    private String address;
    @NotBlank
    private String detailAddress;
    @NotBlank
    private String realName;
    @NotBlank
    private String phone;
}
