package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.application.ApplicationEventBus;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.exceptions.FriendShipNotFound;
import toast.appback.src.users.application.exceptions.RemoveMySelfFromFriendsException;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.usecase.implementation.RemoveFriendUseCase;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.FriendShipRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Remove friend use case tests")
public class RemoveFriendTest {
    private RemoveFriendUseCase removeFriendUseCase;
    private final FriendShipRepository friendShipRepository = mock(FriendShipRepository.class);
    private final ApplicationEventBus applicationEventBus = mock(ApplicationEventBus.class);

    @BeforeEach
    void setUp() {
        removeFriendUseCase = new RemoveFriendUseCase(friendShipRepository, applicationEventBus);
    }

    @Test
    @DisplayName("Should remove friend successfully")
    void shouldRemoveFriendSuccessfully() {
        User userA = UserMother.validUser();
        User userB = UserMother.validUser();
        RemoveFriendCommand command = new RemoveFriendCommand(
                userA.getUserId(),
                userB.getUserId()
        );

        when(friendShipRepository.existsFriendShip(
                command.requesterId(),
                command.friendId()
        )).thenReturn(true);

        removeFriendUseCase.execute(command);

        verify(friendShipRepository, times(1)).existsFriendShip(
                command.requesterId(),
                command.friendId()
        );
        verify(friendShipRepository, times(1)).delete(
                command.requesterId(),
                command.friendId()
        );

        verify(applicationEventBus, times(1)).publish(any());

        verifyNoMoreInteractions(
                friendShipRepository,
                applicationEventBus
        );
    }

    @Test
    @DisplayName("Should throw exception when trying to remove self from friends")
    void shouldThrowWhenRemovingSelf() {
        User userA = UserMother.validUser();
        RemoveFriendCommand command = new RemoveFriendCommand(
                userA.getUserId(),
                userA.getUserId()
        );

        assertThrows(RemoveMySelfFromFriendsException.class, () -> {
            removeFriendUseCase.execute(command);
        });

        verifyNoMoreInteractions(
                friendShipRepository,
                applicationEventBus
        );
    }

    @Test
    @DisplayName("Should throw FriendNotFound when friendship does not exist")
    void shouldThrowFriendNotFoundWhenFriendshipDoesNotExist() {
        User userA = UserMother.validUser();
        User userB = UserMother.validUser();
        RemoveFriendCommand command = new RemoveFriendCommand(
                userA.getUserId(),
                userB.getUserId()
        );

        when(friendShipRepository.existsFriendShip(
                command.requesterId(),
                command.friendId()
        )).thenReturn(false);

        assertThrows(FriendShipNotFound.class, () -> {
            removeFriendUseCase.execute(command);
        });

        verify(friendShipRepository, times(1)).existsFriendShip(
                command.requesterId(),
                command.friendId()
        );

        verifyNoMoreInteractions(
                friendShipRepository,
                applicationEventBus
        );
    }
}
