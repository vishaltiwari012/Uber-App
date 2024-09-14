package com.codingworld.project.uber.uberApp.services.impl;

import com.codingworld.project.uber.uberApp.dto.DriverDTO;
import com.codingworld.project.uber.uberApp.dto.SignUpDTO;
import com.codingworld.project.uber.uberApp.dto.UserDTO;
import com.codingworld.project.uber.uberApp.entities.Driver;
import com.codingworld.project.uber.uberApp.entities.User;
import com.codingworld.project.uber.uberApp.entities.enums.Role;
import com.codingworld.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.codingworld.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.codingworld.project.uber.uberApp.repositories.UserRepository;
import com.codingworld.project.uber.uberApp.security.JWTService;
import com.codingworld.project.uber.uberApp.services.AuthService;
import com.codingworld.project.uber.uberApp.services.DriverService;
import com.codingworld.project.uber.uberApp.services.RiderService;
import com.codingworld.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.codingworld.project.uber.uberApp.entities.enums.Role.DRIVER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public String[] login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new String[]{accessToken, refreshToken};
    }

    @Override
    @Transactional
    public UserDTO signup(SignUpDTO signUpDTO) {
        User user = userRepository.findByEmail(signUpDTO.getEmail()).orElse(null);
        if(user != null)
                throw new RuntimeConflictException("Cannot signup, User already exists with email " + signUpDTO.getEmail());

        User mappedUser = modelMapper.map(signUpDTO, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser = userRepository.save(mappedUser);

//       create user related entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public DriverDTO onboardNewDriver(Long userId, String vehicleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if(user.getRoles().contains(DRIVER)) {
            throw new RuntimeConflictException("User with id "+userId+" is already a driver");
        }
        Driver createdDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .available(true)
                .build();
        user.getRoles().add(DRIVER);
        userRepository.save(user);
        Driver savedDriver = driverService.createNewDriver(createdDriver);
        return modelMapper.map(savedDriver, DriverDTO.class);

    }

    public String refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return jwtService.generateAccessToken(user);
    }

}
