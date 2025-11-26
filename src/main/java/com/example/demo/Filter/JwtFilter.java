package com.example.demo.Filter;

import com.example.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authheader=request.getHeader("Authorization");
        if (authheader == null || !authheader.startsWith("Bearer")) {
            filterChain.doFilter(request,response);
            return;
        }
        String token=authheader.replace("Bearer ","");

        if (!jwtService.verify(token)){
            filterChain.doFilter(request, response);
            return;
        }
        var userEmail = jwtService.getEmailFromToken(token);
        var userRole = jwtService.getRole(token);
        var  authentication= new UsernamePasswordAuthenticationToken(
                userEmail,null, List.of(new SimpleGrantedAuthority("ROLE_"+userRole))
);
        authentication.setDetails(
                new WebAuthenticationDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }


}
