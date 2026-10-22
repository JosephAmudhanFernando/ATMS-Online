package org.atmsonline.sms.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import org.atmsonline.sms.entity.BaseEntity;
import org.atmsonline.sms.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVoucher extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String voucherNumber; // E.g., VOUCH-2026-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private VendorBill bill;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMode paymentMode;

    @Column(length = 100)
    private String transactionReference; // Cheque No / UTR / Transaction ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private VoucherStatus status = VoucherStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}