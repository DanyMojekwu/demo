package com.example.demo.controller;

import com.example.demo.dto.JwtDto;
import com.example.demo.dto.VerifyDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class UserController {
    UserRepository userRepository;
    JwtService jwtService;

    @GetMapping("/get")
    public ResponseEntity<List<User>> get (){
        var get =userRepository.findAll();
        return new ResponseEntity<>(get, HttpStatus.OK);

    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody VerifyDto verifyDto) {
        System.out.println("verify endpoint");
        var email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println("Email from auth:"+email);

        var user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        if (!(user.getVerificationCode() == verifyDto.getVerificationCode()))  return new ResponseEntity<>(Map.of("message", "Incorrect verification code"), HttpStatus.BAD_REQUEST);
        if (!user.getVerificationExpiry().after(new Date())) return new ResponseEntity<>(Map.of("message", "Verification code has expired"), HttpStatus.BAD_REQUEST);
        user.setVerify(true);
        user.setVerificationCode(0);

        return new ResponseEntity<User>(userRepository.save(user),HttpStatus.OK);

    }


}
