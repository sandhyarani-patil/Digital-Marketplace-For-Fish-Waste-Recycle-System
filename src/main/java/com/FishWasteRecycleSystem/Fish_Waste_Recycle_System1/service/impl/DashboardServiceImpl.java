//package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;
//
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.AdminDashboardDto;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.CompanyDashboardDto;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.SellerDashboardDto;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Company;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.RequirementStatus;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.CompanyRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.OrderRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.DashboardService;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.RequirementRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class DashboardServiceImpl implements DashboardService {
//
//    private final SellerRepository sellerRepository;
//    private final WasteListingRepository wasteListingRepository;
//    private final OrderRepository orderRepository;
//    private final CompanyRepository companyRepository;
//    private final RequirementRepository requirementRepository;
//
//
//    @Override
//    public SellerDashboardDto getSellerDashboard(Long sellerId) {
//
//        Seller seller = sellerRepository.findById(sellerId)
//                .orElseThrow(() ->
//                        new IllegalArgumentException("Seller not found"));
//
//        SellerDashboardDto dto = new SellerDashboardDto();
//
//        dto.setTotalListings(
//                wasteListingRepository.countBySellerSellerId(sellerId));
//
//        dto.setAvailableListings(
//                wasteListingRepository.countBySellerSellerIdAndStatus(
//                        sellerId,
//                        FishWasteStatus.AVAILABLE));
//
//        dto.setSoldListings(
//                wasteListingRepository.countBySellerSellerIdAndStatus(
//                        sellerId,
//                        FishWasteStatus.SOLD));
//
//        dto.setReservedListings(
//                wasteListingRepository.countBySellerSellerIdAndStatus(
//                        sellerId,
//                        FishWasteStatus.RESERVED));
//
//        dto.setAvailableFishWasteKg(
//                seller.getAvailableFishWasteKg());
//
//        dto.setTotalOrders(
//                orderRepository.countByWasteListingSellerSellerId(sellerId));
//
//        return dto;
//    }
//
//    @Override
//    public AdminDashboardDto getAdminDashboard() {
//
//        AdminDashboardDto dto = new AdminDashboardDto();
//
//        dto.setTotalSellers(sellerRepository.count());
//
//        dto.setTotalCompanies(companyRepository.count());
//
//        dto.setTotalListings(wasteListingRepository.count());
//
//        dto.setTotalOrders(orderRepository.count());
//
//        dto.setTotalRequirements(requirementRepository.count());
//
//        dto.setAvailableListings(
//                wasteListingRepository.countByStatus(FishWasteStatus.AVAILABLE));
//
//        dto.setReservedListings(
//                wasteListingRepository.countByStatus(FishWasteStatus.RESERVED));
//
//        dto.setSoldListings(
//                wasteListingRepository.countByStatus(FishWasteStatus.SOLD));
//
//        return dto;
//    }
//
//    @Override
//    public CompanyDashboardDto getCompanyDashboard(Long companyId) {
//
//        Company company = companyRepository.findById(companyId)
//                .orElseThrow(() ->
//                        new IllegalArgumentException("Company not found"));
//
//        CompanyDashboardDto dto = new CompanyDashboardDto();
//
//        // Requirements
//        dto.setTotalRequirements(
//                requirementRepository.countByCompanyCompanyId(companyId));
//
//        dto.setOpenRequirements(
//                requirementRepository.countByCompanyCompanyIdAndStatus(
//                        companyId,
//                        RequirementStatus.OPEN));
//
//        dto.setFulfilledRequirements(
//                requirementRepository.countByCompanyCompanyIdAndStatus(
//                        companyId,
//                        RequirementStatus.FULFILLED));
//
//        // Orders
//        dto.setTotalOrders(
//                orderRepository.countByRequirementCompanyCompanyId(companyId));
//
//        dto.setPendingOrders(
//                orderRepository.countByRequirementCompanyCompanyIdAndStatus(
//                        companyId,
//                        OrderStatus.PENDING));
//
//        dto.setCompletedOrders(
//                orderRepository.countByRequirementCompanyCompanyIdAndStatus(
//                        companyId,
//                        OrderStatus.COMPLETED));
//
//        dto.setCancelledOrders(
//                orderRepository.countByRequirementCompanyCompanyIdAndStatus(
//                        companyId,
//                        OrderStatus.CANCELLED));
//
//        // Purchase Summary
//        dto.setTotalPurchasedKg(
//                orderRepository.getTotalPurchasedKg(companyId));
//
//        dto.setTotalSpentAmount(
//                orderRepository.getTotalSpentAmount(companyId));
//
//        return dto;
//    }
//}

package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.AdminDashboardDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.CompanyDashboardDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.SellerDashboardDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Company;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.RequirementStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.CompanyRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.OrderRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.DashboardService;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.RequirementRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SellerRepository sellerRepository;
    private final WasteListingRepository wasteListingRepository;
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final RequirementRepository requirementRepository;


    @Override
    public SellerDashboardDto getSellerDashboard(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Seller not found"));

        SellerDashboardDto dto = new SellerDashboardDto();

        dto.setTotalListings(
                wasteListingRepository.countBySellerSellerId(sellerId));

        dto.setAvailableListings(
                wasteListingRepository.countBySellerSellerIdAndStatus(
                        sellerId,
                        FishWasteStatus.AVAILABLE));

        dto.setSoldListings(
                wasteListingRepository.countBySellerSellerIdAndStatus(
                        sellerId,
                        FishWasteStatus.SOLD));

        dto.setReservedListings(
                wasteListingRepository.countBySellerSellerIdAndStatus(
                        sellerId,
                        FishWasteStatus.RESERVED));

        // Negative stock जाण्यापासून सुरक्षेसाठी Math.max
        int currentStock = seller.getAvailableFishWasteKg() != null ? seller.getAvailableFishWasteKg() : 0;
        dto.setAvailableFishWasteKg(Math.max(0, currentStock));

        dto.setTotalOrders(
                orderRepository.countByWasteListingSellerSellerId(sellerId));

        return dto;
    }

    @Override
    public AdminDashboardDto getAdminDashboard() {

        AdminDashboardDto dto = new AdminDashboardDto();

        dto.setTotalSellers(sellerRepository.count());

        dto.setTotalCompanies(companyRepository.count());

        dto.setTotalListings(wasteListingRepository.count());

        dto.setTotalOrders(orderRepository.count());

        dto.setTotalRequirements(requirementRepository.count());

        dto.setAvailableListings(
                wasteListingRepository.countByStatus(FishWasteStatus.AVAILABLE));

        dto.setReservedListings(
                wasteListingRepository.countByStatus(FishWasteStatus.RESERVED));

        dto.setSoldListings(
                wasteListingRepository.countByStatus(FishWasteStatus.SOLD));

        return dto;
    }

    @Override
    public CompanyDashboardDto getCompanyDashboard(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Company not found"));

        CompanyDashboardDto dto = new CompanyDashboardDto();

        // Requirements
        dto.setTotalRequirements(
                requirementRepository.countByCompanyCompanyId(companyId));

        dto.setOpenRequirements(
                requirementRepository.countByCompanyCompanyIdAndStatus(
                        companyId,
                        RequirementStatus.OPEN));

        dto.setFulfilledRequirements(
                requirementRepository.countByCompanyCompanyIdAndStatus(
                        companyId,
                        RequirementStatus.FULFILLED));

        // Orders
        dto.setTotalOrders(
                orderRepository.countByRequirementCompanyCompanyId(companyId));

        dto.setPendingOrders(
                orderRepository.countByRequirementCompanyCompanyIdAndStatus(
                        companyId,
                        OrderStatus.PENDING));

        // Completed साठी ACCEPTED वापरा कारण Accept झाल्यावरच खरेदी पूर्ण मानली जाते
        dto.setCompletedOrders(
                orderRepository.countByRequirementCompanyCompanyIdAndStatus(
                        companyId,
                        OrderStatus.ACCEPTED));

        dto.setCancelledOrders(
                orderRepository.countByRequirementCompanyCompanyIdAndStatus(
                        companyId,
                        OrderStatus.CANCELLED));

        // Purchase Summary (Null-Safe)
        BigDecimal totalPurchased = orderRepository.getTotalPurchasedKg(companyId);
        dto.setTotalPurchasedKg(totalPurchased != null ? totalPurchased : BigDecimal.ZERO);

        BigDecimal totalSpent = orderRepository.getTotalSpentAmount(companyId);
        dto.setTotalSpentAmount(totalSpent != null ? totalSpent : BigDecimal.ZERO);

        return dto;
    }
}
