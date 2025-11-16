package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.application.exceptions.domain.UserEditionException;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.usecase.implementation.EditUserUseCase;
import toast.appback.src.users.domain.User;
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

    @Test
    @DisplayName("Should edit user successfully")
    void shouldEditUserSuccessfully() {
        User user = UserMother.validUser();
        EditUserCommand command = new EditUserCommand(
                user.getUserId(),
                "Jane",
                "Smith"
        );

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        var result = editUserUseCase.execute(command);
        assertNotNull(result);
        assertEquals(command.userId().getValue(), result.userId());
        assertEquals(command.firstName(), result.firstName());
        assertEquals(command.lastName(), result.lastName());
        assertEquals(user.getPhone().getValue(), result.phone());

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).save(user);

        verifyNoMoreInteractions(
                userRepository
        );
    }

    @Test
    @DisplayName("Should throw UserNotFound when user does not exist")
    void shouldThrowUserNotFoundWhenUserDoesNotExist() {
        User user = UserMother.validUser();
        EditUserCommand command = new EditUserCommand(
                user.getUserId(),
                "Jane",
                "Smith"
        );
        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> {
            editUserUseCase.execute(command);
        });

        verify(userRepository, times(1)).findById(user.getUserId());

        verifyNoMoreInteractions(
                userRepository
        );
    }

    @Test
    @DisplayName("Should throw UserEditionException when command data is invalid")
    void shouldThrowUserEditionExceptionWhenNameIsInvalid() {
        User user = UserMother.validUser();
        EditUserCommand command = new EditUserCommand(
                user.getUserId(),
                "",
                "Smith"
        );

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        assertThrows(UserEditionException.class, () -> {
            editUserUseCase.execute(command);
        });

        verify(userRepository, times(1)).findById(user.getUserId());

        verifyNoMoreInteractions(
                userRepository
        );
    }
}
