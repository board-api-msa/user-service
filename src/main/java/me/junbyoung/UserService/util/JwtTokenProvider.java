package me.junbyoung.UserService.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import me.junbyoung.UserService.model.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	private final SecretKey secretKey;

	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMs;

	public JwtTokenProvider(@Value("${app.jwtSecret}") String jwtSecret){
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateToken(Authentication authentication) {
		SecurityUser user = (SecurityUser) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder()
				.subject(Long.toString(user.getId()))
				.issuedAt(new Date())
				.expiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}
}
