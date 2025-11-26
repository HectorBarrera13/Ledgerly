package toast.appback.src.groups.domain.repository;

import toast.appback.src.groups.domain.vo.GroupMember;

public interface MemberRepository {
    void save(GroupMember member);
}
