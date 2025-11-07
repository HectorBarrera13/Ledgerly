package toast.appback.src.users.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.users.domain.Friend;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.appback.src.users.infrastructure.persistence.mapping.UserMapper;
import toast.model.entities.users.FriendEntity;
import toast.model.entities.users.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryMySQL implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        UserEntity userEntity = UserMapper.toEntity(user);
        List<Friend> friends = user.getFriends();
        if (friends != null && !friends.isEmpty()) {
            List<FriendEntity> friendEntities = friends.stream()
                    .map(friend -> {
                        UserEntity friendEntity = jpaUserRepository.findByUserId(friend.friendId().uuid())
                                .orElseThrow(() -> new IllegalArgumentException("Friend with ID " + friend.friendId().uuid() + " not found"));
                        FriendEntity friendLink = new FriendEntity();
                        friendLink.setUser(userEntity);
                        friendLink.setFriend(friendEntity);
                        friendLink.setAddedAt(friend.addedAt());
                        return friendLink;
                    }).toList();
            userEntity.setFriends(friendEntities);
        }
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return UserMapper.toDomain(savedEntity, user.pullDomainEvents());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findByUserId(id)
                .map(u -> UserMapper.toDomain(u, List.of()));
    }

    @Override
    public void deleteById(UUID id) {
        jpaUserRepository.findByUserId(id).ifPresent(jpaUserRepository::delete);
    }
}
