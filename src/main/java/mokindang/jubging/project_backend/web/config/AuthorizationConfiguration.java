package mokindang.jubging.project_backend.web.config;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.web.argumentresolver.LoginMemberArgumentResolver;
import mokindang.jubging.project_backend.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfiguration implements WebMvcConfigurer {
    private static final List<String> MEMBER_SERVICES_PATTERNS = List.of("/", "/api/member/join/**");
    private static final List<String> KAKAO_LOGIN_PATTERNS = List.of("/index.html", "/kakao_login_medium_narrow.png");
    private static final List<String> SWAGGER_PATTERNS = List.of("/v3/api-docs", "/swagger-ui.html", "/swagger-ui/index.html",
            "/swagger-ui/swagger-initializer.js", "/v3/api-docs/swagger-config");

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> allPatterns = new ArrayList<>(MEMBER_SERVICES_PATTERNS);
        allPatterns.addAll(KAKAO_LOGIN_PATTERNS);
        allPatterns.addAll(SWAGGER_PATTERNS);

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(allPatterns);
    }
}
