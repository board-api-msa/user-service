package me.junbyoung.UserService.service;

import me.junbyoung.UserService.model.SecurityUser;
import me.junbyoung.UserService.model.User;
import me.junbyoung.UserService.payload.SignUpRequest;
import me.junbyoung.UserService.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with Email " + username + " not found"));
        return new SecurityUser(user);
    }

    public UserDetails loadUserById(long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + id + " not found"));
        return new SecurityUser(user);
    }

    public User create(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        signUpRequest.setPassword(encodedPassword);
        User user = new User(signUpRequest);
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        kafkaTemplate.send("user-events", id)
                .thenAccept(result -> {
                    userRepository.deleteById(id);
                })
                .exceptionally(ex -> {
                    LOGGER.warn("Failed to send message: {}", ex.getMessage());
                    return null;
                });
    }
}
