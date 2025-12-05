package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.application.exceptions.domain.UserEditionException;
import toast.appback.src.users.application.usecase.contract.EditUser;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Implementación del caso de uso encargado de editar el nombre de un usuario.
 *
 * <p>Busca el usuario, valida el nuevo nombre y persiste la entidad actualizada.
 * Lanza {@link UserNotFound} si el usuario no existe y {@link UserEditionException}
 * si la validación del nombre falla.
 */
public class EditUserUseCase implements EditUser {
    private final UserRepository userRepository;

    public EditUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Ejecuta la edición del usuario.
     *
     * @param command Comando con el identificador y los nuevos nombres.
     * @return Vista (`UserView`) con los datos actualizados.
     * @throws UserNotFound         Si el usuario no existe.
     * @throws UserEditionException Si la validación del nombre falla.
     */
    @Override
    public UserView execute(EditUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFound(command.userId()));

        Name newName = Name.create(command.firstName(), command.lastName())
                .orElseThrow(UserEditionException::new);

        user.changeName(newName);

        userRepository.save(user);

        return new UserView(
                user.getUserId().getValue(),
                user.getName().getFirstName(),
                user.getName().getLastName(),
                user.getPhone().getValue()
        );
    }
}
