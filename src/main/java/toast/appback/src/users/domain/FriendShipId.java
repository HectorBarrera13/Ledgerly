package toast.appback.src.users.domain;

public record FriendShipId(Long id) {
    public static FriendShipId load(Long id) {
        return new FriendShipId(id);
    }
}
