package com.example.event.authentication.service;

import com.example.event.authentication.persistance.entity.RoleEntity;
import com.example.event.authentication.persistance.entity.TokenEntity;
import com.example.event.authentication.persistance.entity.UserEntity;
import com.example.event.authentication.persistance.repository.TokenRepository;
import com.example.event.authentication.persistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/*@Service
public class AuthenticationService {
    private static final int TOKEN_VALIDITY_IN_MINUTES = 150;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public String authenticate(String username, String password) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Username and/or password do not match!");
        }

        if ( ! passwordEncoder.matches(password,
                optionalUser.get().getPasswordHash())) {
            throw new AuthenticationCredentialsNotFoundException("Username and/or password do not match!");
        }

        TokenEntity token = new TokenEntity();
        String randomString = UUID.randomUUID().toString();
        token.setToken(randomString);
        token.setUser(optionalUser.get());
        token.setCreated(LocalDateTime.now());

        tokenRepository.save(token);

        return token.getToken();
    }

    @Transactional
    public UserRolesDTO authenticate(String token) {
        Optional<TokenEntity> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication failed!");
        }

        validateTokenExpiration(optionalToken.get());

        Set<RoleEntity> roles = optionalToken.get().getUser().getRoles();
        Set<String> roleNames = roles.stream()
                .map( entry -> entry.getRoleName())
                .collect(Collectors.toSet());

        return new UserRolesDTO(optionalToken.get().getUser().getUsername(), roleNames);
    }

    private void validateTokenExpiration(TokenEntity token) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tokenExpiration = token.getCreated().plus(TOKEN_VALIDITY_IN_MINUTES, ChronoUnit.MINUTES);

        if ( now.isAfter(tokenExpiration) ) {
            throw new AuthenticationCredentialsNotFoundException("Authentication failed!");
        }
    }

    @Transactional
    public void tokenRemove(String token) {
        tokenRepository.deleteByToken(token);
    }
}*/


/*@Service
public class AuthenticationService {
    private static final int TOKEN_VALIDITY_IN_MINUTES = 10;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public String authenticate(String username, String password) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Username and/or password do not match!");
        }

        if (!passwordEncoder.matches(password, optionalUser.get().getPasswordHash())) {
            throw new AuthenticationCredentialsNotFoundException("Username and/or password do not match!");
        }

        String token = generateAndStoreToken(optionalUser.get().getId());

        return token;
    }

    @Transactional
    public UserRolesDTO authenticate(String token) {
        Optional<TokenEntity> optionalToken = tokenRepository.findByToken(token);
        String userId = getUserIdFromToken(token);

        if (userId == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication failed!");
        }

        validateTokenExpiration(optionalToken.get());

        Set<RoleEntity> roles = optionalToken.get().getUser().getRoles();
        Set<String> roleNames = roles.stream()
                .map( entry -> entry.getRoleName())
                .collect(Collectors.toSet());

        return new UserRolesDTO(optionalToken.get().getUser().getUsername(), roleNames);

    }

    private void validateTokenExpiration(TokenEntity token) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tokenExpiration = token.getCreated().plus(TOKEN_VALIDITY_IN_MINUTES, ChronoUnit.MINUTES);

        if ( now.isAfter(tokenExpiration) ) {
            throw new AuthenticationCredentialsNotFoundException("Authentication failed!");
        }
    }

    private String generateAndStoreToken(Long userId) {
        String randomString = UUID.randomUUID().toString();
        String token = "token:" + randomString;
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        // Store the token in Redis with an expiration time
        opsForValue.set(token, userId.toString(), TOKEN_VALIDITY_IN_MINUTES, TimeUnit.MINUTES);

        return token;
    }

    private String getUserIdFromToken(String token) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        return opsForValue.get(token);
    }

    public void tokenRemove(String token) {
        redisTemplate.delete(token);
    }

}*/


@Service
public class AuthenticationService {
    private static final int TOKEN_VALIDITY_IN_MINUTES = 15;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public String authenticate(String username, String password) {
        // Check if the token is cached in Redis
        String cachedToken = getCachedToken(username);

        if (cachedToken != null) {
            return cachedToken; // Token is already cached, return it
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Username and/or password do not match!");
        }

        if ( ! passwordEncoder.matches(password,
                optionalUser.get().getPasswordHash())) {
            throw new AuthenticationCredentialsNotFoundException("Username and/or password do not match!");
        }

        TokenEntity token = new TokenEntity();
        String randomToken = UUID.randomUUID().toString();
        token.setToken(randomToken);
        token.setUser(optionalUser.get());
        token.setCreated(LocalDateTime.now());

        tokenRepository.save(token);

        // Cache the token in Redis with
        cacheToken(username, randomToken);

        return randomToken;
    }

    private String getCachedToken(String username) {
        // Retrieve the token from Redis cache
        return redisTemplate.opsForValue().get("token:" + username);
    }

    private void cacheToken(String username, String token) {
        // Cache the token in Redis
        redisTemplate.opsForValue().set("token:" + username, token, Duration.ofMinutes(TOKEN_VALIDITY_IN_MINUTES));
        redisTemplate.opsForValue().set("reverseToken:" + token, username, Duration.ofMinutes(TOKEN_VALIDITY_IN_MINUTES));
    }

    @Transactional
    public UserRolesDTO authenticate(String token) {
        Optional<UserEntity> user = userRepository.findByUsername(redisTemplate.opsForValue().get("reverseToken:" + token));

        if (token.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication failed!");
        }

        validateTokenExpiration(token);

        Set<RoleEntity> roles = user.get().getRoles();
        Set<String> roleNames = roles.stream()
                .map( entry -> entry.getRoleName())
                .collect(Collectors.toSet());

        return new UserRolesDTO(user.get().getUsername(), roleNames);
    }

    private void validateTokenExpiration(String token) {
        Long tokenExpiration = redisTemplate.getExpire("reverseToken:" + token);

        if (tokenExpiration == null || tokenExpiration < 0) {
            System.out.println(tokenExpiration);
            throw new AuthenticationCredentialsNotFoundException("Authentication failed!");
        }
    }

    @Transactional
    public void tokenRemove(String token) {
        redisTemplate.delete("reverseToken:" + token);
    }
}