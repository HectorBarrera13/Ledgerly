package toast.appback.src.users.domain;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.event.UserCreated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa un usuario del sistema.
 *
 * <p>Contiene el identificador único (`UserId`), el nombre (`Name`), el teléfono (`Phone`),
 * la marca temporal de creación y una lista de eventos de dominio asociados.
 *
 * <p>Notas:
 * - Los métodos `create` y `load` son fábricas para construir la entidad en distintos contextos.
 * - `pullEvents` devuelve los eventos acumulados y los limpia (efecto secundario).
 */
public class User {
    private final UserId userId;
    private final Phone phone;
    private final Instant createdAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private Name name;

    private User(UserId userId, Name name, Phone phone, Instant createdAt) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    /**
     * Crea un nuevo usuario válido con un identificador generado y registra el evento de creación.
     *
     * @param name  Nombre del usuario. Debe ser un objeto `Name` previamente validado.
     * @param phone Teléfono del usuario. Debe ser un objeto `Phone` previamente validado.
     * @return Instancia de `User` recién creada.
     */
    public static User create(Name name, Phone phone) {
        UserId userId = UserId.generate();
        User user = new User(userId, name, phone, Instant.now());
        user.recordEvent(new UserCreated(userId, name));
        return user;
    }

    /**
     * Carga una entidad `User` existente a partir de sus valores (sin registrar eventos).
     *
     * @param userId    Identificador del usuario.
     * @param name      Nombre del usuario.
     * @param phone     Teléfono del usuario.
     * @param createdAt Marca temporal de creación.
     * @return Instancia de `User` construida con los valores proporcionados.
     */
    public static User load(UserId userId, Name name, Phone phone, Instant createdAt) {
        return new User(userId, name, phone, createdAt);
    }

    /**
     * @return El identificador único del usuario.
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * @return El nombre del usuario.
     */
    public Name getName() {
        return name;
    }

    /**
     * @return El teléfono del usuario.
     */
    public Phone getPhone() {
        return phone;
    }

    /**
     * @return Marca temporal de creación del usuario.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }


    /**
     * Devuelve y elimina (efecto secundario) los eventos de dominio acumulados por esta entidad.
     *
     * @return Lista de eventos de dominio registrados hasta el momento.
     */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    /**
     * Registra un evento de dominio en la entidad.
     *
     * @param event Evento de dominio a registrar (no nulo).
     */
    public void recordEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * Cambia el nombre del usuario por otro válido.
     *
     * @param newName Nuevo nombre para el usuario.
     */
    public void changeName(Name newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return "User{" +
                "accountId=" + userId +
                ", name=" + name +
                ", phone=" + phone +
                ", domainEvents=" + domainEvents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
