package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.groups.application.communication.command.CreateGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.exceptions.CreationGroupException;
import toast.appback.src.groups.application.usecase.contract.CreateGroup;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.groups.domain.vo.GroupMember;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateGroupUseCase implements CreateGroup {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public CreateGroupUseCase(
            GroupRepository groupRepository,
            UserRepository userRepository,
            MemberRepository memberRepository
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Group execute(CreateGroupCommand command) {
        User creator = userRepository.findById(command.creatorId())
                .orElseThrow(() -> new UserNotFound(command.creatorId()));

        Result<GroupInformation, DomainError> groupInfoResult = GroupInformation.create(
                command.name(),
                command.description()
        );

        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(groupInfoResult);

        emptyResult.ifFailureThrows(CreationGroupException::new);

        GroupInformation groupInformation = groupInfoResult.get();
        Group group = Group.create(
                groupInformation,
                creator.getUserId()
        );

        groupRepository.save(group);

        return group;
    }
}
