package org.atmsonline.sms.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import org.atmsonline.sms.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendor_bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorBill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false, unique = true, length = 50)
    private String billNumber;

    @Column(nullable = false)
    private LocalDate billDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private BillStatus status = BillStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String invoiceAttachmentPath; // Local or S3 path for receipt file

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PaymentVoucher> vouchers = new ArrayList<>();
}