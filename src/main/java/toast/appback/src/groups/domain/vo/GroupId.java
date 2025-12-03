package toast.appback.src.groups.domain.vo;

import java.util.UUID;

public class GroupId {
    private UUID id;

    private GroupId(UUID id) {
        this.id = id;
    };

    public static GroupId generate() {
        return new GroupId(UUID.randomUUID());
    }

    public static GroupId load(UUID id) {
        return new GroupId(id);
    }

    public UUID getValue(){
        return id;
    }
}
