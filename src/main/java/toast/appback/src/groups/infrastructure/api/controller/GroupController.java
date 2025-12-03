package toast.appback.src.groups.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtBetweenUsersResponse;
import toast.appback.src.groups.application.communication.command.AddGroupDebtCommand;
import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.groups.application.communication.result.MemberView;
import toast.appback.src.groups.application.port.GroupReadService;
import toast.appback.src.groups.application.usecase.contract.AddGroupDebt;
import toast.appback.src.groups.application.usecase.contract.AddMember;
import toast.appback.src.groups.infrastructure.api.dto.request.AddGroupDebtRequest;
import toast.appback.src.groups.infrastructure.api.dto.request.AddMemberRequest;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.infrastructure.api.dto.request.EditGroupRequest;
import toast.appback.src.groups.infrastructure.service.transactional.EditGroupService;
import toast.appback.src.shared.infrastructure.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtResponse;
import toast.appback.src.groups.application.communication.command.CreateGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupDebtView;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.port.GroupDebtReadRepository;
import toast.appback.src.groups.application.port.MemberReadRepository;
import toast.appback.src.groups.application.usecase.contract.CreateGroup;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.api.dto.GroupResponseMapper;
import toast.appback.src.groups.infrastructure.api.dto.request.CreateGroupRequest;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.api.dto.UserResponseMapper;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final MemberReadRepository memberReadRepository;
    private final GroupDebtReadRepository groupDebtReadRepository;
    private final EditGroupService editGroupService;
    private final CreateGroup createGroup;
    private final AddMember addMember;
    private final AddGroupDebt addGroupDebt;
    private final GroupReadService groupReadService;

    @PostMapping()
    public ResponseEntity<GroupDetailResponse> createGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateGroupRequest request
    ) {
        UserId userId = customUserDetails.getUserId();
        CreateGroupCommand command = request.toCreateGroupCommand(userId);
        Group group = createGroup.execute(command);
        AddMemberCommand addMemberCommand = request.toAddMemberCommand(group.getId(), userId);
        List<UserView> members = addMember.execute(addMemberCommand);
        GroupDetailResponse response = GroupResponseMapper.toGroupDetailResponse(group, members.subList(0,Math.min(members.size(), 5)));
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<GroupDetailResponse>> getUserGroups(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        UserId userId = customUserDetails.getUserId();
        List<GroupDetailResponse> result = groupReadService.getGroupsForUser(
                userId,
                PageRequest.of(0, limit)
        );

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroupById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("groupId") UUID groupId
    ) {
        UserId userId = customUserDetails.getUserId();
        int limit = 5;
        GroupDetailResponse response = groupReadService.getGroupByIdAndUserId(
                GroupId.load(groupId),
                userId,
                limit
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}/debts")
    public ResponseEntity<Pageable<DebtBetweenUsersResponse, UUID>> getGroupDebts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor,
            @PathVariable("groupId") UUID groupId,
            @RequestParam(value = "role", required = true) String role,
            @RequestParam(value = "status", required = true) String status
    ) {
        UserId userId = customUserDetails.getUserId();
        PageResult<DebtBetweenUsersView, UUID> pageResult;
        if(cursor==null) {
            pageResult = groupDebtReadRepository.findUserDebtsByGroupId(
                    GroupId.load(groupId),
                    userId,
                    role,
                    status,
                    PageRequest.of(0, limit)
            );
        } else {
            pageResult = groupDebtReadRepository.findUserDebtsByGroupIdAfterCursor(
                    GroupId.load(groupId),
                    userId,
                    role,
                    status,
                    CursorRequest.of(limit, cursor)
            );
        }
        if(pageResult.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Pageable.toPageable(pageResult, DebtResponseMapper::toGroupDebtResponse));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<Pageable<UserResponse, UUID>> getGroupMembers(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("groupId") UUID groupId,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        PageResult<MemberView, UUID> pageResult;
        if(cursor==null) {
            pageResult = memberReadRepository.findMembersByGroupId(
                    GroupId.load(groupId),
                    PageRequest.of(0, limit)
            );
        } else {
            pageResult = memberReadRepository.findMembersByGroupIdAfterCursor(
                    GroupId.load(groupId),
                    CursorRequest.of(limit, cursor)
            );
        }
        if(pageResult.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Pageable.toPageable(pageResult, UserResponseMapper :: toUserResponse));
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroupInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("groupId") UUID groupId,
            @RequestBody EditGroupRequest request
    ) {
        EditGroupCommand command = request.toEditGroupCommand(GroupId.load(groupId));
        GroupView groupView = editGroupService.execute(command);
        GroupResponse response = GroupResponseMapper.toGroupResponse(groupView);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/members" )
    public ResponseEntity<Void> addGroupMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("groupId") UUID groupId,
            @RequestBody AddMemberRequest request
    ) {
        UserId userId = customUserDetails.getUserId();
        AddMemberCommand command = request.toAddMemberCommand(GroupId.load(groupId), userId);
        addMember.execute(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group-debt")
    public ResponseEntity<Void> addGroupDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody AddGroupDebtRequest request
    ) {
        UserId userId = customUserDetails.getUserId();
       AddGroupDebtCommand command =
                request.toAddGroupDebtCommand(
                        userId
                );
        addGroupDebt.execute(command);
        return ResponseEntity.ok().build();
    }

}
