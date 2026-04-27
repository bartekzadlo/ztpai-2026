package pl.edu.pk.demo.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pk.demo.auth.model.AppUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
