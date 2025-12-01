package toast.appback.src.groups.domain.repository;

import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.vo.GroupId;

import java.util.Optional;

public interface GroupRepository {
    void save (Group group);
    Optional<Group> findById (GroupId id);
}
