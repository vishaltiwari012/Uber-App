package com.codingworld.project.uber.uberApp.services.impl;

import com.codingworld.project.uber.uberApp.dto.SignUpDTO;
import com.codingworld.project.uber.uberApp.dto.UserDTO;
import com.codingworld.project.uber.uberApp.entities.User;
import com.codingworld.project.uber.uberApp.entities.enums.Role;
import com.codingworld.project.uber.uberApp.repositories.UserRepository;
import com.codingworld.project.uber.uberApp.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
) class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RiderServiceImpl riderService;

    @Mock
    private WalletServiceImpl walletService;

    @Mock
    private DriverServiceImpl driverService;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private SignUpDTO signUpDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(Set.of(Role.RIDER));

        signUpDTO = new SignUpDTO();
        signUpDTO.setEmail("test@example.com");
        signUpDTO.setPassword("password");
    }

    @Test
    void testLogin_whenSuccess() {
//        arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

//        act
        String[] tokens = authService.login(user.getEmail(), user.getPassword());

//        assert
        assertThat(tokens).hasSize(2);
        assertThat(tokens[0]).isEqualTo("accessToken");
        assertThat(tokens[1]).isEqualTo("refreshToken");
    }

    @Test
    void testSignup_whenSuccess() {
//        arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

//        act
        UserDTO userDTO = authService.signup(signUpDTO);

//        assert
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getEmail()).isEqualTo(signUpDTO.getEmail());

        verify(riderService).createNewRider(any(User.class));
        verify(walletService).createNewWallet(any(User.class));
    }

    @Test
    void onboardNewDriver() {
    }

    @Test
    void refreshToken() {
    }
}