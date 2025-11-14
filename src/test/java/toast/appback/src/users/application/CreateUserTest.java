package toast.appback.src.users.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.exceptions.domain.CreationUserException;
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

    /**
     * <p>Test case: Create user successfully
     * <p>Precondition: Valid CreateUserCommand is provided
     * <p>Expected outcome: User is created and saved in the repository
     */
    @Test
    @DisplayName("Should create user successfully")
    public void testCreateUserSuccessfully() {
        // Mock user creation
        User user = mock(User.class);
        when(userFactory.create(any()))
                .thenReturn(Result.success(user));

        // Execute use case
        CreateUserCommand command = new CreateUserCommand(
                "John",
                "Doe",
                "+1",
                "1234567890"
        );
        User createdUser = createUserUseCase.execute(command);
        // Verify created user
        assertEquals(user, createdUser);
        verify(userFactory, times(1)).create(any());
        verify(userRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    /**
     * <p>Test case: User creation fails due to invalid data
     * <p>Precondition: UserFactory returns a failure result
     * <p>Expected outcome: CreationUserException is thrown
     */
    @Test
    @DisplayName("Should throw CreationUserException when user creation fails")
    public void testCreateUserFailsDueToInvalidData() {
        // Mock user creation failure
        when(userFactory.create(any()))
                .thenReturn(Result.failure(mock(toast.appback.src.shared.domain.DomainError.class)));

        // Execute use case
        CreateUserCommand command = new CreateUserCommand(
                "John",
                "Doe",
                "+1",
                "1234567890"
        );
        assertThrows(
                CreationUserException.class,
                () -> createUserUseCase.execute(command)
        );
        verify(userFactory, times(1)).create(any());
        verifyNoInteractions(userRepository);
    }
}
