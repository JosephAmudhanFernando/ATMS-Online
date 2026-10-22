package org.atmsonline.sms.controller.finance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atmsonline.sms.dto.finance.FinanceDtos.*;
import org.atmsonline.sms.service.finance.FinanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    // ==========================================
    // VENDOR ENDPOINTS
    // ==========================================

    @PostMapping("/vendors")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VendorResponse> createVendor(
            @Valid @RequestBody CreateVendorRequest request
    ) {
        VendorResponse response = financeService.createVendor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vendors")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<VendorResponse>> getAllVendors() {
        return ResponseEntity.ok(financeService.getAllVendors());
    }

    // ==========================================
    // BILL ENDPOINTS
    // ==========================================

    @PostMapping("/bills")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VendorBillResponse> createBill(
            @Valid @RequestBody CreateBillRequest request
    ) {
        VendorBillResponse response = financeService.createBill(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vendors/{vendorId}/bills")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<VendorBillResponse>> getBillsByVendor(
            @PathVariable UUID vendorId
    ) {
        return ResponseEntity.ok(financeService.getBillsByVendor(vendorId));
    }

    // ==========================================
    // VOUCHER ENDPOINTS
    // ==========================================

    @PostMapping("/vouchers/disburse")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VoucherResponse> disburseVoucher(
            @Valid @RequestBody CreateVoucherRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        VoucherResponse response = financeService.createAndDisburseVoucher(
                request,
                userDetails.getUsername()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}