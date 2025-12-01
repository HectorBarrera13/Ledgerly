package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.exceptions.domain.CreationUserException;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.usecase.implementation.CreateUserUseCase;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Create user use case tests")
public class CreateUserTest {
    private CreateUserUseCase createUserUseCase;
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        this.createUserUseCase = new CreateUserUseCase(
                userRepository
        );
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        User user = UserMother.validUser();
        CreateUserCommand command = new CreateUserCommand(
                user.getName().getFirstName(),
                user.getName().getLastName(),
                user.getPhone().getCountryCode(),
                user.getPhone().getNumber()
        );

        var result = createUserUseCase.execute(command);

        assertNotNull(result);
        assertEquals(user.getName().getFirstName(), result.getName().getFirstName());
        assertEquals(user.getName().getLastName(), result.getName().getLastName());
        assertEquals(user.getPhone().getCountryCode(), result.getPhone().getCountryCode());
        assertEquals(user.getPhone().getNumber(), result.getPhone().getNumber());

        verify(userRepository, times(1)).save(result);

        verifyNoMoreInteractions(
                userRepository
        );
    }

    @Test
    @DisplayName("Should throw CreationUserException when user creation fails")
    void shouldThrowCreationUserExceptionWhenUserCreationFails() {
        CreateUserCommand command = new CreateUserCommand(
                "",
                "Doe",
                "+12",
                "0987654321"
        );

        assertThrows(
                CreationUserException.class,
                () -> createUserUseCase.execute(command)
        );

        verifyNoInteractions(
                userRepository
        );
    }
}
