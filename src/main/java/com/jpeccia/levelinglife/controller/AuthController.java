package com.jpeccia.levelinglife.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import jakarta.validation.Valid;

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
    public ResponseEntity<?> login(@RequestBody @Valid @Parameter(description = "Dados de login do usuário.") AuthDTO data) {
        try {
            // Verifica se o usuário existe
            User user = this.repository.findByUsername(data.username())
                    .orElseThrow(() -> new SecurityException("Invalid username or password"));

            // Verifica a senha
            if (passwordEncoder.matches(data.password(), user.getPassword())) {
                // Gera o token JWT com expiração de 15 minutos
                String token = this.tokenService.generateToken(user);
                return ResponseEntity.ok(new ResponseDTO(token));
            } else {
                // Considerar limitar tentativas de login aqui
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (SecurityException e) {
            // Log de tentativa de login falhada para análise futura (com precaução de privacidade)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            // Log de exceções inesperadas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Registrar um novo usuário", description = "Cadastra um novo usuário e retorna um token JWT para acesso à API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso, token retornado."),
            @ApiResponse(responseCode = "400", description = "Falha no registro, usuário já existe."),
            @ApiResponse(responseCode = "409", description = "Usuário já registrado.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid @Parameter(description = "Dados de registro do novo usuário.") UserRegisterDTO data) {
        // Verifica se o nome de usuário já existe
        Optional<User> existingUser = this.repository.findByUsername(data.username());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }

        // Verifica se o email já está registrado
        Optional<User> existingEmail = this.repository.findByEmail(data.email());
        if (existingEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email is already registered");
        }

        // Cria um novo usuário
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(data.password())); // Codifica a senha
        newUser.setEmail(data.email());
        newUser.setUsername(data.username());
        newUser.setName(data.name());

        // Salva o novo usuário no banco de dados
        this.repository.save(newUser);

        // Gera um token JWT para o usuário
        String token = this.tokenService.generateToken(newUser);
        return ResponseEntity.ok(new ResponseDTO(token));
    }
}
