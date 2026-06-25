package com.delivery.wallet;

import com.delivery.dto.WalletResponse;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WalletResponse getMyWallet(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found: " + username));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Wallet not found for this user: " + username));
        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                username
        );
    }

    @Transactional
    public WalletResponse topUp(String username, BigDecimal amount){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found: " + username));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Wallet not found for this user: " + username));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setWalletId(wallet.getId());
        transaction.setAmount(amount);
        transaction.setDescription("Wallet top-up");
        transactionRepository.save(transaction);

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                username
        );
    }

    @Transactional
    public void processPayment(User customer, BigDecimal amount, UUID orderId, String description){
        Wallet wallet = walletRepository.findByUser(customer)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if(wallet.getBalance().compareTo(amount) < 0){
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setAmount(amount);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setReferenceType("ORDER_PAYMENT");
        transaction.setReferenceId(orderId);
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void creditCourier(User courier, BigDecimal amount, UUID orderId){
        Wallet wallet = walletRepository.findByUser(courier)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "This courier doesn't have a wallet"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(TransactionType.COURIER_EARNING);
        transaction.setReferenceType("ORDER_DELIVERY");
        transaction.setReferenceId(orderId);
        transaction.setAmount(amount);
        transaction.setDescription("Commission for order delivery");
        transactionRepository.save(transaction);
    }
}
