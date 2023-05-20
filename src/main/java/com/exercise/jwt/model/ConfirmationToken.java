package com.exercise.jwt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "M_CONFIRMATIONTOKENS")
public class ConfirmationToken {

    @Id
    @GeneratedValue
    @Column(name="token_id")
    private Long tokenId;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @Column(name = "confimation_token_rp")
    private String confirmationTokenResetPassword;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @OneToOne(targetEntity = Users.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Users user;

}
