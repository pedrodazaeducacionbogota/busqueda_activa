package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import co.gov.educacionbogota.sicobertura.busquedaactiva.config.BusquedaActivaJwtUtil;
import co.gov.educacionbogota.sicobertura.entities.Usuario;
import co.gov.educacionbogota.sicobertura.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BusquedaActivaJwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        java.util.Optional<Usuario> userOpt = repository.findByNombreUsuario(loginRequest.getUsername());
        
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getClave())) {
            String token = jwtUtil.generateToken(userOpt.get().getNombreUsuario());
            return ResponseEntity.ok(new JwtResponse(token));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales invalidas");
    }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class JwtResponse {
    private String token;

    public JwtResponse() {}
    public JwtResponse(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
