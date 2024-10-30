package com.jpeccia.levelinglife.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        System.out.println("Token recebido: " + token);
        if (token != null) {
            String email = tokenService.validateToken(token);
            System.out.println("email recebido:" + email );
            if (email != null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User Not Found"));
                
                // Cria as autoridades do usuário, aqui usando um exemplo padrão de ROLE_USER
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                
                // Configura o contexto de segurança com a autenticação do usuário
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Autenticação configurada: " + SecurityContextHolder.getContext().getAuthentication());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7); // Remove "Bearer " e retorna apenas o token
    }
}