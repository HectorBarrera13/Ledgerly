package toast.appback.src.users.domain;

import toast.appback.src.shared.DomainEvent;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.domain.event.UserFriendAdded;
import toast.appback.src.users.domain.event.UserFriendRemoved;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {
    private final UserId id;
    private Name name;
    private Phone phone;
    private List<Friend> friends;
    private List<DomainEvent> domainEvents = new ArrayList<>();

    public User(UserId id, Name name, Phone phone, List<Friend> friends) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.friends = new ArrayList<>(friends);
    }

    public User(UserId id, Name name, Phone phone, List<Friend> friends, List<DomainEvent> domainEvents) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.friends = friends;
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

    public List<Friend> getFriends() {
        return Collections.unmodifiableList(friends);
    }

    public List<DomainEvent> pullDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public void changeName(Name newName) {
        this.name = newName;
    }

    public void addFriend(Friend newFriend) {
        this.friends.add(newFriend);
        addDomainEvent(new UserFriendAdded(
                this.id,
                newFriend.friendId()
        ));
    }

    public Result<Void, DomainError> removeFriend(UserId friendId) {
        boolean result = this.friends.removeIf(friend -> friend.friendId().equals(friendId));
        if (!result) {
            return Result.failure(DomainError.businessRule("Friend with ID " + friendId.uuid() + " not found."));
        } else {
            addDomainEvent(new UserFriendRemoved(
                    this.id,
                    friendId
            ));
            return Result.success();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "accountId=" + id +
                ", name=" + name +
                ", phone=" + phone +
                ", friends=" + friends +
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
