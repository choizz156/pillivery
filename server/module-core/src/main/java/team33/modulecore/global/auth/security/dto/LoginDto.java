package team33.modulecore.global.auth.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginDto {

    private String username;
    private String password;

    @Builder
    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
