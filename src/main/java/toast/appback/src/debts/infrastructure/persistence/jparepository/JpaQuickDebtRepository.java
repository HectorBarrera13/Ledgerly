package toast.appback.src.debts.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.QuickDebtProjection;
import toast.model.entities.debt.QuickDebtEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaQuickDebtRepository extends JpaRepository<QuickDebtEntity, Long> {

    Optional<QuickDebtEntity> findByDebtId(UUID debtId);

    @Query("""
            SELECT 
                 q.debtId AS debtId,
                 q.purpose AS purpose, 
                 q.description AS description,
                 q.amount AS amount, 
                 q.currency AS currency,
                 q.userId AS userId,
                 user.firstName AS userFirstName,
                 user.lastName AS userLastName,
                 q.myRole AS myRole,
                 q.targetUserName AS targetUserName,
                 q.status AS status
            FROM QuickDebtEntity q
            JOIN UserEntity user ON q.userId = user.userId
            WHERE q.debtId = :debtId
            """)
    Optional<QuickDebtProjection> findQuickDebtProjectionByDebtId(@Param("debtId") UUID debtId);

    @Query("""
            SELECT 
                 q.debtId AS debtId,
                 q.purpose AS purpose, 
                 q.description AS description,
                 q.amount AS amount, 
                 q.currency AS currency,
                 q.userId AS userId,
                 user.firstName AS userFirstName,
                 user.lastName AS userLastName,
                 q.myRole AS role,
                 q.targetUserName AS targetUserName,
                 q.status AS status
            FROM QuickDebtEntity q
            JOIN UserEntity user ON q.userId = user.userId
            WHERE 
            (CASE WHEN :role = 'DEBTOR' THEN q.myRole = 'DEBTOR' AND q.userId = :userId END) OR
            (CASE WHEN :role = 'CREDITOR' THEN q.myRole = 'CREDITOR' AND q.userId = :userId END)
            ORDER BY q.createdAt DESC
            LIMIT :limit
            """)
    List<QuickDebtProjection> getQuickDebtsProjectionByRole(@Param("userId") UUID userId, @Param("role") String role, @Param("limit") int limit);

    @Query("""
            SELECT 
                 q.debtId AS debtId,
                 q.purpose AS purpose, 
                 q.description AS description,
                 q.amount AS amount, 
                 q.currency AS currency,
                 q.userId AS userId,
                 user.firstName AS userFirstName,
                 user.lastName AS userLastName,
                 q.myRole AS role,
                 q.targetUserName AS targetUserName,
                 q.status AS status
            FROM QuickDebtEntity q
            JOIN UserEntity user ON q.userId = user.userId
            WHERE 
            (CASE WHEN :role = 'DEBTOR' THEN q.myRole = 'DEBTOR' AND q.userId = :userId END) OR
            (CASE WHEN :role = 'CREDITOR' THEN q.myRole = 'CREDITOR' AND q.userId = :userId END)
            AND q.debtId < :cursor
            ORDER BY q.createdAt DESC
            LIMIT :limit
            """)
    List<QuickDebtProjection> getQuickDebtsProjectionByRoleAfterCursor(
            @Param("userId") UUID userId,
            @Param("role") String role,
            @Param("cursor") UUID cursor,
            @Param("limit") int limit);
}
