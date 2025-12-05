package toast.appback.src.auth.infrastructure.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Utilidad para operaciones de cookie relacionadas con el token de refresh.
 * <p>
 * Funcionalidad:
 * - Setear cookie httpOnly con refresh token.
 * - Eliminar la cookie de refresh.
 * - Extraer valor de refresh token desde la cookie en la petición.
 * - Extraer token desde header Authorization (Bearer <token>).
 */
@Service
public class RefreshCookiesService {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    /**
     * Añade una cookie con el refresh token en la respuesta.
     *
     * @param response     HttpServletResponse donde añadir la cookie.
     * @param refreshToken Valor del refresh token.
     * @param path         Ruta base (se añade "/refresh" para el path de la cookie).
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, String path) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path(path + "/refresh")
                .maxAge(1296000L)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Borra la cookie de refresh en la respuesta.
     *
     * @param response HttpServletResponse donde añadir la cookie con maxAge=0.
     * @param path     Ruta base usada para la cookie.
     */
    public void deleteRefreshCookie(HttpServletResponse response, String path) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path(path + "/refresh")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Obtiene el valor del refresh token (si existe) desde las cookies de la petición.
     *
     * @param request HttpServletRequest que contiene las cookies.
     * @return valor del refresh token o null si no está presente.
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Extrae el token desde el header Authorization. Si el header tiene el prefijo "Bearer ", se elimina el prefijo.
     *
     * @param header Valor del header Authorization.
     * @return token sin prefijo o null si header es null.
     */
    public String extractTokenFromAuthorizationHeader(String header) {
        if (header == null) return null;
        if (header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header;
    }
}
