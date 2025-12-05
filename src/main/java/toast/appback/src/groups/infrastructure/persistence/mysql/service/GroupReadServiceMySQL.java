package toast.appback.src.groups.infrastructure.persistence.mysql.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.response.UserSummaryResponse;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.port.GroupReadRepository;
import toast.appback.src.groups.application.port.GroupReadService;
import toast.appback.src.groups.application.port.MemberReadRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.api.dto.GroupResponseMapper;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.mapping.UserMapper;
import toast.model.entities.group.MemberEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupReadServiceMySQL implements GroupReadService {
    private final GroupReadRepository groupReadRepository;
    private final MemberReadRepository memberReadRepository;
    private final DebtResponseMapper debtResponseMapper;

    @Override
    public List<GroupDetailResponse> getGroupsForUser(UserId userId, PageRequest pageRequest) {
        PageResult<GroupView, UUID> groupViews =
                groupReadRepository.findGroupsByUserId(userId, pageRequest);

        if (groupViews.isEmpty()) {
            return List.of();
        }
        List<UUID> groupIds = groupViews.items().stream()
                .map(GroupView::groupId)
                .toList();

        List<MemberEntity> memberEntities =
                memberReadRepository.findMembersByGroupIds(groupIds);

        Map<UUID, List<UserView>> membersByGroupId =
                memberEntities.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getGroup().getGroupId(),
                                Collectors.mapping(
                                        e -> UserMapper.toUserView(e.getUser()),
                                        Collectors.toList()
                                )
                        ));

        return groupViews.items().stream()
                .map(gv -> GroupResponseMapper.toGroupDetailResponse(
                        gv,
                        membersByGroupId.getOrDefault(gv.groupId(), List.of()).stream()
                                .map(v -> {
                                    UserSummaryView userSummaryView = new UserSummaryView(v.userId(), v.firstName(), v.lastName());
                                    return debtResponseMapper.toUserSummaryResponse(userSummaryView);
                                })
                                .toList()
                ))
                .toList();
    }

    @Override
    public GroupDetailResponse getGroupByIdAndUserId(GroupId groupId, UserId userId, int limit) {
        GroupResponse groupResponse = groupReadRepository.findGroupByIdAndUser(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found or unauthorized"));
        PageRequest page = PageRequest.of(0, limit);

        PageResult<UserSummaryView, UUID> pageResult =
                memberReadRepository.findMembersByGroupId(groupId, page);

        List<UserSummaryResponse> members =
                pageResult.items()
                        .stream()
                        .map(debtResponseMapper::toUserSummaryResponse)
                        .toList();

        return new GroupDetailResponse(groupResponse, members);
    }
}
