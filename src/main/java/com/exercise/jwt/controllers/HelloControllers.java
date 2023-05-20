package com.exercise.jwt.controllers;

import com.exercise.jwt.dtos.ChangePasswordDTO;
import com.exercise.jwt.model.Users;
import com.exercise.jwt.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/hello")
@Slf4j
@RequiredArgsConstructor
public class HelloControllers {

    private final ObjectMapper mapper;


    private final UsersService usersService;


    @GetMapping(value = "")
    public ResponseEntity<String> hello(){
        Authentication user = SecurityContextHolder.getContext().getAuthentication();

        String getUserAuthorize = user.getAuthorities().toString();
        String getCrendential = user.getPrincipal().toString();
        log.info("user authorize"+getUserAuthorize);
        log.info("impelement name:"+ user.getName());
        log.info("get credetials: ",getCrendential);
        String str = getUserAuthorize.substring(1,5);
        if ("USER".equals(str) ){
            return ResponseEntity.ok("Hello from the otherside");
        }
        return ResponseEntity.badRequest().body("Not AUthorized");
    }


    @GetMapping(value = "/listuser")
    public ResponseEntity<Object> getAllUser(){
        return usersService.getAllUsers();

    }
    @PostMapping(value = "/changepassword")
    ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO request){
        Authentication user = SecurityContextHolder.getContext().getAuthentication();

        return usersService.changePassword(request,user.getName());
    }

    @PostMapping(value = "/verifyemail")
    ResponseEntity<Object> verifyEmail(Principal principal){
       ;log.info(principal.getName());

        return usersService.setVerifyEmail(principal.getName());
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Object> confirmUserAccount(@RequestParam("token")String confirmationToken) {
        return usersService.confirmEmail(confirmationToken);
    }

}
