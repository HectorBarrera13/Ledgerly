package toast.appback.src.groups.infrastructure.api.dto;

import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.infrastructure.api.dto.UserResponseMapper;

import java.util.List;

public class GroupResponseMapper {

    public static GroupDetailResponse toGroupDetailResponse(Group group, List<UserView> users) {
        GroupResponse groupResponse = new GroupResponse(
                group.getId().getValue(),
                group.getGroupInformation().getName(),
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        );

        return new GroupDetailResponse(
                groupResponse,
                users.stream()
                        .map(UserResponseMapper::toUserResponse)
                        .toList()
        );
    }

    public static GroupDetailResponse toGroupDetailResponse(GroupView groupView, List<UserView> users) {
        GroupResponse groupResponse = new GroupResponse(
                groupView.groupId(),
                groupView.name(),
                groupView.description(),
                groupView.createdAt()
        );

        return new GroupDetailResponse(
                groupResponse,
                users.stream()
                        .map(UserResponseMapper::toUserResponse)
                        .toList()
        );
    }

    public static GroupResponse toGroupResponse(GroupView groupView) {
        return new GroupResponse(
                groupView.groupId(),
                groupView.name(),
                groupView.description(),
                groupView.createdAt()
        );
    }
}
