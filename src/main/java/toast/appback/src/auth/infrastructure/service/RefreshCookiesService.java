package toast.appback.src.auth.infrastructure.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class RefreshCookiesService {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

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

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public String extractTokenFromAuthorizationHeader(String header) {
        if (header == null) return null;
        if (header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return header;
    }
}
