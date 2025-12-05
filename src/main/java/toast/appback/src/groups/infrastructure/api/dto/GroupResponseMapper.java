package toast.appback.src.groups.infrastructure.api.dto;

import toast.appback.src.debts.infrastructure.api.dto.response.UserSummaryResponse;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;

import java.util.List;

/**
 * Mapeador estático que convierte entidades y vistas del dominio en DTOs de respuesta
 * para la API del módulo `groups`.
 * <p>
 * Responsabilidad:
 * - Crear instancias de {@link GroupResponse} y {@link GroupDetailResponse} a partir de
 * una entidad {@link Group} o de la vista de aplicación {@link GroupView}.
 * <p>
 * Notas:
 * - Los métodos son estáticos y puros: no modifican las entidades de entrada.
 * - Los campos de fecha/UUID se transfieren tal cual desde la entidad o vista.
 */
public class GroupResponseMapper {

    private GroupResponseMapper() {
    }

    /**
     * Convierte una entidad {@link Group} y la lista de usuarios relacionados en
     * un {@link GroupDetailResponse} listo para exponer por la API.
     *
     * @param group Entidad de dominio que contiene información completa del grupo.
     * @param users Lista de {@link UserSummaryResponse} con los miembros o resúmenes de usuario.
     * @return {@link GroupDetailResponse} que agrupa la representación del grupo y los usuarios.
     */
    public static GroupDetailResponse toGroupDetailResponse(Group group, List<UserSummaryResponse> users) {
        GroupResponse groupResponse = new GroupResponse(
                group.getId().getValue(),
                group.getCreatorId().getValue(),
                group.getGroupInformation().getName(),
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        );

        return new GroupDetailResponse(
                groupResponse,
                users
        );
    }

    /**
     * Convierte una entidad {@link Group} en su representación pública mínima {@link GroupResponse}.
     *
     * @param group Entidad de dominio.
     * @return {@link GroupResponse} con los campos públicos del grupo.
     */
    public static GroupResponse toGroupResponse(Group group) {
        return new GroupResponse(
                group.getId().getValue(),
                group.getCreatorId().getValue(),
                group.getGroupInformation().getName(),
                group.getGroupInformation().getDescription(),
                group.getCreatedAt()
        );
    }

    /**
     * Convierte una vista de aplicación {@link GroupView} y la lista de usuarios relacionados en
     * un {@link GroupDetailResponse} listo para la API.
     *
     * @param groupView Vista de aplicación que representa el grupo (ya mapeada desde la capa de persistencia).
     * @param users     Lista de {@link UserSummaryResponse} asociados al grupo.
     * @return {@link GroupDetailResponse} con la información consolidada.
     */
    public static GroupDetailResponse toGroupDetailResponse(GroupView groupView, List<UserSummaryResponse> users) {
        GroupResponse groupResponse = new GroupResponse(
                groupView.groupId(),
                groupView.creatorId(),
                groupView.name(),
                groupView.description(),
                groupView.createdAt()
        );

        return new GroupDetailResponse(
                groupResponse,
                users
        );
    }

    /**
     * Convierte una vista {@link GroupView} en su representación pública {@link GroupResponse}.
     *
     * @param groupView Vista de aplicación del grupo.
     * @return {@link GroupResponse} con los campos públicos del grupo.
     */
    public static GroupResponse toGroupResponse(GroupView groupView) {
        return new GroupResponse(
                groupView.groupId(),
                groupView.creatorId(),
                groupView.name(),
                groupView.description(),
                groupView.createdAt()
        );
    }
}
