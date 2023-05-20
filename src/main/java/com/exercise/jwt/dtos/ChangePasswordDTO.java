package com.exercise.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangePasswordDTO {

    private String currentPassword;

    private String newPasswordFirst;

    private String newPasswordSecond;
}
