package toast.appback.src.shared.application;

import toast.appback.src.shared.domain.DomainEvent;

import java.util.List;

/**
 * Puerto de publicación de eventos de dominio a nivel de aplicación.
 * <p>
 * Implementaciones deben encargarse de entregar los eventos a los manejadores registrados
 * o a un broker/event store según la infraestructura del proyecto.
 */
public interface DomainEventBus {
    /**
     * Publica y entrega todos los eventos proporcionados.
     *
     * @param events Lista de eventos de dominio a publicar.
     */
    void publishAll(List<DomainEvent> events);
}