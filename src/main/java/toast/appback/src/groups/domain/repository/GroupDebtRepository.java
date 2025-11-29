package toast.appback.src.groups.domain.repository;

import toast.appback.src.groups.domain.vo.GroupDebt;
import toast.appback.src.groups.domain.vo.GroupId;

import java.util.List;

public interface GroupDebtRepository {
    void save(GroupDebt groupDebt);
    List<GroupDebt> findByGroupId(GroupId groupId);
}
