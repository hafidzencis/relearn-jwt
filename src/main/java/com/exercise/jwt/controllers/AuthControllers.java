package com.exercise.jwt.controllers;


import com.exercise.jwt.dtos.LoginDTO;
import com.exercise.jwt.dtos.RegisterDTO;
import com.exercise.jwt.model.Users;
import com.exercise.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@Slf4j
public class AuthControllers {

//    @Autowired
    private final  AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDTO registerDTO){
        return authService.register(registerDTO);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO request){
        log.info(request.getEmail());
       return authService.login(request);
    }

    @RequestMapping(value="/reset-password", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Object> resetPassword(@RequestBody Users requset) {
        return authService.resetPassword(requset);
    }
    @RequestMapping(value="/confirm-password", method= RequestMethod.POST)
    public ResponseEntity<Object> confirmPassword(@RequestParam("token")String confirmationToken,@RequestBody Users req) {
        return authService.confirmResetPassword(confirmationToken, req);
    }
}
