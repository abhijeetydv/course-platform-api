package com.courseplatform.service;

import com.courseplatform.dto.request.LoginRequest;
import com.courseplatform.dto.request.RegisterRequest;
import com.courseplatform.dto.response.LoginResponse;
import com.courseplatform.dto.response.RegisterResponse;
import com.courseplatform.entity.User;
import com.courseplatform.exception.DuplicateEmailException;
import com.courseplatform.exception.InvalidCredentialsException;
import com.courseplatform.repository.UserRepository;
import com.courseplatform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        
        log.info("Successfully registered user with id: {}", savedUser.getId());

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .message("User registered successfully")
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getEmail());
        
        log.info("Successfully logged in user: {}", user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .build();
    }
}
