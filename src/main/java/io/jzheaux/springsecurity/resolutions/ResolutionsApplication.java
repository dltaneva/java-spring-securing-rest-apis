package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication //(exclude = SecurityAutoConfiguration.class)
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests(authz -> authz
						.anyRequest().authenticated())
				.httpBasic(basic -> {})
				.cors(cors -> {})
		;
	}

	@Bean
	UserDetailsService userDetailsService(UserRepository users) {
		return new UserRepositoryUserDetailsService(users);
	}

	@Bean
	WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						// .maxAge(0) // if using local verification
						.allowedOrigins("http://localhost:4000")
						.allowedMethods("HEAD")
						.allowedHeaders("Authorization");
			}
		};
	}

// before method-based authorization
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//				.authorizeRequests(authz -> authz
//						.mvcMatchers(GET, "/resolutions", "/resolution/**").hasAuthority("resolution:read")
//						.anyRequest().hasAuthority("resolution:write"))
//				.httpBasic(basic -> {});
//	}


//	@Bean
//	public UserDetailsService userDetailsService() {
//		return new InMemoryUserDetailsManager(
//				org.springframework.security.core.userdetails.User
//						.withUsername("user")
//						.password("{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W")
//						.authorities("resolution:read")
//						.build());
//	}

// hardcoded authorities
//	@Bean
//	public UserDetailsService userDetailsService(DataSource dataSource) {
//		return new JdbcUserDetailsManager(dataSource) {
//			@Override
//			protected List<GrantedAuthority> loadUserAuthorities(String username) {
//				return AuthorityUtils.createAuthorityList("resolution:read");
//			}
//		};
//
//	}

//	@Bean
//	public UserDetailsService userDetailsService(DataSource dataSource) {
//		return new JdbcUserDetailsManager(dataSource);
//	}
}
