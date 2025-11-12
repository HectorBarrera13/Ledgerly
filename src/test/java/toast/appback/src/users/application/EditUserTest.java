package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.application.exceptions.domain.UserEditionException;
import toast.appback.src.users.application.usecase.implementation.EditUserUseCase;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edit User Use Case Tests")
public class EditUserTest {
    private EditUserUseCase editUserUseCase;
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        this.editUserUseCase = new EditUserUseCase(
                userRepository
        );
    }

    /**
     * <p>Test case: Edit user successfully
     * <p>Precondition: Valid EditUserCommand is provided
     * <p>Expected outcome: User is edited and saved in the repository
     */
    @Test
    @DisplayName("Should edit user successfully")
    public void testEditUserSuccessfully() {
        User user = mock(User.class);
        // Mock user retrieval
        when(user.getUserId()).thenReturn(UserId.generate());
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        EditUserCommand command = new EditUserCommand(
                user.getUserId(),
                "Jane",
                "Doe"
        );
        User editedUser = editUserUseCase.execute(command);
        // Verify edited user
        assertEquals(user, editedUser);
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    /**
     * <p>Test case: User edit fails due to invalid data
     * <p>Precondition: Invalid EditUserCommand is provided
     * <p>Expected outcome: Exception is thrown indicating the failure
     */
    @Test
    @DisplayName("Should throw exception when user edit fails")
    public void testEditUserFailsDueToInvalidData() {
        User user = mock(User.class);
        // Mock user retrieval
        when(user.getUserId()).thenReturn(UserId.generate());
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        EditUserCommand command = new EditUserCommand(
                user.getUserId(),
                "", // Invalid first name
                "Doe"
        );
        // Execute use case and expect exception
        assertThrows(UserEditionException.class, () -> editUserUseCase.execute(command));
        verify(userRepository, times(1)).findById(any());
        verifyNoMoreInteractions(userRepository);
    }

    /**
     * <p>Test case: User to edit does not exist
     * <p>Precondition: EditUserCommand with non-existing userId is provided
     * <p>Expected outcome: Exception is thrown indicating user not found
     */
    @Test
    @DisplayName("Should throw exception when user to edit does not exist")
    public void testEditUserFailsWhenUserDoesNotExist() {
        // Mock user retrieval to return empty
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        EditUserCommand command = new EditUserCommand(
                UserId.generate(),
                "Jane",
                "Doe"
        );
        // Execute use case and expect exception
        assertThrows(UserNotFound.class, () -> editUserUseCase.execute(command));
        verify(userRepository, times(1)).findById(any());
        verifyNoMoreInteractions(userRepository);
    }
}
