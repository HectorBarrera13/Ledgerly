package toast.appback.src.groups.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.groups.application.exceptions.AddMemberException;
import toast.appback.src.groups.application.usecase.implementation.AddMemberUseCase;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupMember;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.Phone;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddMemberTest {

    private GroupRepository groupRepository;
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    private AddMemberUseCase useCase;

    @BeforeEach
    void setup() {
        groupRepository = mock(GroupRepository.class);
        memberRepository = mock(MemberRepository.class);
        userRepository = mock(UserRepository.class);

        useCase = new AddMemberUseCase(groupRepository, memberRepository, userRepository);
    }

    @Test
    void testAddMembersSuccessfully() {
        GroupId groupId = GroupId.generate();
        UserId creatorId = UserId.generate();

        Group group = mock(Group.class);
        when(group.getId()).thenReturn(groupId);
        when(group.getCreatorId()).thenReturn(creatorId);

        // Actor (el creador)
        User actor = mock(User.class);
        when(actor.getUserId()).thenReturn(creatorId);

        // Miembros a agregar
        UserId member1 = UserId.generate();
        UserId member2 = UserId.generate();

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getUserId()).thenReturn(member1);
        when(user1.getName()).thenReturn(Name.load("Juan","Perez"));
        when(user1.getPhone()).thenReturn(Phone.load("+52","9991112233"));

        when(user2.getUserId()).thenReturn(member2);
        when(user2.getName()).thenReturn(Name.load("Luis","Ramirez"));
        when(user2.getPhone()).thenReturn(Phone.load("+52","9995556677"));

        // Mock repos
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(creatorId)).thenReturn(Optional.of(actor));
        when(userRepository.findById(member1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(member2)).thenReturn(Optional.of(user2));

        when(memberRepository.exists(eq(groupId), any())).thenReturn(false);

        AddMemberCommand command = new AddMemberCommand(groupId, creatorId, List.of(member1, member2));

        List<UserView> result = useCase.execute(command);

        assertEquals(2, result.size());
        verify(memberRepository, times(2)).save(any(GroupMember.class));
    }

    @Test
    void testFailsWhenActorIsNotCreator() {
        GroupId groupId = GroupId.generate();
        UserId creatorId = UserId.generate();
        UserId actorId = UserId.generate();

        Group group = mock(Group.class);
        when(group.getCreatorId()).thenReturn(creatorId);

        User actor = mock(User.class);
        when(actor.getUserId()).thenReturn(actorId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(actorId)).thenReturn(Optional.of(actor));

        AddMemberCommand command = new AddMemberCommand(groupId, actorId, List.of());

        assertThrows(AddMemberException.class, () -> useCase.execute(command));
    }

    @Test
    void testIgnoresExistingMembers() {
        GroupId groupId = GroupId.generate();
        UserId creatorId = UserId.generate();
        UserId memberId = UserId.generate();

        Group group = mock(Group.class);
        when(group.getId()).thenReturn(groupId);
        when(group.getCreatorId()).thenReturn(creatorId);

        User actor = mock(User.class);
        when(actor.getUserId()).thenReturn(creatorId);

        User user = mock(User.class);
        when(user.getUserId()).thenReturn(memberId);
        when(user.getName()).thenReturn(Name.load("Mario","Lopez"));
        when(user.getPhone()).thenReturn(Phone.load("+52","9998887777"));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(creatorId)).thenReturn(Optional.of(actor));
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));

        when(memberRepository.exists(eq(groupId), any())).thenReturn(true);

        AddMemberCommand command = new AddMemberCommand(groupId, creatorId, List.of(memberId));

        List<UserView> result = useCase.execute(command);

        assertEquals(0, result.size());
        verify(memberRepository, never()).save(any());
    }
}
