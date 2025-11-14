package toast.appback.src.users.domain;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.event.FriendAdded;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendShip {
    private final UserId firstUser;
    private final UserId secondUser;
    private final Instant createdAt;
    private List<DomainEvent> friendshipEvents = new ArrayList<>();

    private FriendShip(UserId firstUser, UserId secondUser, Instant createdAt) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.createdAt = createdAt;
    }

    private FriendShip(UserId firstUser, UserId secondUser, Instant createdAt, List<DomainEvent> friendshipEvents) {
        this(firstUser, secondUser, createdAt);
        this.friendshipEvents = new ArrayList<>(friendshipEvents);
    }

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

    public static FriendShip load(UserId firstUser, UserId secondUser, Instant createdAt) {
        return new FriendShip(firstUser, secondUser, createdAt);
    }


    public UserId getFirstUser() {
        return firstUser;
    }

    public UserId getSecondUser() {
        return secondUser;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(friendshipEvents);
        friendshipEvents.clear();
        return events;
    }

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