package me.junbyoung.UserService.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 10, message = "이름은 10자 이내로 입력해주세요.")
    String name;
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    String email;
    @NotBlank(message = "비밀번호을 입력해주세요.")
    String password;
}
