package com.example.demo.controller;

import com.example.demo.dto.JwtDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.VerifyDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    public  ResponseEntity<?> login(@RequestBody UserLoginDto logindto, HttpServletResponse response) {
    var user = userRepository.findByEmail(logindto.getEmail()).orElseThrow(()-> new UsernameNotFoundException("user not found"));
//    if (!user.isVerify()) return new ResponseEntity<>(Map.of("message","User not verified"), HttpStatus.FORBIDDEN);
    var authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       logindto.getEmail(),
                       logindto.getPassword()
               )
       );
    SecurityContextHolder.getContext().setAuthentication(authentication);


       var authToken = jwtService.generateAuthToken(user);

//       generate refresh token and save it to the cookie
       var refreshToken = jwtService.generateRefreshToken(user);
        Cookie cookie = new Cookie("refreshToken",refreshToken);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);


       return new ResponseEntity<>(new JwtDto(authToken),HttpStatus.OK);

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
    @PostMapping("/refresh")
    public ResponseEntity<?> getNewAuthToken(@CookieValue("refreshToken") String refreshToken){
        if(refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","refresh token missing"));

        if (!jwtService.verify(refreshToken)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","refresh token expired"));

        var id = Long.parseLong(jwtService.getPayload(refreshToken).getSubject());
        var user = userRepository.findById(id).orElse(null);

        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","user not found"));

        var newAuthToken = jwtService.generateAuthToken(user);

        var jwtDto = new JwtDto(newAuthToken);

        return new ResponseEntity<>(jwtDto,HttpStatus.OK);

    }




}
