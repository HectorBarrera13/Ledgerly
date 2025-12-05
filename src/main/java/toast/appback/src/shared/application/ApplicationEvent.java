package toast.appback.src.shared.application;

import java.time.Instant;

/**
 * Marca un evento a nivel de aplicación. Implementaciones pueden aportar información adicional.
 *
 * @implNote El método `occurredOn` por defecto devuelve la fecha actual; las implementaciones pueden sobrescribirlo.
 */
public interface ApplicationEvent {
    default Instant occurredOn() {
        return Instant.now();
    }
}
