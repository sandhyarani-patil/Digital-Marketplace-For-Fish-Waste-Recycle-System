package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDashboardDto {

    private Long totalRequirements;

    private Long openRequirements;

    private Long fulfilledRequirements;

    private Long totalOrders;

    private Long pendingOrders;

    private Long completedOrders;

    private Long cancelledOrders;

    private BigDecimal totalPurchasedKg;


    private BigDecimal totalSpentAmount;


}