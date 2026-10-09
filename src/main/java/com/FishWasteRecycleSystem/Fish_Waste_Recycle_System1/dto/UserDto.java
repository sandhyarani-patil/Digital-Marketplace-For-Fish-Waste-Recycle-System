package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private String email;

    private String phoneNo;
    private Role role;
}
