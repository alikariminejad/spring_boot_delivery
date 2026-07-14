package com.delivery.settlement;

import com.delivery.dto.SettlementRequestResponse;
import com.delivery.mapper.SettlementMapper;
import com.delivery.notification.NotificationService;
import com.delivery.notification.NotificationType;
import com.delivery.user.User;
import com.delivery.user.UserRepository;
import com.delivery.wallet.Wallet;
import com.delivery.wallet.WalletRepository;
import com.delivery.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRequestRepository settlementRequestRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final NotificationService notificationService;
    private final SettlementMapper settlementMapper;

    @Transactional
    public SettlementRequestResponse createRequest(String courierUsername, BigDecimal amount){
        User courier = userRepository.findByUsername(courierUsername)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier name was not found"));
        Wallet wallet = walletRepository.findByUser(courier)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier doesn't have a wallet"));
        if(wallet.getBalance().subtract(wallet.getBlockedBalance()).compareTo(amount) <0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settlement Request amount is bigger than your available balance");
        }
        SettlementRequest req = new SettlementRequest();
        req.setCourier(courier);
        req.setAmount(amount);
        req.setStatus(SettlementStatus.PENDING);
        SettlementRequest savedReq= settlementRequestRepository.save(req);

        walletService.blockFunds(wallet.getId(), amount, savedReq.getId());
        return settlementMapper.toDto(savedReq);
    }

    @Transactional
    public SettlementRequestResponse cancelRequest(UUID requestId, String courierUsername){
        SettlementRequest req = settlementRequestRepository.findById(requestId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Settlement request not found"));
        if(!req.getCourier().getUsername().equals(courierUsername)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request is forbidden by this courier: "+ courierUsername);
        }
        if(!req.getStatus().equals(SettlementStatus.PENDING)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request status is not pending");
        }
        req.setStatus(SettlementStatus.CANCELLED);
        req.setProcessedAt(LocalDateTime.now());
        walletService.unblockFunds(req.getCourier().getWallet().getId(), req.getAmount(), requestId);
        SettlementRequest savedReq = settlementRequestRepository.save(req);
        return settlementMapper.toDto(savedReq);
    }

    @Transactional
    public SettlementRequestResponse approveRequest(UUID requestId, String adminUsername){
        SettlementRequest req = settlementRequestRepository.findById(requestId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Settlement request not found"));
        if(!req.getStatus().equals(SettlementStatus.PENDING)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request status is not pending");
        }
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        Wallet wallet = req.getCourier().getWallet();
        walletService.deductSettlement(wallet.getId(), req.getAmount(), requestId);

        req.setStatus(SettlementStatus.APPROVED);
        req.setProcessedBy(admin);
        req.setProcessedAt(LocalDateTime.now());
        req.setNote("Approved");
        SettlementRequest savedReq = settlementRequestRepository.save(req);
        String notifMessage = "Settlement Request with id:" + requestId + " is approved";
        notificationService.createNotification(req.getCourier().getUsername(), notifMessage, NotificationType.SETTLEMENT_APPROVED, requestId);
        return settlementMapper.toDto(savedReq);
    }

    @Transactional
    public SettlementRequestResponse rejectRequest(UUID requestId, String adminUsername, String note){
        SettlementRequest req = settlementRequestRepository.findById(requestId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Settlement request not found"));
        if(!req.getStatus().equals(SettlementStatus.PENDING)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request status is not pending");
        }
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        walletService.unblockFunds(req.getCourier().getWallet().getId(), req.getAmount(), requestId);
        req.setStatus(SettlementStatus.REJECTED);
        req.setProcessedBy(admin);
        req.setProcessedAt(LocalDateTime.now());
        req.setNote(note);
        SettlementRequest savedReq = settlementRequestRepository.save(req);

        String notifMessage = "Settlement Request with id:" + requestId + " is rejected";
        notificationService.createNotification(req.getCourier().getUsername(), notifMessage, NotificationType.SETTLEMENT_REJECTED, requestId);
        return settlementMapper.toDto(savedReq);
    }
    @Transactional(readOnly = true)
    public Page<SettlementRequestResponse> getCourierRequests(String username, Pageable pageable){
        User courier = userRepository.findByUsername(username)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier not found"));
        Page<SettlementRequest> requests = settlementRequestRepository.findByCourier(courier, pageable);
        return requests.map(settlementMapper::toDto);
    }
    @Transactional(readOnly = true)
    public Page<SettlementRequestResponse> getAllRequests(Pageable pageable){
        Page<SettlementRequest> requests = settlementRequestRepository.findAll(pageable);
        return requests.map(settlementMapper::toDto);

    }
}
