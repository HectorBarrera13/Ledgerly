package toast.appback.src.debts.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.DebtProjection;
import toast.model.entities.debt.DebtBetweenUsersEntity;
import toast.model.entities.debt.DebtEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDebtBetweenUsersRepository extends JpaRepository<DebtBetweenUsersEntity, Long> {

    Optional<DebtBetweenUsersEntity> findByDebtId(UUID debtId);

    @Query("""
           SELECT dbu
           From DebtBetweenUsersEntity dbu
           Where dbu.debtorId = :userId
           Order By dbu.createdAt Desc
           """)
    List<DebtProjection> findDebtorDebtBetweenUsersProjection(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
              SELECT dbu
                From DebtBetweenUsersEntity dbu
                Where dbu.debtorId = :userId
                    And dbu.debtId < :cursor
                Order By dbu.createdAt Desc
            """)
    List<DebtProjection> findDebtorDebtsBetweenUsersProjectionAfterCursor(@Param("userId") UUID userId, @Param("cursor") UUID cursor, Pageable pageable);

    @Query("""
              SELECT dbu
                From DebtBetweenUsersEntity dbu
                Where dbu.creditorId = :userId
                Order By dbu.createdAt Desc
            """)
    List<DebtProjection> findCreditorDebtBetweenUsersProjection(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
              SELECT dbu
                From DebtBetweenUsersEntity dbu
                Where dbu.creditorId = :userId
                    And dbu.debtId < :cursor
                Order By dbu.createdAt Desc
            """)
    List<DebtProjection> findCreditorDebtsBetweenUsersProjectionAfterCursor(@Param("userId") UUID userId, @Param("cursor") UUID cursor, Pageable pageable);
}
