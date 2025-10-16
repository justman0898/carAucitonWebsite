package semicolon.carauctionsystem.users.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import semicolon.carauctionsystem.users.data.models.EventRetry;

import java.util.List;
import java.util.UUID;

public interface EventRetryRepo extends JpaRepository<EventRetry, UUID> {
    List<EventRetry> findByStatus(String status);
    
}
