package toast.appback.src.auth.infrastructure.service.worker;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toast.appback.src.auth.infrastructure.persistence.jparepository.JpaSessionRepository;

/**
 * Tarea programada que limpia sesiones expiradas y revocadas periódicamente.
 * <p>
 * Programada para ejecutarse cada 12 horas.
 */
@Component
@RequiredArgsConstructor
public class SessionsCleaner {

    private static final Logger log = LoggerFactory.getLogger(SessionsCleaner.class);
    private final JpaSessionRepository jpaSessionRepository;

    @Scheduled(fixedRate = 12 * 3600000) // every 12 hours
    public void cleanInvalidSessions() {
        boolean cleaned = jpaSessionRepository.cleanExpiredSessions();
        if (cleaned) {
            log.info("Expired and revoked sessions cleaned successfully.");
        } else {
            throw new IllegalStateException("failed to clean expired and revoked sessions.");
        }
    }
}
