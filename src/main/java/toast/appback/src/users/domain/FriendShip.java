package toast.appback.src.users.domain;

import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.event.FriendAdded;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FriendShip {
    private final FriendShipId friendshipId;
    private final User request;
    private final User receiver;
    private final Instant addTime;
    private List<DomainEvent> friendshipEvents = new ArrayList<>();

    public FriendShip(FriendShipId friendshipId, User request, User receiver, Instant addTime) {
        this.friendshipId = friendshipId;
        this.request = request;
        this.receiver = receiver;
        this.addTime = addTime;
    }

    public FriendShip(FriendShipId friendshipId, User request, User receiver, Instant addTime, List<DomainEvent> friendshipEvents) {
        this(friendshipId, request, receiver, addTime);
        this.friendshipEvents = new ArrayList<>(friendshipEvents);
    }

    public static FriendShip create(User request, User receiver) {
        Instant now = Instant.now();
        FriendShip friendship = new FriendShip(null, request, receiver, now);
        friendship.recordEvent(
                new FriendAdded(
                        request.getId(),
                        receiver.getId()
                )
        );
        return friendship;
    }

    public FriendShipId getFriendshipId() {
        return friendshipId;
    }

    public User getRequest() {
        return request;
    }

    public User getReceiver() {
        return receiver;
    }

    public Instant getAddTime() {
        return addTime;
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(friendshipEvents);
        friendshipEvents.clear();
        return events;
    }

    public void recordEvent(DomainEvent event) {
        this.friendshipEvents.add(event);
    }
}