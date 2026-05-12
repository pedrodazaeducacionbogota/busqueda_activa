package co.gov.educacionbogota.sicobertura.busquedaactiva.config;

import co.gov.educacionbogota.sicobertura.entities.Usuario;
import co.gov.educacionbogota.sicobertura.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeedConfig {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByNombreUsuario("admin").isPresent()) {
                System.out.println("Default user 'admin' already exists.");
                return;
            }

            System.out.println("Creating default user 'admin' / 'password'...");
            Usuario admin = new Usuario();
            admin.setNombreUsuario("admin");
            admin.setClave(passwordEncoder.encode("password"));
            admin.setCorreo("admin@educacionbogota.gov.co");
            admin.setNombres("Admin");
            admin.setApellidos("Sicobertura");
            admin.setTelefono("12345678");
            admin.setNumeroIdentificacion("12345678");
            admin.setActivo(true);
            admin.setFechaCreacion(new java.util.Date());

            repository.save(admin);
            System.out.println("Default user 'admin' created successfully.");
        };
    }
}
