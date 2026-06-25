package com.delivery.admin;

import com.delivery.dto.CreateUserRequest;
import com.delivery.dto.UserProfileDto;
import com.delivery.user.Role;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import com.delivery.wallet.Wallet;
import com.delivery.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    public UserProfileDto createUser(CreateUserRequest request){
        String username = request.getUsername();
        if(userRepository.findByUsername(username).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists!");
        }
        String email = request.getEmail();
        if(userRepository.findByEmail(email).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(request.getFirstName());
        user.setUsername(request.getLastName());
        user.setPhoneNumber(request.getPhoneNubmer());
        Role role = request.getRole();
        user.setRole(role);
        user.setIsEnabled(true);

        String password = request.getPassword();
        String encodedPass = passwordEncoder.encode(password);
        user.setPassword(encodedPass);
        userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        return new UserProfileDto(username,email, role.toString());
    }

}
