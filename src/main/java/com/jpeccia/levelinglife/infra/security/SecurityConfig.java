package com.jpeccia.levelinglife.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Desativa o CSRF para APIs REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // JWT será usado, então Stateless Session
            .authorizeHttpRequests(authorize -> authorize
                // Permitir acesso público aos endpoints de autenticação e registro
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                
                // Permitir apenas usuários autenticados para todos os outros endpoints
                .requestMatchers(HttpMethod.GET, "/api/users/{username}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated()

                .requestMatchers(HttpMethod.GET, "/api/quests/user/{userId}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/quests/add").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/quests/{id}/complete").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/quests/{id}").authenticated()

                .anyRequest().authenticated()  // Qualquer outra requisição também requer autenticação
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);  // Adiciona o filtro JWT antes do filtro padrão de autenticação por senha

        return http.build();
    }

    // Bean para codificação de senha
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para gerenciar autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
