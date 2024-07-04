package com.example.nginx_secondchance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDto {
    private String username;
    private String name;
    private String role;
    private String takeaway;
}
