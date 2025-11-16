package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.exceptions.domain.CreationUserException;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.usecase.implementation.CreateUserUseCase;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserFactory;
import toast.appback.src.users.domain.repository.UserRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Create user use case tests")
public class CreateUserTest {
    private CreateUserUseCase createUserUseCase;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserFactory userFactory = mock(UserFactory.class);

    @BeforeEach
    public void setUp() {
        this.createUserUseCase = new CreateUserUseCase(
                userRepository,
                userFactory
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

        when(userFactory.create(command))
                .thenReturn(Result.ok(user));

        var result = createUserUseCase.execute(command);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userFactory, times(1)).create(command);
        verify(userRepository, times(1)).save(user);

        verifyNoMoreInteractions(
                userRepository,
                userFactory
        );
    }

    @Test
    @DisplayName("Should throw CreationUserException when user creation fails")
    void shouldThrowCreationUserExceptionWhenUserCreationFails() {
        CreateUserCommand command = new CreateUserCommand(
                "Jane",
                "Doe",
                "+12",
                "0987654321"
        );

        when(userFactory.create(command))
                .thenReturn(Result.failure(DomainError.empty()));

        assertThrows(
                CreationUserException.class,
                () -> createUserUseCase.execute(command)
        );

        verify(userFactory, times(1)).create(command);

        verifyNoInteractions(
                userRepository
        );

        verifyNoMoreInteractions(
                userFactory
        );
    }
}
