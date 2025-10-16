package semicolon.carauctionsystem.users.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import semicolon.carauctionsystem.users.data.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
