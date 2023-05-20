package com.exercise.jwt.service;

import com.exercise.jwt.dtos.ChangePasswordDTO;
import com.exercise.jwt.model.ConfirmationToken;
import com.exercise.jwt.model.Users;
import com.exercise.jwt.repository.ConfirmationTokenRepository;
import com.exercise.jwt.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UsersService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;


    public  ResponseEntity<Object> setVerifyEmail(Object request){
        log.info(request.toString());

        Optional<Users> usersOptional = usersRepository.findByEmail(request.toString());

//        ConfirmationToken confirmationToken = new ConfirmationToken(usersOptional.get());

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .createdDate(LocalDateTime.now())
                .confirmationToken(UUID.randomUUID().toString())
                .user(usersOptional.get())
                .build();
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(usersOptional.get().getEmail());
        mailMessage.setSubject("Complete Regristation!");
        mailMessage.setText("Please confirm your account!, click here : http://localhost:8085/v1/hello/" +
                "confirm-account?token="+confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        return ResponseEntity.ok("Coba dulu kirim gmail");
    }
    public ResponseEntity<Object> confirmEmail(String confirmationemail){
        ConfirmationToken token =  confirmationTokenRepository.findByConfirmationToken(confirmationemail);

        if (token != null){
            Optional<Users> users = usersRepository.findByEmail(token.getUser().getEmail());
            Users userUpdate = users.get();
            userUpdate.setIsEnabled(true);
            usersRepository.save(userUpdate);
            return ResponseEntity.ok("Email Verified");
        }

        return ResponseEntity.badRequest().body("Salah");
    }



    public ResponseEntity<Object> getAllUsers(){
        List<Users> users= usersRepository.findAll();
        Map<String,Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message","SUCCESS");
        response.put("data",users);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<Object> changePassword(ChangePasswordDTO request,String email){
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("timestamp",LocalDateTime.now());

        Optional<Users> user = usersRepository.findByEmail(email);

        if (user.isEmpty()){
            responseMap.put("message","ERROR EMPTY USER");
            return new ResponseEntity<>(responseMap,HttpStatus.BAD_REQUEST);
        }

        System.out.println("the password new : " + request.getCurrentPassword());
        System.out.println("the password stored : "+user.get().getPassword());
        if (!passwordEncoder.matches( request.getCurrentPassword(), user.get().getPassword() )){

            System.out.println("coba gagal");
            responseMap.put("message","ERROR PASSWORD NOT MATCHES");
            return new ResponseEntity<>(responseMap,HttpStatus.BAD_REQUEST);
        }

        if ( !request.getNewPasswordFirst().equals(request.getNewPasswordSecond()) ){
            responseMap.put("message","ERROR CAUSE NEW PASSWORD NOT SAME");
            return new ResponseEntity<>(responseMap,HttpStatus.BAD_REQUEST);
        }

        Users userUpdate = user.get();
        userUpdate.setPassword(passwordEncoder.encode(request.getNewPasswordFirst()));

        usersRepository.save(userUpdate);

        responseMap.put("message","SUCCESS_CHANGE_THE_PASSWORD");
        return new ResponseEntity<>(responseMap,HttpStatus.OK);
    }


}
