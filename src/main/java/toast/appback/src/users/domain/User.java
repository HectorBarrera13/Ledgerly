package toast.appback.src.users.domain;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.event.UserCreated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private final UserId userId;
    private Name name;
    private final Phone phone;
    private final Instant createdAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private User(UserId userId, Name name, Phone phone, Instant createdAt) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public static User create(Name name, Phone phone) {
        UserId userId = UserId.generate();
        User user = new User(userId, name, phone, Instant.now());
        user.recordEvent(new UserCreated(userId, name));
        return user;
    }

    public static User load(UserId userId, Name name, Phone phone, Instant createdAt) {
        return new User(userId, name, phone, createdAt);
    }

    public UserId getUserId() {
        return userId;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }



    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    public void recordEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

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
