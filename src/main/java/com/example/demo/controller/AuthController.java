package com.example.demo.controller;

import com.example.demo.dto.JwtDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserLoginDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;

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
    public ResponseEntity<UserDto> signup(@RequestBody UserDto user){
        System.out.println(user.getPassword());
       User newUser=new User(
               user.getEmail(),
               user.getLastname(),
               user.getFirstname(),
               bCryptPasswordEncoder.encode(user.getPassword()),
               user.getRole());


       var save =userRepository.save(newUser);

       return new ResponseEntity<>(user, HttpStatus.CREATED);

    }


@PostMapping("/login")

    public  ResponseEntity<JwtDto> login(@RequestBody UserLoginDto logindto) {
       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       logindto.getEmail(),
                       logindto.getPassword()
               )
       );
       var user = userRepository.findByEmail(logindto.getEmail()).orElseThrow(()-> new UsernameNotFoundException("user not found"));

       var token = jwtService.generateToken(user);

       return new ResponseEntity<>(new JwtDto(token),HttpStatus.OK);

}


@ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<HashMap<String, String>> forbiddenError (HttpClientErrorException.Forbidden error){
        var message = error.getMessage();
        var errorJson = new HashMap<String,String>();
        errorJson.put("error_message", message);
        return new ResponseEntity<>(errorJson,HttpStatus.FORBIDDEN);
}
}
