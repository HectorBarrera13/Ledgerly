package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.exceptions.ReceiverNotFound;
import toast.appback.src.users.application.exceptions.RequesterNotFound;
import toast.appback.src.users.application.usecase.implementation.AddFriendUseCase;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Add friend use case tests")
public class AddFriendTest {
    private AddFriendUseCase addFriendUseCase;
    private final FriendShipRepository friendShipRepository = mock(FriendShipRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final EventBus eventBus = mock(EventBus.class);

    @BeforeEach
    void setUp() {
        this.addFriendUseCase = new AddFriendUseCase(
                friendShipRepository,
                userRepository,
                eventBus
        );
    }

    /**
     * <p>Test case: Add friend successfully
     * <p>Precondition: Both requester and receiver users exist in the repository
     * <p>Expected outcome: FriendShip is created and saved in the repository, events are published
     */
    @Test
    @DisplayName("Happy path: should add friend successfully")
    public void testAddFriendSuccessfully() {
        User requester = mock(User.class);
        User receiver = mock(User.class);
        UserId requesterId = UserId.generate();
        UserId receiverId = UserId.generate();
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(requester.getUserId()).thenReturn(requesterId);
        when(receiver.getUserId()).thenReturn(receiverId);
        AddFriendCommand command = new AddFriendCommand(
                requesterId,
                receiverId
        );
        assertDoesNotThrow(() -> addFriendUseCase.execute(command));
        verify(userRepository, times(2)).findById(any());
        verify(friendShipRepository, times(1)).save(any());
        verify(eventBus, times(1)).publishAll(any());
    }

    /**
     * <p>Test case: Add friend fails when requester user does not exist
     * <p>Precondition: Requester user does not exist in the repository
     * <p>Expected outcome: RequesterNotFound exception is thrown
     */
    @Test
    @DisplayName("Should throw RequesterNotFound when requester does not exist")
    public void testAddFriendRequesterNotFound() {
        UserId requesterId = UserId.generate();
        UserId receiverId = UserId.generate();
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());
        AddFriendCommand command = new AddFriendCommand(
                requesterId,
                receiverId
        );
        assertThrows(RequesterNotFound.class, () -> addFriendUseCase.execute(command));
        verify(userRepository, times(1)).findById(requesterId);
        verify(friendShipRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    /**
     * <p>Test case: Add friend fails when receiver user does not exist
     * <p>Precondition: Receiver user does not exist in the repository
     * <p>Expected outcome: ReceiverNotFound exception is thrown
     */
    @Test
    @DisplayName("Should throw ReceiverNotFound when receiver does not exist")
    public void testAddFriendReceiverNotFound() {
        User requester = mock(User.class);
        UserId requesterId = UserId.generate();
        UserId receiverId = UserId.generate();
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());
        AddFriendCommand command = new AddFriendCommand(
                requesterId,
                receiverId
        );
        assertThrows(ReceiverNotFound.class, () -> addFriendUseCase.execute(command));
        verify(userRepository, times(2)).findById(any());
        verify(friendShipRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }
}
