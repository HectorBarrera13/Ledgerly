package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.exceptions.FriendNotFound;
import toast.appback.src.users.application.exceptions.RemoveMySelfFromFriendsException;
import toast.appback.src.users.application.usecase.implementation.RemoveFriendUseCase;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.FriendShipRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Remove friend use case tests")
public class RemoveFriendTest {
    private RemoveFriendUseCase removeFriendUseCase;
    private final FriendShipRepository friendShipRepository = mock(FriendShipRepository.class);
    private final EventBus eventBus = mock(EventBus.class);

    @BeforeEach
    void setUp() {
        removeFriendUseCase = new RemoveFriendUseCase(friendShipRepository, eventBus);
    }

    /**
     * <p>Test case: Remove friend successfully
     * <p>Precondition: Valid RemoveFriendCommand is provided and the friendship exists
     * <p>Expected outcome: Friendship is removed from the repository and FriendRemoved event is published
     */
    @Test
    @DisplayName("Should remove friend successfully")
    public void testRemoveFriendSuccessfully() {
        FriendShip friendShip = mock(FriendShip.class);
        UserId requester = UserId.generate();
        UserId receiver = UserId.generate();
        when(friendShip.getFirstUser()).thenReturn(requester);
        when(friendShip.getSecondUser()).thenReturn(receiver);
        when(friendShipRepository.findByUsersIds(any(), any()))
                .thenReturn(Optional.of(friendShip));
        when(friendShip.getSecondUser()).thenReturn(UserId.generate());
        when(friendShip.getFirstUser()).thenReturn(UserId.generate());
        RemoveFriendCommand command = new RemoveFriendCommand(
                UserId.generate(),
                UserId.generate()
        );
        assertDoesNotThrow(() -> removeFriendUseCase.execute(command));
        verify(friendShipRepository, times(1))
                .delete(friendShip);
        verify(eventBus, times(1)).publish(any());
    }

    /**
     * <p>Test case: Remove friend fails when friendship does not exist
     * <p>Precondition: Valid RemoveFriendCommand is provided but the friendship does not exist
     * <p>Expected outcome: FriendNotFound exception is thrown
     */
    @Test
    @DisplayName("Should throw FriendNotFound when friendship does not exist")
    public void testRemoveFriendFailsWhenFriendshipDoesNotExist() {
        when(friendShipRepository.findByUsersIds(any(), any()))
                .thenReturn(Optional.empty());
        RemoveFriendCommand command = new RemoveFriendCommand(
                UserId.generate(),
                UserId.generate()
        );
        assertThrows(FriendNotFound.class, () -> removeFriendUseCase.execute(command));
        verify(friendShipRepository, times(1))
                .findByUsersIds(command.requesterId(), command.friendId());
        verify(friendShipRepository, times(0)).delete(any());
        verify(eventBus, times(0)).publish(any());
    }

    /**
     * <p>Test case: Remove friend with the same user IDs
     * <p>Precondition: RemoveFriendCommand is provided with the same firstUserId and friendId
     * <p>Expected outcome: RemoveMySelfFromFriendsException is thrown
     */
    @Test
    @DisplayName("Should throw RemoveMySelfFromFriendsException when firstUserId and friendId are the same")
    public void testRemoveFriendWithSameUserIds() {
        UserId userId = UserId.generate();
        RemoveFriendCommand command = new RemoveFriendCommand(
                userId,
                userId
        );
        assertThrows(RemoveMySelfFromFriendsException.class, () -> removeFriendUseCase.execute(command));
        verify(friendShipRepository, times(0)).findByUsersIds(any(), any());
        verify(friendShipRepository, times(0)).delete(any());
        verify(eventBus, times(0)).publish(any());
    }
}
