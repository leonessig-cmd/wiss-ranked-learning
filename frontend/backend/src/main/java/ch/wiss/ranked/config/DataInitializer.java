package ch.wiss.ranked.config;

import ch.wiss.ranked.entity.*;
import ch.wiss.ranked.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final RankRepository rankRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initRanks();
        initAdminUser();
    }

    private void initRoles() {
        if (roleRepo.count() == 0) {
            roleRepo.save(Role.builder().name("STUDENT").build());
            roleRepo.save(Role.builder().name("TEACHER").build());
            roleRepo.save(Role.builder().name("ADMIN").build());
            log.info("Rollen initialisiert");
        }
    }

    private void initRanks() {
        if (rankRepo.count() == 0) {
            rankRepo.save(Rank.builder().name("Neuling").minPoints(0).maxPoints(99).build());
            rankRepo.save(Rank.builder().name("Lernender").minPoints(100).maxPoints(299).build());
            rankRepo.save(Rank.builder().name("Fortgeschrittener").minPoints(300).maxPoints(699).build());
            rankRepo.save(Rank.builder().name("Experte").minPoints(700).maxPoints(1499).build());
            rankRepo.save(Rank.builder().name("Meister").minPoints(1500).maxPoints(2999).build());
            rankRepo.save(Rank.builder().name("Legende").minPoints(3000).maxPoints(Integer.MAX_VALUE).build());
            log.info("Ränge initialisiert");
        }
    }

    private void initAdminUser() {
        if (!userRepo.existsByUsername("admin")) {
            Role adminRole = roleRepo.findByName("ADMIN").orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .email("admin@wiss.ch")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build();
            userRepo.save(admin);
            log.info("Admin-Benutzer erstellt: admin / admin123");
        }
    }
}
