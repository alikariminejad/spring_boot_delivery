package com.delivery.wallet;



import com.delivery.AbstractIntegrationTest;
import com.delivery.dto.AuthResponse;
import com.delivery.dto.RegisterRequest;
import com.delivery.dto.WalletResponse;
import com.delivery.security.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WalletServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private WalletService walletService;

    @Test
    void shouldCreateWalletOnRegistrationAndTopUp(){
        // Register a new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser3");
        registerRequest.setEmail("testuser3@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test2");
        registerRequest.setLastName("User2");

        AuthResponse authResponse = authService.register(registerRequest);
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getUsername()).isEqualTo("testuser3");

        // Check wallet balance (should be 0)
        WalletResponse wallet = walletService.getMyWallet("testuser3");
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(wallet.getBlockedBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(wallet.getAvailableBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        // Top up
        WalletResponse toppedUp = walletService.topUp("testuser3", BigDecimal.valueOf(100));
        assertThat(toppedUp.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(toppedUp.getAvailableBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));

        // Verify via getMyWallet
        WalletResponse afterTopUp = walletService.getMyWallet("testuser3");
        assertThat(afterTopUp.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}
