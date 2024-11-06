package com.jpeccia.levelinglife.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.AuthDTO;
import com.jpeccia.levelinglife.dto.ResponseDTO;
import com.jpeccia.levelinglife.dto.UserRegisterDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.infra.security.TokenService;
import com.jpeccia.levelinglife.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserRepository repository;

    @Autowired
    TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Login do usuário", description = "Autentica o usuário e retorna um token JWT para acesso à API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado."),
            @ApiResponse(responseCode = "400", description = "Falha no login, credenciais inválidas.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Parameter(description = "Dados de login do usuário.") AuthDTO data){
        User user = this.repository.findByUsername(data.username()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(data.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(token));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Registrar um novo usuário", description = "Cadastra um novo usuário e retorna um token JWT para acesso à API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso, token retornado."),
            @ApiResponse(responseCode = "400", description = "Falha no registro, usuário já existe."),
            @ApiResponse(responseCode = "409", description = "Usuário já registrado.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Parameter(description = "Dados de registro do novo usuário.") UserRegisterDTO data){
        Optional<User> user = this.repository.findByUsername(data.username());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(data.password()));
            newUser.setEmail(data.email());
            newUser.setUsername(data.username());
            newUser.setName(data.name());
            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(token));
        }
        return ResponseEntity.badRequest().build();
    }
}
