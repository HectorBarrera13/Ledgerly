package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.exceptions.ExistingFriendShipException;
import toast.appback.src.users.application.exceptions.FriendToMySelfException;
import toast.appback.src.users.application.exceptions.ReceiverNotFound;
import toast.appback.src.users.application.exceptions.RequesterNotFound;
import toast.appback.src.users.application.usecase.contract.AddFriend;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.domain.repository.UserRepository;

/**
 * Implementación del caso de uso que añade una relación de amistad entre dos usuarios.
 *
 * <p>Valida que los usuarios existan, que no sean la misma persona y que no exista una relación
 * previa. Persiste la relación y publica los eventos de dominio generados.
 */
public class AddFriendUseCase implements AddFriend {
    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;
    private final DomainEventBus domainEventBus;

    public AddFriendUseCase(FriendShipRepository friendShipRepository, UserRepository userRepository, DomainEventBus domainEventBus) {
        this.friendShipRepository = friendShipRepository;
        this.userRepository = userRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Ejecuta la lógica para crear una amistad.
     *
     * @param command Comando que contiene los identificadores de los dos usuarios.
     * @return Vista del amigo creado (`FriendView`).
     * @throws FriendToMySelfException     Si ambos identificadores son iguales.
     * @throws RequesterNotFound           Si el usuario solicitante no existe.
     * @throws ReceiverNotFound            Si el usuario receptor no existe.
     * @throws ExistingFriendShipException Si ya existe una relación de amistad entre ambos.
     */
    @Override
    public FriendView execute(AddFriendCommand command) {
        if (command.userAId().equals(command.userBId())) {
            throw new FriendToMySelfException();
        }

        User first = userRepository.findById(command.userAId())
                .orElseThrow(() -> new RequesterNotFound(command.userAId()));

        User second = userRepository.findById(command.userBId())
                .orElseThrow(() -> new ReceiverNotFound(command.userBId()));

        boolean existsFriendShip = friendShipRepository.existsFriendShip(
                command.userAId(),
                command.userBId()
        );

        if (existsFriendShip) {
            throw new ExistingFriendShipException(command.userAId(), command.userBId());
        }

        FriendShip newfriendShip = FriendShip.create(first.getUserId(), second.getUserId());

        friendShipRepository.save(newfriendShip);

        domainEventBus.publishAll(newfriendShip.pullEvents());

        return new FriendView(
                second.getUserId().getValue(),
                second.getName().getFirstName(),
                second.getName().getLastName(),
                second.getPhone().getValue(),
                newfriendShip.getCreatedAt()
        );
    }
}
