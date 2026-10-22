package org.atmsonline.sms.entity.finance;

import jakarta.persistence.*;
import lombok.*;
import org.atmsonline.sms.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String taxIdentificationNumber; // GSTIN / TIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseCategory defaultCategory;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VendorBill> bills = new ArrayList<>();
}