package toast.appback.src.shared.domain;

import java.time.Instant;

/**
 * Interfaz que marca un evento del dominio.
 * <p>
 * Implementaciones pueden añadir metadatos específicos; el método por defecto `occurredOn` devuelve el instante actual.
 */
public interface DomainEvent {
    default Instant occurredOn() {
        return Instant.now();
    }
}
