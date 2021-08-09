package guru.sfg.brewery.config;

import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // needed for use with Spring Data JPA SPeL
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/h2-console/**").permitAll() //do not use in production!
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll();
                })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and().csrf().ignoringAntMatchers("h2-console/**", "/api/**");
        ;

        //h2 use frames and must be config
        //h2 console config
        http
                .headers()
                .frameOptions()
                .sameOrigin();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Autowired
//    JpaUserDetailsService jpaUserDetailsService;

    ////fluent api  - need to specify the password encoder {noop}
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//        auth.userDetailsService(this.jpaUserDetailsService).passwordEncoder(passwordEncoder());

//        auth
//                .inMemoryAuthentication()
//                .withUser("tnc")
//                .password("$2a$10$h3PcTimYKUsyxpWAyEq7D.P1a0DoNr8iTmCjNK14Ai6MUzrYEqaVu")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("$2a$10$h3PcTimYKUsyxpWAyEq7D.P1a0DoNr8iTmCjNK14Ai6MUzrYEqaVu")
//                .roles("USER");
//    }

    //    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("tnc")
//                .password("password")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
