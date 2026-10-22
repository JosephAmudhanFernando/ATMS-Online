package org.atmsonline.sms.repository.finance;

import org.atmsonline.sms.entity.finance.ExpenseCategory;
import org.atmsonline.sms.entity.finance.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    boolean existsByNameIgnoreCase(String name);
    List<Vendor> findByDefaultCategory(ExpenseCategory category);
}