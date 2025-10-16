package semicolon.carauctionsystem.users.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import semicolon.carauctionsystem.users.data.models.Kyc;

import java.util.UUID;

public interface KycRepository extends JpaRepository<Kyc, UUID> {
}
