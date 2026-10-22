package org.atmsonline.sms.repository.finance;

import org.atmsonline.sms.entity.finance.BillStatus;
import org.atmsonline.sms.entity.finance.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, UUID> {
    Optional<VendorBill> findByBillNumber(String billNumber);
    boolean existsByBillNumber(String billNumber);
    List<VendorBill> findByVendorId(UUID vendorId);
    List<VendorBill> findByStatus(BillStatus status);
    List<VendorBill> findByDueDateBeforeAndStatusNot(LocalDate date, BillStatus status);
}