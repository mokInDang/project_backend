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


    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> memberServicePatterns = List.of("/", "/api/member/join/**");
        List<String> kakaoLoginPatterns = List.of("/index.html", "/kakao_login_medium_narrow.png");
        List<String> swaggerPatterns = List.of("/v3/api-docs", "/swagger-ui.html",
                "/swagger-ui/index.html", "/swagger-ui/swagger-initializer.js",
                "/v3/api-docs/swagger-config");

        List<String> allPatterns = new ArrayList<>(memberServicePatterns);
        allPatterns.addAll(kakaoLoginPatterns);
        allPatterns.addAll(swaggerPatterns);

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(allPatterns);
    }
}
