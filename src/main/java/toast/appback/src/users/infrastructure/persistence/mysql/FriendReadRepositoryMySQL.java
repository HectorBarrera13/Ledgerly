package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.shared.infrastructure.PageMapper;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.port.FriendReadRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaFriendShipRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendReadRepositoryMySQL implements FriendReadRepository {
    private static final String USER_NOT_FOUND_ERROR = "User not found";

    private final JpaFriendShipRepository jpaFriendShipRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public PageResult<FriendView, UUID> findFriendsByUserId(UserId userId, PageRequest pageRequest) {
        Long userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .getId();

        Page<FriendView> pageable = jpaFriendShipRepository
                .findAllUserFriendsByUserId(userDbId, PageMapper.toPageable(pageRequest))
                .map(
                        projection -> new FriendView(
                                projection.getUserId(),
                                projection.getFirstName(),
                                projection.getLastName(),
                                projection.getPhone(),
                                projection.getAddedAt()
                        )
                );
        return PageMapper.toPageResult(pageable);
    }

    @Override
    public PageResult<FriendView, UUID> findFriendsByUserIdAfterCursor(UserId userId, CursorRequest<UUID> cursorRequest) {
        Long userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .getId();
        Long cursorDbId = jpaUserRepository.findByUserId(cursorRequest.cursor())
                .orElseThrow(() -> new IllegalArgumentException("Cursor user not found"))
                .getId();
        Page<FriendView> pageable = jpaFriendShipRepository
                .findAllUserFriendsByUserIdAfterCursor(userDbId, cursorDbId, PageMapper.toPageable(cursorRequest))
                .map(
                        projection -> new FriendView(
                                projection.getUserId(),
                                projection.getFirstName(),
                                projection.getLastName(),
                                projection.getPhone(),
                                projection.getAddedAt()
                        )
                );
        return PageMapper.toPageResult(pageable);
    }

    @Override
    public PageResult<FriendView, UUID> searchFriendsByName(UserId userId, String nameQuery, PageRequest pageRequest) {
        Long userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .getId();
        Page<FriendView> pageable = jpaFriendShipRepository
                .searchFriendsByName(
                        userDbId,
                        nameQuery,
                        PageMapper.toPageable(pageRequest)
                ).map(
                        projection -> new FriendView(
                                projection.getUserId(),
                                projection.getFirstName(),
                                projection.getLastName(),
                                projection.getPhone(),
                                projection.getAddedAt()
                        )
                );
        return PageMapper.toPageResult(pageable);
    }

    @Override
    public PageResult<FriendView, UUID> searchFriendsByPhone(UserId userId, String phoneQuery, PageRequest pageRequest) {
        Long userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .getId();
        Page<FriendView> pageable = jpaFriendShipRepository
                .searchFriendsByPhone(
                        userDbId,
                        phoneQuery,
                        PageMapper.toPageable(pageRequest)
                ).map(
                        projection -> new FriendView(
                                projection.getUserId(),
                                projection.getFirstName(),
                                projection.getLastName(),
                                projection.getPhone(),
                                projection.getAddedAt()
                        )
                );
        return PageMapper.toPageResult(pageable);
    }
}
