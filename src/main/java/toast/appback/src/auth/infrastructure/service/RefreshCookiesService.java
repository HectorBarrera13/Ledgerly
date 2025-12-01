package toast.appback.src.auth.infrastructure.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class RefreshCookiesService {

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, String path) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // only for https TODO: add https
                .sameSite("None")
                .path(path + "/refresh")
                .maxAge(1296000L)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteRefreshCookie(HttpServletResponse response, String path) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // only for https TODO: add https
                .sameSite("None")
                .path(path + "/refresh")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
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
