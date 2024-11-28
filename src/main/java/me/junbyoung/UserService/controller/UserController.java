package me.junbyoung.UserService.controller;

import jakarta.validation.Valid;
import me.junbyoung.UserService.model.SecurityUser;
import me.junbyoung.UserService.model.User;
import me.junbyoung.UserService.payload.LoginRequest;
import me.junbyoung.UserService.payload.LoginResponse;
import me.junbyoung.UserService.payload.SignUpRequest;
import me.junbyoung.UserService.service.UserService;
import me.junbyoung.UserService.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserInfoByUserId(@RequestHeader("User-Agent") String userAgent,
                                                    @PathVariable Long userId) {
        if (userAgent != null && userAgent.equals("FeignClient")) { //마이크로 서비스에게만 노출되도록 설정
            SecurityUser securityUser = (SecurityUser) userService.loadUserById(userId);
            return ResponseEntity.ok(securityUser.getUser());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @PostMapping("/me")
    public ResponseEntity<User> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        SecurityUser user = (SecurityUser) userService.loadUserById(userId);
        return ResponseEntity.ok(user.getUser());
    }

    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        User user = userService.create(signUpRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(user);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("X-User-Id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
