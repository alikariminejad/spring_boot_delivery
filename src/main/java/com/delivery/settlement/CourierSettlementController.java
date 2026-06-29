package com.delivery.settlement;

import com.delivery.dto.CreateSettlementRequest;
import com.delivery.dto.SettlementRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settlement")
@PreAuthorize("hasRole('COURIER')")
public class CourierSettlementController {

    private final SettlementService settlementService;

    @PostMapping("/request")
    public ResponseEntity<SettlementRequestResponse> createRequest(@Valid @RequestBody CreateSettlementRequest req, @AuthenticationPrincipal UserDetails user){
        SettlementRequestResponse requestResponse = settlementService.createRequest(user.getUsername(), req.getAmount());
        return new ResponseEntity<SettlementRequestResponse>(requestResponse, HttpStatus.CREATED);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<SettlementRequestResponse>> getMyRequests(@AuthenticationPrincipal UserDetails user, Pageable pageable){
        Page<SettlementRequestResponse> requestResponses = settlementService.getCourierRequests(user.getUsername(), pageable);
        return new ResponseEntity<Page<SettlementRequestResponse>>(requestResponses, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SettlementRequestResponse> cancelRequest(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails){
        SettlementRequestResponse requestResponse = settlementService.cancelRequest(id, userDetails.getUsername());
        return new ResponseEntity<SettlementRequestResponse>(requestResponse, HttpStatus.OK);
    }
}
