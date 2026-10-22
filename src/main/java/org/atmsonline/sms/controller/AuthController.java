package org.atmsonline.sms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atmsonline.sms.dto.AuthDtos.AuthResponse;
import org.atmsonline.sms.dto.AuthDtos.LoginRequest;
import org.atmsonline.sms.dto.AuthDtos.RegisterRequest;
import org.atmsonline.sms.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}