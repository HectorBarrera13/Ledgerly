package toast.appback.src.users.domain;

import toast.appback.src.shared.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private final UserId id;
    private Name name;
    private final Phone phone;
    private List<DomainEvent> domainEvents = new ArrayList<>();

    public User(UserId id, Name name, Phone phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public User(UserId id, Name name, Phone phone, List<DomainEvent> domainEvents) {
        this(id, name, phone);
        this.domainEvents = new ArrayList<>(domainEvents);
    }

    public UserId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
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
                "accountId=" + id +
                ", name=" + name +
                ", phone=" + phone +
                ", domainEvents=" + domainEvents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
