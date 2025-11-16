package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.port.FriendReadRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaFriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendReadRepositoryMySQL implements FriendReadRepository {
    private final JpaFriendShipRepository jpaFriendShipRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public List<FriendView> findFriendsByUserId(UserId userId, int limit) {
        Long userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        return jpaFriendShipRepository.findAllUserFriendsByUserId(userDbId, limit).stream()
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
        Long userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
        Long cursorDbId = jpaUserRepository.findByUserId(cursor)
                .orElseThrow(() -> new IllegalArgumentException("Cursor user not found"))
                .getId();
        return jpaFriendShipRepository.findAllUserFriendsByUserIdAfterCursor(userDbId, cursorDbId, limit).stream()
                .map(projection -> new FriendView(
                        projection.getUserId(),
                        projection.getFirstName(),
                        projection.getLastName(),
                        projection.getPhone(),
                        projection.getAddedAt()
                )).toList();
    }
}
