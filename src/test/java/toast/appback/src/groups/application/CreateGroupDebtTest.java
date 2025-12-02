package toast.appback.src.groups.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.groups.application.communication.command.AddGroupDebtCommand;
import toast.appback.src.groups.application.communication.command.GroupDebtorCommand;
import toast.appback.src.groups.application.usecase.implementation.CreateGroupDebtUseCase;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupDebtRepository;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupDebt;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateGroupDebtTest {

        private GroupDebtRepository groupDebtRepository;
        private GroupRepository groupRepository;
        private UserRepository userRepository;
        private CreateDebtBetweenUsers createDebtBetweenUsers;

        private CreateGroupDebtUseCase useCase;

        @BeforeEach
        void setup() {
            groupDebtRepository = mock(GroupDebtRepository.class);
            groupRepository = mock(GroupRepository.class);
            userRepository = mock(UserRepository.class);
            createDebtBetweenUsers = mock(CreateDebtBetweenUsers.class);

            useCase = new CreateGroupDebtUseCase(
                    groupDebtRepository,
                    groupRepository,
                    userRepository,
                    createDebtBetweenUsers
            );
        }

        @Test
        void testCreatesGroupDebtsSuccessfully() {
            GroupId groupId = GroupId.generate();
            UserId creditorId = UserId.generate();

            Group group = mock(Group.class);
            when(group.getId()).thenReturn(groupId);

            User creditor = mock(User.class);
            when(creditor.getUserId()).thenReturn(creditorId);

            UserId debtorId1 = UserId.generate();
            UserId debtorId2 = UserId.generate();

            User debtor1 = mock(User.class);
            User debtor2 = mock(User.class);

            when(debtor1.getUserId()).thenReturn(debtorId1);
            when(debtor2.getUserId()).thenReturn(debtorId2);

            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
            when(userRepository.findById(creditorId)).thenReturn(Optional.of(creditor));
            when(userRepository.findById(debtorId1)).thenReturn(Optional.of(debtor1));
            when(userRepository.findById(debtorId2)).thenReturn(Optional.of(debtor2));

            // Simular resultado de createDebtBetweenUsers
            DebtBetweenUsersView view1 = mock(DebtBetweenUsersView.class);
            DebtBetweenUsersView view2 = mock(DebtBetweenUsersView.class);

            when(view1.debtId()).thenReturn(java.util.UUID.randomUUID());
            when(view2.debtId()).thenReturn(java.util.UUID.randomUUID());

            when(createDebtBetweenUsers.execute(any())).thenReturn(view1, view2);

            AddGroupDebtCommand command = new AddGroupDebtCommand(
                    groupId,
                    creditorId,
                    "Cena",
                    "Pagaste la cena",
                    "MXN",
                    List.of(
                            new GroupDebtorCommand(debtorId1, 100L),
                            new GroupDebtorCommand(debtorId2, 200L)
                    )
            );

            List<DebtBetweenUsersView> result = useCase.execute(command);

            assertEquals(2, result.size());
            verify(createDebtBetweenUsers, times(2)).execute(any());
            verify(groupDebtRepository, times(2)).save(any(GroupDebt.class));
        }

        @Test
        void testFailsWhenGroupNotFound() {
            GroupId groupId = GroupId.generate();
            AddGroupDebtCommand command = new AddGroupDebtCommand(
                    groupId,
                    UserId.generate(),
                    "x", "y", "MXN",
                    List.of()
            );

            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        }

        @Test
        void testFailsWhenCreditorNotFound() {
            GroupId groupId = GroupId.generate();
            UserId creditorId = UserId.generate();

            AddGroupDebtCommand command = new AddGroupDebtCommand(
                    groupId,
                    creditorId,
                    "x", "y", "MXN",
                    List.of()
            );

            when(groupRepository.findById(groupId)).thenReturn(Optional.of(mock(Group.class)));
            when(userRepository.findById(creditorId)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        }

        @Test
        void testFailsWhenDebtorNotFound() {
            GroupId groupId = GroupId.generate();
            UserId creditorId = UserId.generate();
            UserId debtorId = UserId.generate();

            AddGroupDebtCommand command = new AddGroupDebtCommand(
                    groupId,
                    creditorId,
                    "x", "y", "MXN",
                    List.of(new GroupDebtorCommand(debtorId, 100L))
            );

            when(groupRepository.findById(groupId)).thenReturn(Optional.of(mock(Group.class)));
            when(userRepository.findById(creditorId)).thenReturn(Optional.of(mock(User.class)));
            when(userRepository.findById(debtorId)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        }
    }