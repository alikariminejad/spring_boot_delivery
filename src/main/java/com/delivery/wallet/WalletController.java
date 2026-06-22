package com.delivery.wallet;

import com.delivery.dto.TopUpRequest;
import com.delivery.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponse> myWallet(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        WalletResponse wallet = walletService.getMyWallet(username);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/topup")
    public ResponseEntity<WalletResponse> topUpWallet(@RequestBody TopUpRequest topUpRequest,
                                                      @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        WalletResponse wallet = walletService.topUp(username, topUpRequest.getAmount());
        return ResponseEntity.ok(wallet);
    }

}
