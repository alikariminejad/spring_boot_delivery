package com.delivery.admin;

import com.delivery.dto.RejectSettlementRequest;
import com.delivery.dto.SettlementRequestResponse;
import com.delivery.settlement.SettlementService;
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
@RequestMapping("/api/admin/settlement")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettlementController {

    private final SettlementService settlementService;

    @GetMapping
    public ResponseEntity<Page<SettlementRequestResponse>> getAllRequests(Pageable pageable){
        Page<SettlementRequestResponse> requestResponses = settlementService.getAllRequests(pageable);
        return new ResponseEntity<Page<SettlementRequestResponse>>(requestResponses, HttpStatus.OK);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<SettlementRequestResponse> approveRequest(@PathVariable UUID id,
                                                                    @AuthenticationPrincipal UserDetails admin){
        SettlementRequestResponse requestResponse = settlementService.approveRequest(id, admin.getUsername());
        return new ResponseEntity<SettlementRequestResponse>(requestResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<SettlementRequestResponse> rejectRequest(@PathVariable UUID id, @RequestBody @Valid RejectSettlementRequest req,
                                                                   @AuthenticationPrincipal UserDetails admin){
        SettlementRequestResponse requestResponse = settlementService.rejectRequest(id, admin.getUsername(), req.getNote());
        return new ResponseEntity<SettlementRequestResponse>(requestResponse, HttpStatus.OK);
    }
}
