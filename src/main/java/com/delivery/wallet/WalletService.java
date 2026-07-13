package com.delivery.wallet;

import com.delivery.dto.WalletResponse;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "wallets", key = "#username")
    public WalletResponse getMyWallet(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found: " + username));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Wallet not found for this user: " + username));
        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getBlockedBalance(),
                wallet.getBalance().subtract(wallet.getBlockedBalance()),
                username
        );
    }

    @Transactional
    @CacheEvict(value = "wallets", key = "#username")
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
                wallet.getBlockedBalance(),
                wallet.getBalance().subtract(wallet.getBlockedBalance()),
                username
        );
    }

    @Transactional
    @CacheEvict(value = "wallets", key = "#customer.username")
    public void processPayment(User customer, BigDecimal amount, UUID orderId, String description){
        Wallet wallet = walletRepository.findByUser(customer)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
        BigDecimal available = wallet.getBalance().subtract(wallet.getBlockedBalance());
        if(available.compareTo(amount) < 0){
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Insufficient available balance");
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
    @CacheEvict(value = "wallets", key = "#courier.username")
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

    @Transactional
    @CacheEvict(value = "wallets", key = "#wallet.user.username")
    public void blockFunds(UUID walletId, BigDecimal amount, UUID settlementId){
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet was not found"));
        wallet.setBlockedBalance(wallet.getBlockedBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(TransactionType.BLOCKED);
        transaction.setReferenceType("SETTLEMENT_HOLD");
        transaction.setReferenceId(settlementId);
        transaction.setAmount(amount);
        transaction.setDescription("Funds blocked for settlement request");
        transactionRepository.save(transaction);
    }

    @Transactional
    @CacheEvict(value = "wallets", key = "#wallet.user.username")
    public void unblockFunds(UUID walletId, BigDecimal amount, UUID settlementId){
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet was not found"));
        if(wallet.getBlockedBalance().compareTo(amount)<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are not enough blocked balance");
        }
        wallet.setBlockedBalance(wallet.getBlockedBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(TransactionType.UNBLOCKED);
        transaction.setReferenceType("SETTLEMENT_RELEASE");
        transaction.setReferenceId(settlementId);
        transaction.setAmount(amount);
        transaction.setDescription("Funds released for settlement request");
        transactionRepository.save(transaction);
    }

    @Transactional
    @CacheEvict(value = "wallets", key = "#wallet.user.username")
    public void deductSettlement(UUID walletId, BigDecimal amount, UUID settlementId){
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet was not found"));
        if(wallet.getBlockedBalance().compareTo(amount)<0 || wallet.getBalance().compareTo(amount)<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance or blocked amount");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setBlockedBalance(wallet.getBlockedBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setReferenceType("SETTLEMENT");
        transaction.setReferenceId(settlementId);
        transaction.setAmount(amount);
        transaction.setDescription("Funds subtracted from balance and blocked balance for settlement request");
        transactionRepository.save(transaction);
    }
}
