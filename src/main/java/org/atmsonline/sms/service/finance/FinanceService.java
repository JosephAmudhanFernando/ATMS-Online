package org.atmsonline.sms.service.finance;

import lombok.RequiredArgsConstructor;
import org.atmsonline.sms.dto.finance.FinanceDtos.*;
import org.atmsonline.sms.entity.User;
import org.atmsonline.sms.entity.finance.*;
import org.atmsonline.sms.repository.UserRepository;
import org.atmsonline.sms.repository.finance.PaymentVoucherRepository;
import org.atmsonline.sms.repository.finance.VendorBillRepository;
import org.atmsonline.sms.repository.finance.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final VendorRepository vendorRepository;
    private final VendorBillRepository billRepository;
    private final PaymentVoucherRepository voucherRepository;
    private final UserRepository userRepository;

    // ==========================================
    // VENDOR OPERATIONS
    // ==========================================

    @Transactional
    public VendorResponse createVendor(CreateVendorRequest request) {
        if (vendorRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException(
                    "Vendor with name '" + request.name() + "' already exists"
            );
        }

        Vendor vendor = Vendor.builder()
                .name(request.name())
                .contactPerson(request.contactPerson())
                .phone(request.phone())
                .email(request.email())
                .address(request.address())
                .taxIdentificationNumber(request.taxIdentificationNumber())
                .defaultCategory(request.defaultCategory())
                .build();

        return mapToVendorResponse(vendorRepository.save(vendor));
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToVendorResponse)
                .toList();
    }

    // ==========================================
    // BILL OPERATIONS
    // ==========================================

    @Transactional
    public VendorBillResponse createBill(CreateBillRequest request) {
        if (billRepository.existsByBillNumber(request.billNumber())) {
            throw new IllegalArgumentException(
                    "Bill number '" + request.billNumber() + "' is already registered"
            );
        }

        Vendor vendor = vendorRepository.findById(request.vendorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vendor not found with ID: " + request.vendorId()
                ));

        if (request.dueDate().isBefore(request.billDate())) {
            throw new IllegalArgumentException("Due date cannot be before bill date");
        }

        VendorBill bill = VendorBill.builder()
                .vendor(vendor)
                .billNumber(request.billNumber())
                .billDate(request.billDate())
                .dueDate(request.dueDate())
                .totalAmount(request.totalAmount())
                .paidAmount(BigDecimal.ZERO)
                .status(BillStatus.PENDING)
                .category(request.category())
                .description(request.description())
                .build();

        return mapToBillResponse(billRepository.save(bill));
    }

    @Transactional(readOnly = true)
    public List<VendorBillResponse> getBillsByVendor(UUID vendorId) {
        return billRepository.findByVendorId(vendorId).stream()
                .map(this::mapToBillResponse)
                .toList();
    }

    // ==========================================
    // VOUCHER DISBURSEMENT & RECONCILIATION
    // ==========================================

    @Transactional
    public VoucherResponse createAndDisburseVoucher(
            CreateVoucherRequest request,
            String username
    ) {
        if (voucherRepository.existsByVoucherNumber(request.voucherNumber())) {
            throw new IllegalArgumentException(
                    "Voucher number '" + request.voucherNumber() + "' already exists"
            );
        }

        VendorBill bill = billRepository.findById(request.billId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bill not found with ID: " + request.billId()
                ));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + username
                ));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalStateException("Bill is already fully paid");
        }

        BigDecimal remainingBalance = bill.getTotalAmount().subtract(bill.getPaidAmount());
        if (request.amountPaid().compareTo(remainingBalance) > 0) {
            throw new IllegalArgumentException(
                    "Disbursement of " + request.amountPaid() +
                            " exceeds remaining balance of " + remainingBalance
            );
        }

        // 1. Persist the Voucher
        PaymentVoucher voucher = PaymentVoucher.builder()
                .voucherNumber(request.voucherNumber())
                .bill(bill)
                .amountPaid(request.amountPaid())
                .paymentDate(request.paymentDate())
                .paymentMode(request.paymentMode())
                .transactionReference(request.transactionReference())
                .status(VoucherStatus.DISBURSED)
                .createdBy(currentUser)
                .approvedBy(currentUser)
                .remarks(request.remarks())
                .build();

        PaymentVoucher savedVoucher = voucherRepository.save(voucher);

        // 2. Reconcile Bill Totals and State
        BigDecimal newPaidTotal = bill.getPaidAmount().add(request.amountPaid());
        bill.setPaidAmount(newPaidTotal);

        if (newPaidTotal.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }

        billRepository.save(bill);

        return mapToVoucherResponse(savedVoucher);
    }

    // ==========================================
    // MAPPERS (Pure Java 21 Records)
    // ==========================================

    private VendorResponse mapToVendorResponse(Vendor vendor) {
        return new VendorResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getContactPerson(),
                vendor.getPhone(),
                vendor.getEmail(),
                vendor.getAddress(),
                vendor.getTaxIdentificationNumber(),
                vendor.getDefaultCategory()
        );
    }

    private VendorBillResponse mapToBillResponse(VendorBill bill) {
        BigDecimal remaining = bill.getTotalAmount().subtract(bill.getPaidAmount());
        return new VendorBillResponse(
                bill.getId(),
                bill.getVendor().getId(),
                bill.getVendor().getName(),
                bill.getBillNumber(),
                bill.getBillDate(),
                bill.getDueDate(),
                bill.getTotalAmount(),
                bill.getPaidAmount(),
                remaining,
                bill.getStatus(),
                bill.getCategory(),
                bill.getDescription()
        );
    }

    private VoucherResponse mapToVoucherResponse(PaymentVoucher voucher) {
        return new VoucherResponse(
                voucher.getId(),
                voucher.getVoucherNumber(),
                voucher.getBill().getId(),
                voucher.getBill().getBillNumber(),
                voucher.getAmountPaid(),
                voucher.getPaymentDate(),
                voucher.getPaymentMode(),
                voucher.getTransactionReference(),
                voucher.getStatus(),
                voucher.getCreatedBy().getUsername(),
                voucher.getApprovedBy() != null ? voucher.getApprovedBy().getUsername() : null,
                voucher.getRemarks()
        );
    }
}