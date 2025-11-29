package com.example.demo.controller;

import com.example.demo.dto.JwtDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.VerifyDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    JwtService jwtService;



    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserDto user){
        int number = (int) (Math.random()*900000)+100000;
       User newUser=new User(
               user.getEmail(),
               user.getLastname(),
               user.getFirstname(),
               bCryptPasswordEncoder.encode(user.getPassword()),
               user.getRole());
       newUser.setVerificationCode(number);
       newUser.setVerificationExpiry(new Date(System.currentTimeMillis()+(4500000)));

       var save =userRepository.save(newUser);

       return new ResponseEntity<>(save, HttpStatus.CREATED);

    }


@PostMapping("/login")

    public  ResponseEntity<?> login(@RequestBody UserLoginDto logindto) {
    var user = userRepository.findByEmail(logindto.getEmail()).orElseThrow(()-> new UsernameNotFoundException("user not found"));
//    if (!user.isVerify()) return new ResponseEntity<>(Map.of("message","User not verified"), HttpStatus.FORBIDDEN);
    var authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       logindto.getEmail(),
                       logindto.getPassword()
               )
       );
    SecurityContextHolder.getContext().setAuthentication(authentication);


       var token = jwtService.generateToken(user);

       return new ResponseEntity<>(new JwtDto(token),HttpStatus.OK);

}


@GetMapping("/request-verification-code")
public ResponseEntity<?> verifyToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = authentication.getName();
        var user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        user.setVerificationExpiry(new Date(System.currentTimeMillis()+4500000));
    int number = (int) (Math.random()*900000)+100000;
    user.setVerificationCode(number);
    userRepository.save(user);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
}


@ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<HashMap<String, String>> forbiddenError (HttpClientErrorException.Forbidden error){
        var message = error.getMessage();
        var errorJson = new HashMap<String,String>();
        errorJson.put("error_message", message);
        return new ResponseEntity<>(errorJson,HttpStatus.FORBIDDEN);
}
}
