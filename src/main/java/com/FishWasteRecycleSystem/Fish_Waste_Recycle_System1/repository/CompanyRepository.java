package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Company;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    boolean existsByRegistrationNo(String registrationNo);

    boolean existsByUser_Id(Long userId);

    Company findByUser(User user);
}
