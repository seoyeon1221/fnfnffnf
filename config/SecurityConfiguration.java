package board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
// 해당 클래스가 시큐리티에 대한 거의 모든 전반적인걸 관리함
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                // 기본설정을 쓸게요withDefaults()
                // 근데 스프링이 안해줘서 우리가 만들어야함
                // 근데 cors로 구현되어있으니까 우리가 만들어야함
                .cors(withDefaults())
                // 비활성화 할꺼
        // 요청을 보낼대 http 포로토콜안에서 담아서 보낼 수 있는데 그거를 비활성하겠다는말
                // 옛날에는 리퀘스트를 헤더에 담음 인코딩을해, 베이스64로 그거를 안쓴다는가ㅓ)
                .formLogin().disable()
                // 비활성화할꺼
                .httpBasic().disable()

                // 접근권한 주는거고 어던 요청이든지 다 허용하겠다는 뜻
                // 이거는 인증인가를 만들고 나서 변경하는거임
                .authorizeRequests(authorize -> authorize.anyRequest().permitAll());


        return http.build();
    }
}
