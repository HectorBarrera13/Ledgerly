package toast.appback.src.users.domain;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.event.FriendAdded;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Agregado de dominio que representa la relación de amistad entre dos usuarios.
 *
 * <p>Contiene los identificadores de los dos usuarios implicados, la fecha de creación y
 * una lista de eventos de dominio relacionados con la relación.
 */
public class FriendShip {
    private final UserId firstUser;
    private final UserId secondUser;
    private final Instant createdAt;
    private final List<DomainEvent> friendshipEvents = new ArrayList<>();

    private FriendShip(UserId firstUser, UserId secondUser, Instant createdAt) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.createdAt = createdAt;
    }

    /**
     * Crea una nueva amistad entre dos usuarios y registra el evento {@link FriendAdded}.
     *
     * @param firstUser  Identificador del usuario que solicita la amistad.
     * @param secondUser Identificador del usuario que recibe la solicitud.
     * @return Instancia de `FriendShip` recién creada.
     */
    public static FriendShip create(UserId firstUser, UserId secondUser) {
        Instant now = Instant.now();
        FriendShip friendship = new FriendShip(firstUser, secondUser, now);
        friendship.recordEvent(
                new FriendAdded(
                        firstUser,
                        secondUser
                )
        );
        return friendship;
    }

    /**
     * Carga una relación de amistad existente a partir de sus valores.
     *
     * @param firstUser  Identificador del primer usuario.
     * @param secondUser Identificador del segundo usuario.
     * @param createdAt  Fecha de creación de la relación.
     * @return Instancia `FriendShip` construida con los valores proporcionados.
     */
    public static FriendShip load(UserId firstUser, UserId secondUser, Instant createdAt) {
        return new FriendShip(firstUser, secondUser, createdAt);
    }


    /**
     * @return Identificador del primer usuario (solicitante).
     */
    public UserId getFirstUser() {
        return firstUser;
    }

    /**
     * @return Identificador del segundo usuario (receptor).
     */
    public UserId getSecondUser() {
        return secondUser;
    }

    /**
     * @return Fecha de creación de la relación de amistad.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Devuelve y elimina los eventos de relación acumulados.
     *
     * @return Lista de eventos de dominio relacionados con la amistad.
     */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(friendshipEvents);
        friendshipEvents.clear();
        return events;
    }

    /**
     * Registra un evento de dominio relacionado con la relación de amistad.
     *
     * @param event Evento a registrar.
     */
    public void recordEvent(DomainEvent event) {
        this.friendshipEvents.add(event);
    }

    @Override
    public String toString() {
        return "FriendShip{" +
                ", request=" + firstUser +
                ", receiver=" + secondUser +
                ", addTime=" + createdAt +
                ", friendshipEvents=" + friendshipEvents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FriendShip that)) return false;
        return Objects.equals(firstUser, that.firstUser) && Objects.equals(secondUser, that.secondUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser);
    }
}