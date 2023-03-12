package mokindang.jubging.project_backend.web.interceptor;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final TokenManager tokenManager;

    @Value("${redirect-url}")
    private String redirectURI;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            tokenManager.validateToken(authorizationHeader);
        } catch (final RuntimeException e) {
            response.sendRedirect(redirectURI);
            return false;
        }
        return true;
    }
}
