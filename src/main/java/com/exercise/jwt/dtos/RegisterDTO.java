package com.exercise.jwt.dtos;

import com.exercise.jwt.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {

    private Long id;

    private String name;

    private String email;

    private String password;

}
