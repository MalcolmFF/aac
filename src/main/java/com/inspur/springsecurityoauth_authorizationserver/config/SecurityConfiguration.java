package com.inspur.springsecurityoauth_authorizationserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        auth.authenticationProvider(authenticationProvider);

        // 测试自定义filter
        auth.userDetailsService(userDetailsService);
//        auth.authenticationProvider(new UserJoinTimeAuthenticationProvider(userDetailsService));
    }

    /**
     * 排除对静态资源的过滤
     * @param web
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**", "/js/**", "/fonts/**", "/icon/**", "/favicon.ico");
    }

    /**
     * http资源的安全规则配置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 1.配置拦截规则
        http.authorizeRequests()
//                 // ①.配置需要角色权限的地址
//                .antMatchers("/test/1/**").hasRole("admin")
//                .antMatchers("/test/2/**").hasAnyRole("admin","user")
//                .antMatchers("/test/3**").access("hasAnyRole('admin','user')")
//                // ②.token的scope配置（示例）
//                .antMatchers("/user/info").access("#oauth2.hasScope('get_user_info')")
                // ③.配置不需要认证的地址
                .antMatchers("/login","loginout", "/login-error", "/oauth/authorize","/auth/unauth","/auth/**","/check_client").permitAll()
                // ④.配置需要认证的地址
                .anyRequest().authenticated()

                // 2.配置登录地址
                .and()
                .formLogin()
                    .loginPage("/login")
//                    .successHandler(new SessionClearHandler())
                    .failureUrl("/login-error")
                    .permitAll()

                // 退出登录接口与handler的实现
                .and()
                .logout()
                    .logoutUrl("/loginout") //默认logout
                    .logoutSuccessHandler(new LogoutSuccessHandler())
                    .deleteCookies("JSESSIONID")//清楚cook键值

                // 3.请求头配置
                .and()
                .headers().frameOptions().disable()

                // 4.CSRF请求配置
                .and()
                .csrf().disable();

        // 自定义filter测试
//        http.addFilterBefore(new CustomerAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

}
