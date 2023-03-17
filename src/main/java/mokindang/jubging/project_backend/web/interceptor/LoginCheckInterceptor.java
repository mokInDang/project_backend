package mokindang.jubging.project_backend.web.interceptor;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            tokenManager.validateToken(authorizationHeader);
        } catch (final RuntimeException e) {
            response.sendRedirect("https://www.dongnejupging.xyz");
            return false;
        }
        return true;
    }
}
