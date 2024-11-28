package me.junbyoung.UserService.payload;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String token;
    private final String tokenType = "Bearer";

    public LoginResponse(String token){
        this.token = token;
    }
}
