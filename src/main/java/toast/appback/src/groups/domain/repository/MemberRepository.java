package toast.appback.src.groups.domain.repository;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupMember;

import java.util.List;

public interface  MemberRepository {
    void save(GroupMember member);
    List<GroupMember> findByGroupId(GroupId groupId);
    boolean exists(GroupId groupId, GroupMember member);

}
