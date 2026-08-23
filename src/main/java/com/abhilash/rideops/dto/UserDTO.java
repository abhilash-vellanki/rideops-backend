package com.abhilash.rideops.dto;


import com.abhilash.rideops.entities.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    private Set<Role> roles;

}
