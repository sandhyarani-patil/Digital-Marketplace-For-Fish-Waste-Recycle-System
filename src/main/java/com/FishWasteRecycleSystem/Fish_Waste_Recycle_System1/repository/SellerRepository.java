package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller,Long> {

    boolean existsByUser_Id(Long userId);
    Seller findByUser(User user);
}
