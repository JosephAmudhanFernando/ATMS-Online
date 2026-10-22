package org.atmsonline.sms.service;

import lombok.RequiredArgsConstructor;
import org.atmsonline.sms.dto.AuthDtos.AuthResponse;
import org.atmsonline.sms.dto.AuthDtos.LoginRequest;
import org.atmsonline.sms.dto.AuthDtos.RegisterRequest;
import org.atmsonline.sms.entity.Role;
import org.atmsonline.sms.entity.User;
import org.atmsonline.sms.repository.UserRepository;
import org.atmsonline.sms.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.ROLE_STUDENT)) // Default role for open registration
                .enabled(true)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    public AuthResponse login(LoginRequest request) {
        // This will throw an exception if credentials are not valid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}