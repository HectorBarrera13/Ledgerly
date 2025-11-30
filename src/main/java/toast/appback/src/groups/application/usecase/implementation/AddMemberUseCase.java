package toast.appback.src.groups.application.usecase.implementation;

import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.groups.application.communication.result.MemberView;
import toast.appback.src.groups.application.exceptions.AddMemberException;
import toast.appback.src.groups.application.exceptions.GroupNotFound;
import toast.appback.src.groups.application.usecase.contract.AddMember;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.groups.domain.vo.GroupMember;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class AddMemberUseCase implements AddMember {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public AddMemberUseCase(
            GroupRepository groupRepository,
            MemberRepository memberRepository,
            UserRepository userRepository
    ) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserView> execute(AddMemberCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new GroupNotFound(command.groupId()));

        User actor = userRepository.findById(command.actorId())
                .orElseThrow(() -> new UserNotFound(command.actorId()));

        boolean isActorCreator = actor.getUserId().equals(group.getCreatorId());
        if(!isActorCreator) {
            throw new AddMemberException("Only the group creator can add members");
        }

        List<UserView> newMembers = new ArrayList<>();

        for(var memberId : command.membersId()) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new UserNotFound(memberId));

            GroupMember groupMember = GroupMember.create(group.getId(), user.getUserId());

            if (memberRepository.exists(group.getId(), groupMember)){
                continue;
            }

            UserView memberView = new UserView(
                    user.getUserId().getValue(),
                    user.getName().getFirstName(),
                    user.getName().getLastName(),
                    user.getPhone().getValue()
            );
            newMembers.add(memberView);
            memberRepository.save(groupMember);
        }

       return newMembers;
    }
}
