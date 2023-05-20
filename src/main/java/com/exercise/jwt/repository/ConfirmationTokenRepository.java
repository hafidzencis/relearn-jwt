package com.exercise.jwt.repository;

import com.exercise.jwt.model.ConfirmationToken;
import com.exercise.jwt.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;


@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findByConfirmationToken(String confirmationtoken);
    ConfirmationToken findByConfirmationTokenResetPassword(String token);
    Optional<ConfirmationToken> findTopDistinctByUser(Users query);
}
