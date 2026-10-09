
package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.BadRequestException;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.ResourceNotFoundException;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.WasteListingDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.WasteListingRequestDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.WasteListing;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.Unit;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.WasteListingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WasteListingImpl implements WasteListingService {

    private final WasteListingRepository wasteListingRepository;
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<WasteListingDto> getAllListing() {

        return wasteListingRepository.findAll()
                .stream()
                .map(wasteListing -> {
                    WasteListingDto dto = modelMapper.map(wasteListing, WasteListingDto.class);
                    dto.setSellerId(wasteListing.getSeller().getSellerId());
                    return dto;
                })
                .toList();
    }

    @Override
    public WasteListingDto getWasteListingById(Long wasteListingId) {

        WasteListing wasteListing = wasteListingRepository.findById(wasteListingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Waste Listing not found with id: " + wasteListingId));
        WasteListingDto dto = modelMapper.map(wasteListing, WasteListingDto.class);
        dto.setSellerId(wasteListing.getSeller().getSellerId());

        return dto;
    }

    @Override
    @Transactional
    public WasteListingDto createNewWasteListing(WasteListingRequestDto wasteListingRequestDto) {

        Seller seller = sellerRepository.findById(wasteListingRequestDto.getSellerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seller not found with id: " + wasteListingRequestDto.getSellerId()));

        WasteListing newListing = new WasteListing();

        newListing.setFishType(wasteListingRequestDto.getFishType());
        newListing.setWasteCategory(wasteListingRequestDto.getWasteCategory());
        newListing.setQuantity(wasteListingRequestDto.getQuantity());
        newListing.setUnit(wasteListingRequestDto.getUnit());
        newListing.setPricePerKg(wasteListingRequestDto.getPricePerKg());
        newListing.setDescription(wasteListingRequestDto.getDescription());
        newListing.setPickupLocation(wasteListingRequestDto.getPickupLocation());
        newListing.setAvailableDate(wasteListingRequestDto.getAvailableDate());

        newListing.setStatus(FishWasteStatus.AVAILABLE);
        newListing.setCreatedAt(LocalDateTime.now());
        newListing.setUpdatedAt(LocalDateTime.now());

        newListing.setSeller(seller);

        WasteListing savedListing = wasteListingRepository.save(newListing);

        // Seller dashboard stock update
        int currentStock = seller.getAvailableFishWasteKg() != null ? seller.getAvailableFishWasteKg() : 0;
        int addedQuantity = wasteListingRequestDto.getQuantity().intValue();

        seller.setAvailableFishWasteKg(currentStock + addedQuantity);
        sellerRepository.save(seller);

        WasteListingDto dto = modelMapper.map(savedListing, WasteListingDto.class);
        dto.setSellerId(savedListing.getSeller().getSellerId());

        return dto;
    }

    @Override
    public void deleteWasteListingById(Long wasteListingId) {

        if (!wasteListingRepository.existsById(wasteListingId)) {
            throw new ResourceNotFoundException(
                    "Waste Listing not found with id: " + wasteListingId);
        }

        wasteListingRepository.deleteById(wasteListingId);
    }

    @Override
    public WasteListingDto updateWasteListing(Long wasteListingId,
                                              WasteListingRequestDto wasteListingRequestDto) {

        WasteListing wasteListing = wasteListingRepository.findById(wasteListingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Waste Listing not found with id: " + wasteListingId));

        wasteListing.setFishType(wasteListingRequestDto.getFishType());
        wasteListing.setWasteCategory(wasteListingRequestDto.getWasteCategory());
        wasteListing.setQuantity(wasteListingRequestDto.getQuantity());
        wasteListing.setUnit(wasteListingRequestDto.getUnit());
        wasteListing.setPricePerKg(wasteListingRequestDto.getPricePerKg());
        wasteListing.setDescription(wasteListingRequestDto.getDescription());
        wasteListing.setPickupLocation(wasteListingRequestDto.getPickupLocation());
        wasteListing.setAvailableDate(wasteListingRequestDto.getAvailableDate());

        Seller seller = sellerRepository.findById(wasteListingRequestDto.getSellerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seller not found with id: " + wasteListingRequestDto.getSellerId()));
        wasteListing.setSeller(seller);
        wasteListing.setUpdatedAt(LocalDateTime.now());

        WasteListing updatedListing = wasteListingRepository.save(wasteListing);

        WasteListingDto dto = modelMapper.map(updatedListing, WasteListingDto.class);
        dto.setSellerId(updatedListing.getSeller().getSellerId());

        return dto;
    }

    @Override
    public WasteListingDto updatePartialWasteListing(Long wasteListingId,
                                                     Map<String, Object> updates) {

        WasteListing wasteListing = wasteListingRepository.findById(wasteListingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Waste Listing not found with id: " + wasteListingId));
        updates.forEach((field, value) -> {

            switch (field) {

                case "fishType" -> wasteListing.setFishType((String) value);

                case "wasteCategory" -> wasteListing.setWasteCategory((String) value);

                case "quantity" -> wasteListing.setQuantity(new BigDecimal(value.toString()));

                case "unit" -> wasteListing.setUnit(Unit.valueOf(value.toString()));

                case "pricePerKg" -> wasteListing.setPricePerKg(new BigDecimal(value.toString()));

                case "description" -> wasteListing.setDescription((String) value);

                case "pickupLocation" -> wasteListing.setPickupLocation((String) value);

                case "availableDate" -> wasteListing.setAvailableDate(LocalDate.parse(value.toString()));

                case "status" -> wasteListing.setStatus(FishWasteStatus.valueOf(value.toString()));

                default ->throw new BadRequestException(
                        "Field '" + field + "' is not supported");
            }
        });

        wasteListing.setUpdatedAt(LocalDateTime.now());

        WasteListing savedListing = wasteListingRepository.save(wasteListing);

        WasteListingDto dto = modelMapper.map(savedListing, WasteListingDto.class);
        dto.setSellerId(savedListing.getSeller().getSellerId());

        return dto;
    }
}