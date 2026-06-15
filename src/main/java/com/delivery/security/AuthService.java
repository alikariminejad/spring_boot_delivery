package com.delivery.security;

import com.delivery.dto.AuthResponse;
import com.delivery.dto.LoginRequest;
import com.delivery.dto.RegisterRequest;
import com.delivery.user.Role;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest){
        String username = registerRequest.getUsername();
        if(userRepository.findByUsername(username) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists!");
        }
        String email = registerRequest.getEmail();
        if(userRepository.findByEmail(email) != null){
            throw new RuntimeException("Email already exists!");
        }
        String pass = registerRequest.getPassword();
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String encodedPass = encoder.encode(pass);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPass);
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        Role role = Role.CUSTOMER;
        String roleString = role.toString();
        user.setRole(Role.CUSTOMER);
        user.setIsEnabled(true);
        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, username, roleString);
    }

    public AuthResponse login(LoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        UserDetails userDetails = (UserDetails) authenticationManager.authenticate(authenticationToken);
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().toString().substring(7);
        return new AuthResponse(token, userDetails.getUsername(), role);
    }
}
