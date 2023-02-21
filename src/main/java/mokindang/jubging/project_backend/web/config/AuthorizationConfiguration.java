package mokindang.jubging.project_backend.web.config;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.web.argumentresolver.LoginMemberArgumentResolver;
import mokindang.jubging.project_backend.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        registry.addInterceptor(loginCheckInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/index.html", "/api/member/join/**");
    }
}
