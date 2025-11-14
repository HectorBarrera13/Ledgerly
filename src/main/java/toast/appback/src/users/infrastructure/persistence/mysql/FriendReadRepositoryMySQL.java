package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.port.FriendReadRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaFriendShipRepository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendReadRepositoryMySQL implements FriendReadRepository {
    private final JpaFriendShipRepository jpaFriendShipRepository;

    @Override
    public List<FriendView> findFriendsByUserId(UserId userId, int limit) {
        return jpaFriendShipRepository.findAllUserFriendsByUserId(userId.getValue(), limit).stream()
                .map(projection -> new FriendView(
                        projection.getUserId(),
                        projection.getFirstName(),
                        projection.getLastName(),
                        projection.getPhone(),
                        projection.getAddedAt()
                )).toList();
    }

    @Override
    public List<FriendView> findFriendsByUserIdAfterCursor(UserId userId, UUID cursor, int limit) {
        return jpaFriendShipRepository.findAllUserFriendsByUserIdAfterCursor(userId.getValue(), cursor,limit).stream()
                .map(projection -> new FriendView(
                        projection.getUserId(),
                        projection.getFirstName(),
                        projection.getLastName(),
                        projection.getPhone(),
                        projection.getAddedAt()
                )).toList();
    }
}
