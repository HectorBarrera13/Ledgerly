package toast.appback.src.debts.infrastructure.api.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.result.*;
import toast.appback.src.debts.infrastructure.api.dto.response.*;
import toast.appback.src.users.infrastructure.service.UserProfilePictureService;

/**
 * Mapeador que convierte vistas de la capa de aplicación relacionadas con deudas
 * en DTOs de respuesta para la API.
 * <p>
 * Responsabilidades:
 * - Detectar el tipo concreto de {@link DebtView} y delegar a la conversión apropiada.
 * - Enriquecer respuestas con URIs de imagen de perfil usando {@link UserProfilePictureService}.
 * <p>
 * Notas:
 * - Los métodos devuelven DTOs listos para ser serializados por las controladoras REST.
 * - Si se recibe un tipo de deuda no soportado, se lanza {@link IllegalArgumentException}.
 */
@Service
@RequiredArgsConstructor
public class DebtResponseMapper {
    private final UserProfilePictureService userProfilePictureService;

    /**
     * Mapea un {@link DebtView} (polimórfico) a su representación concreta de respuesta.
     *
     * @param debtView Vista de deuda (puede ser {@link QuickDebtView} o {@link DebtBetweenUsersView}).
     * @return Implementación de {@link DebtResponseInt} correspondiente al tipo de deuda.
     * @throws IllegalArgumentException si el tipo de deuda no es soportado.
     */
    public DebtResponseInt toDebtResponse(DebtView debtView) {
        if (debtView instanceof QuickDebtView quickDebt) {
            return toQuickDebtResponse(quickDebt);
        }
        if (debtView instanceof DebtBetweenUsersView betweenUsersDebt) {
            return toDebtBetweenUsersResponse(betweenUsersDebt);
        }

        throw new IllegalArgumentException(
                "Tipo de deuda no soportado: " + debtView.getClass().getSimpleName()
        );
    }

    /**
     * Mapea la información básica común de una deuda (modelo base) a {@link DebtResponse}.
     *
     * @param debtView Vista base de deuda con campos comunes.
     * @return {@link DebtResponse} con los campos básicos (id, propósito, descripción, monto, moneda, estado).
     */
    public DebtResponse toDebtResponseBasic(DebtBaseView debtView) {
        return new DebtResponse(
                debtView.debtId(),
                debtView.purpose(),
                debtView.description(),
                debtView.amount(),
                debtView.currency(),
                debtView.status()
        );
    }

    /**
     * Convierte una {@link QuickDebtView} en su DTO {@link QuickDebtResponse}.
     *
     * @param quickDebt Vista de deuda rápida.
     * @return {@link QuickDebtResponse} que incluye resumen de usuario, rol y nombre objetivo.
     */
    public QuickDebtResponse toQuickDebtResponse(QuickDebtView quickDebt) {
        return new QuickDebtResponse(
                quickDebt.debtId(),
                quickDebt.purpose(),
                quickDebt.description(),
                quickDebt.amount(),
                quickDebt.currency(),
                quickDebt.status(),
                toUserSummaryResponse(quickDebt.userSummary()),
                quickDebt.role(),
                quickDebt.targetUserName()
        );
    }

    /**
     * Convierte una {@link DebtBetweenUsersView} en {@link DebtBetweenUsersResponse}.
     *
     * @param debtBetweenUsers Vista de deuda entre dos usuarios.
     * @return {@link DebtBetweenUsersResponse} con los resúmenes de deudor y acreedor.
     */
    public DebtBetweenUsersResponse toDebtBetweenUsersResponse(DebtBetweenUsersView debtBetweenUsers) {
        return new DebtBetweenUsersResponse(
                debtBetweenUsers.debtId(),
                debtBetweenUsers.purpose(),
                debtBetweenUsers.description(),
                debtBetweenUsers.amount(),
                debtBetweenUsers.currency(),
                debtBetweenUsers.status(),
                toUserSummaryResponse(debtBetweenUsers.debtorSummary()),
                toUserSummaryResponse(debtBetweenUsers.creditorSummary())
        );
    }

    /**
     * Mapea un {@link UserSummaryView} a {@link UserSummaryResponse} e
     * incluye la URI de la imagen de perfil si está disponible.
     *
     * @param userSummaryView Resumen de usuario del módulo de deudas.
     * @return {@link UserSummaryResponse} con id, nombre, apellidos y URL de la foto (puede ser null).
     */
    public UserSummaryResponse toUserSummaryResponse(UserSummaryView userSummaryView) {
        return new UserSummaryResponse(
                userSummaryView.userId(),
                userSummaryView.userFirstName(),
                userSummaryView.userLastName(),
                userProfilePictureService.getProfileUri(
                        userSummaryView.userId()
                )
        );
    }

    /**
     * Alias/conversor específico para mapear deudas de grupo (entre usuarios) a DTO.
     *
     * @param debt Deuda entre usuarios asociada a un grupo.
     * @return {@link DebtBetweenUsersResponse} equivalente.
     */
    public DebtBetweenUsersResponse toGroupDebtResponse(DebtBetweenUsersView debt) {
        return new DebtBetweenUsersResponse(
                debt.debtId(),
                debt.purpose(),
                debt.description(),
                debt.amount(),
                debt.currency(),
                debt.status()
                , toUserSummaryResponse(debt.debtorSummary())
                , toUserSummaryResponse(debt.creditorSummary())
        );
    }
}
