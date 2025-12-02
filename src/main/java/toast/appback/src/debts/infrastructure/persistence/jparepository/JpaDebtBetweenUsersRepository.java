package toast.appback.src.debts.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.DebtBetweenUsersProjection;
import toast.model.entities.debt.DebtBetweenUsersEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDebtBetweenUsersRepository extends JpaRepository<DebtBetweenUsersEntity, Long> {

    Optional<DebtBetweenUsersEntity> findByDebtId(UUID debtId);


    @Query("""
           SELECT 
                d.debtId AS debtId,
                d.purpose AS purpose, 
                d.description AS description,
                d.amount AS amount, 
                d.currency AS currency,
                d.debtorId AS debtorId,
                debtor.firstName AS debtorFirstName,
                debtor.lastName AS debtorLastName,
                d.creditorId AS creditorId,
                creditor.firstName AS creditorFirstName,
                creditor.lastName AS creditorLastName,
                d.status AS status
            FROM DebtBetweenUsersEntity d
            JOIN UserEntity debtor ON d.debtorId = debtor.userId
            JOIN UserEntity creditor ON d.creditorId = creditor.userId
            WHERE d.debtId = :debtId
           """)
    Optional<DebtBetweenUsersProjection> findDebtBetweenUsersProjectionByDebtId(@Param("debtId") UUID debtId);

    @Query("""
       SELECT 
            d.debtId AS debtId,
            d.purpose AS purpose, 
            d.description AS description,
            d.amount AS amount, 
            d.currency AS currency,
            d.debtorId AS debtorId,
            debtor.firstName AS debtorFirstName,
            debtor.lastName AS debtorLastName,
            d.creditorId AS creditorId,
            creditor.firstName AS creditorFirstName,
            creditor.lastName AS creditorLastName,
            d.status AS status
        FROM DebtBetweenUsersEntity d
        JOIN UserEntity debtor ON d.debtorId = debtor.userId
        JOIN UserEntity creditor ON d.creditorId = creditor.userId
        WHERE
            d.status = :status AND 
            (
                (:role = 'DEBTOR' AND d.debtorId = :userId) OR
                (:role = 'CREDITOR' AND d.creditorId = :userId)
            )
        ORDER BY d.createdAt DESC
        LIMIT :limit
       """)
    List<DebtBetweenUsersProjection> getDebtsBetweenUsersProjectionByRole(
            @Param("userId") UUID userId,
            @Param("role") String role,
            @Param("status") String status,
            @Param("limit") int limit
    );

    @Query("""
              SELECT 
                d.debtId AS debtId,
                d.purpose AS purpose, 
                d.description AS description,
                d.amount AS amount, 
                d.currency AS currency,
                d.debtorId AS debtorId,
                debtor.firstName AS debtorFirstName,
                debtor.lastName AS debtorLastName,
                d.creditorId AS creditorId,
                creditor.firstName AS creditorFirstName,
                creditor.lastName AS creditorLastName,
                d.status AS status
            FROM DebtBetweenUsersEntity d
            JOIN UserEntity debtor ON d.debtorId = debtor.userId
            JOIN UserEntity creditor ON d.creditorId = creditor.userId
            WHERE
            d.status = :status AND 
            (
                (:role = 'DEBTOR' AND d.debtorId = :userId) OR
                (:role = 'CREDITOR' AND d.creditorId = :userId)
            )
            AND d.debtId < :cursor
            ORDER BY d.createdAt DESC
            LIMIT :limit
            """)
    List<DebtBetweenUsersProjection> findDebtorDebtsBetweenUsersProjectionAfterCursor(
            @Param("userId") UUID userId,
            @Param("role") String role,
            @Param("status") String status,
            @Param("cursor") UUID cursor,
            @Param("limit") int limit);

}
