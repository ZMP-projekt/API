package security;

import model.Role;
import model.User;
import repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("trener@gym.pl").isEmpty()) {
            User trainer = new User();
            trainer.setEmail("trener@gym.pl");
            trainer.setFirstName("Benek");
            trainer.setLastName("Trener");
            trainer.setPassword(passwordEncoder.encode("haslo123")); // Pamiętaj o szyfrowaniu!
            trainer.setRole(Role.ROLE_TRAINER); 
            
            userRepository.save(trainer);
            System.out.println(">>> Automatycznie utworzono konto trenera: trener@gym.pl");
        }

        if (userRepository.findByEmail("auto-test@user.pl").isEmpty()) {
            User user = new User();
            user.setEmail("auto-test@user.pl");
            user.setFirstName("Mikołaj");
            user.setLastName("Klient");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
            System.out.println(">>> Automatycznie utworzono konto użytkownika: auto-test@user.pl");
        }
    }
}