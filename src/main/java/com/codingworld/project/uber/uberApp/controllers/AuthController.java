package com.codingworld.project.uber.uberApp.controllers;

import com.codingworld.project.uber.uberApp.dto.*;
import com.codingworld.project.uber.uberApp.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUpUser(@RequestBody SignUpDTO signUpDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signUpDTO));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/onBoardNewDriver/{userId}")
    public ResponseEntity<DriverDTO> onBoardNewDriver(@PathVariable Long userId, @RequestBody OnBoardDriverDTO onBoardDriverDTO) {
         return new ResponseEntity<>(authService.onboardNewDriver(userId, onBoardDriverDTO.getVehicleId()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        String tokens[] = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return new ResponseEntity<>(new LoginResponseDTO(tokens[0]), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the cookie."));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDTO(accessToken));
    }
}
