package org.atmsonline.sms.repository.finance;

import org.atmsonline.sms.entity.finance.PaymentVoucher;
import org.atmsonline.sms.entity.finance.VoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, UUID> {
    Optional<PaymentVoucher> findByVoucherNumber(String voucherNumber);
    boolean existsByVoucherNumber(String voucherNumber);
    List<PaymentVoucher> findByBillId(UUID billId);
    List<PaymentVoucher> findByStatus(VoucherStatus status);
    List<PaymentVoucher> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
}