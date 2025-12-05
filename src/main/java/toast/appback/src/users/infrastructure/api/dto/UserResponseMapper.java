package toast.appback.src.users.infrastructure.api.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.infrastructure.api.dto.AuthMapper;
import toast.appback.src.groups.application.communication.result.MemberView;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.infrastructure.api.dto.response.FriendResponse;
import toast.appback.src.users.infrastructure.api.dto.response.ProfileResponse;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;
import toast.appback.src.users.infrastructure.service.UserProfilePictureService;

/**
 * Mapeador que convierte vistas de aplicación en objetos de respuesta para la API REST.
 *
 * <p>Este servicio centraliza la transformación entre los objetos de la capa de aplicación
 * (por ejemplo {@link UserView}, {@link FriendView}, {@link AccountView}) y los DTOs
 * que se devuelven a los clientes HTTP.
 * <p>
 * Notas:
 * - La obtención de la URI de la foto de perfil se delega en {@link UserProfilePictureService} y
 * puede devolver null si el usuario no tiene foto.
 */
@Service
@RequiredArgsConstructor
public class UserResponseMapper {
    private final UserProfilePictureService userProfilePictureService;

    /**
     * Convierte una vista de usuario de la capa de aplicación a un DTO público.
     *
     * @param userView Vista de usuario proveniente del dominio/aplicación.
     * @return {@link UserResponse} con los campos públicos del usuario.
     */
    public UserResponse toUserResponse(UserView userView) {
        return new UserResponse(
                userView.userId(),
                userView.firstName(),
                userView.lastName(),
                userView.phone()
        );
    }

    /**
     * Convierte una vista de amistad a un DTO de respuesta de amigo.
     *
     * @param friendView Vista de la relación de amistad.
     * @return {@link FriendResponse} que incluye los datos del usuario, la fecha de añadido
     * y la URI de la imagen de perfil (puede ser null si no existe).
     */
    public FriendResponse toFriendResponse(FriendView friendView) {
        return new FriendResponse(
                friendView.userId(),
                friendView.firstName(),
                friendView.lastName(),
                friendView.phone(),
                friendView.addedAt(),
                userProfilePictureService.getProfileUri(friendView.userId())
        );
    }

    /**
     * Construye la respuesta de perfil combinando la información de cuenta y del usuario.
     *
     * @param accountView Información de la cuenta (autenticación/identidad).
     * @param userView    Información pública del usuario.
     * @return {@link ProfileResponse} que agrupa la cuenta y el usuario.
     */
    public ProfileResponse toProfileResponse(AccountView accountView, UserView userView) {
        return new ProfileResponse(
                AuthMapper.toAccountResponse(accountView),
                toUserResponse(userView)
        );
    }

    /**
     * Convierte una vista de miembro de grupo a un DTO público de usuario.
     *
     * @param memberView Vista del miembro proveniente del módulo groups.
     * @return {@link UserResponse} con datos básicos del miembro.
     */
    public UserResponse toUserResponse(MemberView memberView) {
        return new UserResponse(
                memberView.userId(),
                memberView.firstName(),
                memberView.lastName(),
                memberView.phone()
        );
    }
}
