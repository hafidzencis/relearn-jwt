package com.exercise.jwt.service;

import com.exercise.jwt.config.JWTService;
import com.exercise.jwt.dtos.LoginDTO;
import com.exercise.jwt.dtos.RegisterDTO;
import com.exercise.jwt.dtos.TokenResponse;
import com.exercise.jwt.model.ConfirmationToken;
import com.exercise.jwt.model.Role;
import com.exercise.jwt.model.Users;
import com.exercise.jwt.repository.ConfirmationTokenRepository;
import com.exercise.jwt.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsersRepository repository;

    private final JWTService jwtService;

    private final AuthenticationManager authmanager;

    private final PasswordEncoder passwordEncoder;

    private final UsersRepository usersRepository;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<Object> register(RegisterDTO registerDTO){
        Users user = Users.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("timestamp", LocalDateTime.now());
        objectMap.put("message","SUCCESS_REGISTERED");

        return new ResponseEntity<>(objectMap, HttpStatus.OK);
    }

    public ResponseEntity<Object> resetPassword(Users request){
        Optional<Users> usersOptional = usersRepository.findByEmail(request.getEmail());

        if (usersOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Tidak ada");
        }


        Optional<ConfirmationToken> optionalConfirmationToken =
                confirmationTokenRepository.findTopDistinctByUser(usersOptional.get());
        log.info(" check ");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(usersOptional.get().getEmail());
        mailMessage.setSubject("Reset Password!");


        if (optionalConfirmationToken.isPresent() ){
            ConfirmationToken confirmationTokenUpdate = optionalConfirmationToken.get();
            confirmationTokenUpdate.setConfirmationTokenResetPassword(UUID.randomUUID().toString());
            confirmationTokenUpdate.setCreatedDate(LocalDateTime.now());
            confirmationTokenRepository.save(confirmationTokenUpdate);

            mailMessage.setText("Reset your password, with the this : " +
                    confirmationTokenUpdate.getConfirmationTokenResetPassword());
            emailService.sendEmail(mailMessage);
            return ResponseEntity.ok("Berhasil di Ubah Bos");
        }

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .confirmationTokenResetPassword(UUID.randomUUID().toString())
                .user(usersOptional.get())
                .build();
        confirmationTokenRepository.save(confirmationToken);

        mailMessage.setText("Reset your password, with the this : " +
                confirmationToken.getConfirmationTokenResetPassword());
        emailService.sendEmail(mailMessage);

        return ResponseEntity.ok("Berhasil di ubah");
    }

    public ResponseEntity<Object> confirmResetPassword(String tokenResetPassword, Users request){
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationTokenResetPassword(tokenResetPassword);
        if (token != null){
            Optional<Users> usersOptional = usersRepository.findByEmail(token.getUser().getEmail());

            Users userUpdate = usersOptional.get();
            userUpdate.setPassword(passwordEncoder.encode(request.getPassword()));

            usersRepository.save(userUpdate);
            return ResponseEntity.ok("Password Udah di ganti");

        }

        return ResponseEntity.badRequest().body("Gagal");
    }

    public ResponseEntity<Object> login(LoginDTO loginDTO){
        log.info("checkingg");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()
        );

        authmanager.authenticate(authenticationToken);

        log.info("success execute auth manager");
        Optional<Users> user = repository.findByEmail(loginDTO.getEmail());
        log.info("Succes find email");
        var jwt = jwtService.generateToken(user);
        log.info("success generate");
        TokenResponse response =  TokenResponse.builder()
                .token(jwt)
                .build();
        log.info("success execute");
        return new ResponseEntity<>(response,HttpStatus.OK);

    }


}
