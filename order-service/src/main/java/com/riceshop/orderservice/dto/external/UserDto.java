// src/main/java/com/riceshop/orderservice/dto/external/UserDto.java
package com.riceshop.orderservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Set<String> roles;
}