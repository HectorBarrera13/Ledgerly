package toast.appback.src.groups.infrastructure.api.dto;

import toast.appback.src.debts.infrastructure.api.dto.response.UserSummaryResponse;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;

import java.util.List;

public class GroupResponseMapper {

    private GroupResponseMapper() {
    }

    public static GroupDetailResponse toGroupDetailResponse(Group group, List<UserSummaryResponse> users) {
        GroupResponse groupResponse = new GroupResponse(
                group.getId().getValue(),
                group.getCreatorId().getValue(),
                group.getGroupInformation().getName(),
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        );

        return new GroupDetailResponse(
                groupResponse,
                users
        );
    }

    public static GroupResponse toGroupResponse(Group group) {
        return new GroupResponse(
                group.getId().getValue(),
                group.getCreatorId().getValue(),
                group.getGroupInformation().getName(),
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        );
    }

    public static GroupDetailResponse toGroupDetailResponse(GroupView groupView, List<UserSummaryResponse> users) {
        GroupResponse groupResponse = new GroupResponse(
                groupView.groupId(),
                groupView.creatorId(),
                groupView.name(),
                groupView.description(),
                groupView.createdAt()
        );

        return new GroupDetailResponse(
                groupResponse,
                users
        );
    }

    public static GroupResponse toGroupResponse(GroupView groupView) {
        return new GroupResponse(
                groupView.groupId(),
                groupView.creatorId(),
                groupView.name(),
                groupView.description(),
                groupView.createdAt()
        );
    }
}
