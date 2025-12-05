package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.application.ApplicationEventBus;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.event.FriendShipBroke;
import toast.appback.src.users.application.exceptions.FriendShipNotFound;
import toast.appback.src.users.application.exceptions.RemoveMySelfFromFriendsException;
import toast.appback.src.users.application.usecase.contract.RemoveFriend;
import toast.appback.src.users.domain.repository.FriendShipRepository;

/**
 * Implementación del caso de uso que elimina una relación de amistad entre dos usuarios.
 *
 * <p>Valida que los identificadores no sean iguales, que exista la relación y la elimina.
 * Publica un evento {@link FriendShipBroke} tras la eliminación.
 */
public class RemoveFriendUseCase implements RemoveFriend {
    private final FriendShipRepository friendShipRepository;
    private final ApplicationEventBus applicationEventBus;

    public RemoveFriendUseCase(FriendShipRepository friendShipRepository,
                               ApplicationEventBus applicationEventBus) {
        this.friendShipRepository = friendShipRepository;
        this.applicationEventBus = applicationEventBus;
    }

    /**
     * Ejecuta la eliminación de una amistad.
     *
     * @param command Comando con el identificador del solicitante y del amigo a eliminar.
     * @throws RemoveMySelfFromFriendsException Si el usuario intenta eliminarse a sí mismo.
     * @throws FriendShipNotFound               Si no existe la relación entre los usuarios.
     */
    @Override
    public void execute(RemoveFriendCommand command) {
        if (command.requesterId().equals(command.friendId())) {
            throw new RemoveMySelfFromFriendsException();
        }

        boolean existsFriendShip = friendShipRepository.existsFriendShip(
                command.requesterId(),
                command.friendId()
        );

        if (!existsFriendShip) {
            throw new FriendShipNotFound(command.requesterId(), command.friendId());
        }

        friendShipRepository.delete(command.requesterId(), command.friendId());

        applicationEventBus.publish(
                new FriendShipBroke(command.requesterId(), command.friendId())
        );
    }
}
