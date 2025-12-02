package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.exceptions.ExistingFriendShipException;
import toast.appback.src.users.application.exceptions.FriendToMySelfException;
import toast.appback.src.users.application.exceptions.ReceiverNotFound;
import toast.appback.src.users.application.exceptions.RequesterNotFound;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.usecase.implementation.AddFriendUseCase;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Add friend use case tests")
class AddFriendTest {
    private final FriendShipRepository friendShipRepository = mock(FriendShipRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final DomainEventBus domainEventBus = mock(DomainEventBus.class);
    private AddFriendUseCase addFriendUseCase;

    @BeforeEach
    void setUp() {
        this.addFriendUseCase = new AddFriendUseCase(
                friendShipRepository,
                userRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should add a friend successfully")
    void shouldAddFriendSuccessfully() {
        User userA = UserMother.validUser();
        User userB = UserMother.validUser();

        when(userRepository.findById(userA.getUserId()))
                .thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getUserId()))
                .thenReturn(Optional.of(userB));
        when(friendShipRepository.existsFriendShip(
                userA.getUserId(),
                userB.getUserId()
        )).thenReturn(false);

        AddFriendCommand command = new AddFriendCommand(
                userA.getUserId(),
                userB.getUserId()
        );

        var result = addFriendUseCase.execute(command);

        assertNotNull(result);
        assertEquals(userB.getUserId().getValue(), result.userId());
        assertEquals(userB.getName().getFirstName(), result.firstName());
        assertEquals(userB.getName().getLastName(), result.lastName());
        assertEquals(userB.getPhone().getValue(), result.phone());

        verify(userRepository, times(1)).findById(userA.getUserId());
        verify(userRepository, times(1)).findById(userB.getUserId());
        verify(friendShipRepository, times(1)).existsFriendShip(
                userA.getUserId(),
                userB.getUserId()
        );
        verify(domainEventBus, times(1)).publishAll(anyList());
        verify(friendShipRepository, times(1)).save(any(FriendShip.class));

        verifyNoMoreInteractions(
                userRepository,
                friendShipRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should throw FriendToMySelfException when user tries to add himself as")
    void shouldThrowFriendToMySelfException() {
        User userA = UserMother.validUser();

        AddFriendCommand command = new AddFriendCommand(
                userA.getUserId(),
                userA.getUserId()
        );

        assertThrows(
                FriendToMySelfException.class,
                () -> addFriendUseCase.execute(command)
        );

        verifyNoInteractions(
                userRepository,
                friendShipRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should throw RequesterNotFound when requester does not exist")
    void shouldThrowRequesterNotFound() {
        User userA = UserMother.validUser();
        User userB = UserMother.validUser();

        when(userRepository.findById(userA.getUserId()))
                .thenReturn(Optional.empty());

        AddFriendCommand command = new AddFriendCommand(
                userA.getUserId(),
                userB.getUserId()
        );

        assertThrows(
                RequesterNotFound.class,
                () -> addFriendUseCase.execute(command)
        );

        verify(userRepository, times(1)).findById(userA.getUserId());

        verifyNoInteractions(
                friendShipRepository,
                domainEventBus
        );

        verifyNoMoreInteractions(
                userRepository
        );
    }

    @Test
    @DisplayName("Should throw ReceiverNotFound when receiver does not exist")
    void shouldThrowReceiverNotFound() {
        User userA = UserMother.validUser();
        User userB = UserMother.validUser();

        when(userRepository.findById(userA.getUserId()))
                .thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getUserId()))
                .thenReturn(Optional.empty());

        AddFriendCommand command = new AddFriendCommand(
                userA.getUserId(),
                userB.getUserId()
        );

        assertThrows(
                ReceiverNotFound.class,
                () -> addFriendUseCase.execute(command)
        );

        verify(userRepository, times(1)).findById(userA.getUserId());
        verify(userRepository, times(1)).findById(userB.getUserId());

        verifyNoInteractions(
                friendShipRepository,
                domainEventBus
        );

        verifyNoMoreInteractions(
                userRepository
        );
    }

    @Test
    @DisplayName("Should throw ExistingFriendShipException when friendship already exists")
    void shouldThrowExistingFriendShipException() {
        User userA = UserMother.validUser();
        User userB = UserMother.validUser();

        when(userRepository.findById(userA.getUserId()))
                .thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getUserId()))
                .thenReturn(Optional.of(userB));
        when(friendShipRepository.existsFriendShip(
                userA.getUserId(),
                userB.getUserId()
        )).thenReturn(true);

        AddFriendCommand command = new AddFriendCommand(
                userA.getUserId(),
                userB.getUserId()
        );

        assertThrows(
                ExistingFriendShipException.class,
                () -> addFriendUseCase.execute(command)
        );

        verify(userRepository, times(1)).findById(userA.getUserId());
        verify(userRepository, times(1)).findById(userB.getUserId());
        verify(friendShipRepository, times(1)).existsFriendShip(
                userA.getUserId(),
                userB.getUserId()
        );

        verifyNoInteractions(
                domainEventBus
        );

        verifyNoMoreInteractions(
                userRepository,
                friendShipRepository
        );
    }
}
