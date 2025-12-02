package toast.appback.src.groups.domain.vo;

import java.util.UUID;

public class GroupId {
    private UUID id; // Identificador único del grupo

    private GroupId(UUID id) {
        this.id = id;
    };

    public static GroupId generate() {
        return new GroupId(UUID.randomUUID()); // Crea un nuevo GroupId con un UUID generado automáticamente
    }

    public static GroupId load(UUID id) {
        return new GroupId(id); // Crea un GroupId a partir de un UUID existente
    }

    public UUID getValue(){
        return id; // Retorna el UUID interno
    }
}
