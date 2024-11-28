package me.junbyoung.UserService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.junbyoung.UserService.payload.SignUpRequest;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "app_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore //패스워드는 직렬화 되지않도록 설정.
    private String password;

    @CreatedDate
    private LocalDateTime createdAt;

    public User(SignUpRequest signUpRequest){
        this.email = signUpRequest.getEmail();
        this.name = signUpRequest.getName();
        this.password = signUpRequest.getPassword();
    }
}