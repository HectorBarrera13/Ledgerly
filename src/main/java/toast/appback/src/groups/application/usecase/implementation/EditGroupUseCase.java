package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.exceptions.GroupEditionException;
import toast.appback.src.groups.application.usecase.contract.EditGroup;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupInformation;

public class EditGroupUseCase implements EditGroup {
    private final GroupRepository groupRepository;

    public EditGroupUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public GroupView execute(EditGroupCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + command.groupId()));

        GroupInformation newInfo = GroupInformation.create(
                command.name(),
                command.description()
        ).orElseThrow(GroupEditionException::new);

        groupRepository.save(group);

        return new GroupView(
                group.getId().getValue(),
                group.getGroupInformation().getName(),
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        );
    }
}
