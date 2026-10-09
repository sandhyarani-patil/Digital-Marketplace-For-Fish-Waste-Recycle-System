
package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Order;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderRepository extends JpaRepository<Order, Long> {

    long countByWasteListingSellerSellerId(Long sellerId);

    long countByRequirementCompanyCompanyId(Long companyId);

    long countByRequirementCompanyCompanyIdAndStatus(
            Long companyId,
            OrderStatus status);

    // o.status = com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus.ACCEPTED वापरा
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.requirement.company.companyId = :companyId
            AND o.status = com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus.ACCEPTED
            """)
    BigDecimal getTotalSpentAmount(@Param("companyId") Long companyId);

    @Query("""
            SELECT COALESCE(SUM(o.orderQuantity), 0)
            FROM Order o
            WHERE o.requirement.company.companyId = :companyId
            AND o.status = com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus.ACCEPTED
            """)
    BigDecimal getTotalPurchasedKg(@Param("companyId") Long companyId);

    boolean existsByRequirementRequirementId(Long requirementId);
}