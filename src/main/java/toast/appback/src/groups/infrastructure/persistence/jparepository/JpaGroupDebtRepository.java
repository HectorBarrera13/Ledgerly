package toast.appback.src.groups.infrastructure.persistence.jparepository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.DebtBetweenUsersProjection;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.DebtProjection;
import org.springframework.data.domain.Pageable;
import toast.model.entities.group.GroupDebtEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface JpaGroupDebtRepository extends JpaRepository<GroupDebtEntity, UUID> {

    @Override
    Optional<GroupDebtEntity> findById(UUID id);

    @Query("""
    SELECT 
        d.debtId AS debtId,
        d.purpose AS purpose,
        d.description AS description,
        d.amount AS amount,
        d.currency AS currency,
        d.status AS status,

        d.debtorId AS debtorId,
        ud.firstName AS debtorFirstName,
        ud.lastName AS debtorLastName,

        d.creditorId AS creditorId,
        uc.firstName AS creditorFirstName,
        uc.lastName AS creditorLastName

    FROM GroupDebtEntity gd
    JOIN gd.debt d
    JOIN UserEntity ud ON ud.userId = d.debtorId
    JOIN UserEntity uc ON uc.userId = d.creditorId
    
    WHERE gd.group.groupId = :groupId
      AND (:status IS NULL OR d.status = :status)
      AND (
            (:role = 'DEBTOR' AND d.debtorId = :userId)
         OR (:role = 'CREDITOR' AND d.creditorId = :userId)
         OR (:role IS NULL AND :userId IS NULL) 
      )

    ORDER BY d.createdAt DESC
    """
    )
    Page<DebtBetweenUsersProjection> findAllDebtsByGroupIdAndUserId(
            @Param("groupId") UUID groupId,
            @Param("userId") UUID userId,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );


    @Query("""
    SELECT 
        d.debtId AS debtId,
        d.purpose AS purpose,
        d.description AS description,
        d.amount AS amount,
        d.currency AS currency,
        d.status AS status,

        d.debtorId AS debtorId,
        ud.firstName AS debtorFirstName,
        ud.lastName AS debtorLastName,

        d.creditorId AS creditorId,
        uc.firstName AS creditorFirstName,
        uc.lastName AS creditorLastName

    FROM GroupDebtEntity gd
    JOIN gd.debt d
    JOIN UserEntity ud ON ud.userId = d.debtorId
    JOIN UserEntity uc ON uc.userId = d.creditorId

    WHERE gd.group.groupId = :groupId
      AND d.status = :status
      AND (
            (:role = 'DEBTOR' AND d.debtorId = :userId)
         OR (:role = 'CREDITOR' AND d.creditorId = :userId)
      )
      AND d.createdAt < :cursor

    ORDER BY d.createdAt DESC
""")
    Page<DebtBetweenUsersProjection> findAllDebtsByGroupIdAndUserIdAfterCursor(
            @Param("groupId") UUID groupId,
            @Param("userId") UUID userId,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );


}
