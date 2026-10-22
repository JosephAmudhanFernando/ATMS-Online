package org.atmsonline.sms.dto.finance;

import jakarta.validation.constraints.*;
import org.atmsonline.sms.entity.finance.BillStatus;
import org.atmsonline.sms.entity.finance.ExpenseCategory;
import org.atmsonline.sms.entity.finance.PaymentMode;
import org.atmsonline.sms.entity.finance.VoucherStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class FinanceDtos {

    // --- VENDOR DTOs ---
    public record CreateVendorRequest(
            @NotBlank(message = "Vendor name is required")
            @Size(max = 150)
            String name,

            String contactPerson,
            String phone,

            @Email(message = "Invalid vendor email")
            String email,

            String address,
            String taxIdentificationNumber,

            @NotNull(message = "Default expense category is required")
            ExpenseCategory defaultCategory
    ) {}

    public record VendorResponse(
            UUID id,
            String name,
            String contactPerson,
            String phone,
            String email,
            String address,
            String taxIdentificationNumber,
            ExpenseCategory defaultCategory
    ) {}

    // --- BILL DTOs ---
    public record CreateBillRequest(
            @NotNull(message = "Vendor ID is required")
            UUID vendorId,

            @NotBlank(message = "Bill number is required")
            @Size(max = 50)
            String billNumber,

            @NotNull(message = "Bill date is required")
            LocalDate billDate,

            @NotNull(message = "Due date is required")
            LocalDate dueDate,

            @NotNull(message = "Total amount is required")
            @DecimalMin(value = "0.01", message = "Bill amount must be greater than zero")
            BigDecimal totalAmount,

            @NotNull(message = "Expense category is required")
            ExpenseCategory category,

            String description
    ) {}

    public record VendorBillResponse(
            UUID id,
            UUID vendorId,
            String vendorName,
            String billNumber,
            LocalDate billDate,
            LocalDate dueDate,
            BigDecimal totalAmount,
            BigDecimal paidAmount,
            BigDecimal remainingAmount,
            BillStatus status,
            ExpenseCategory category,
            String description
    ) {}

    // --- VOUCHER DTOs ---
    public record CreateVoucherRequest(
            @NotNull(message = "Bill ID is required")
            UUID billId,

            @NotBlank(message = "Voucher number is required")
            String voucherNumber,

            @NotNull(message = "Amount paid is required")
            @DecimalMin(value = "0.01", message = "Disbursement must be greater than zero")
            BigDecimal amountPaid,

            @NotNull(message = "Payment date is required")
            LocalDate paymentDate,

            @NotNull(message = "Payment mode is required")
            PaymentMode paymentMode,

            String transactionReference,
            String remarks
    ) {}

    public record VoucherResponse(
            UUID id,
            String voucherNumber,
            UUID billId,
            String billNumber,
            BigDecimal amountPaid,
            LocalDate paymentDate,
            PaymentMode paymentMode,
            String transactionReference,
            VoucherStatus status,
            String createdByUsername,
            String approvedByUsername,
            String remarks
    ) {}
}