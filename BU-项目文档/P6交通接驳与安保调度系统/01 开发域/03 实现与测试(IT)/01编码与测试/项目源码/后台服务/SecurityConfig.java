package com.example.oldtown.config;

import com.example.oldtown.component.JwtAuthenticationTokenFilter;
import com.example.oldtown.component.RestAuthenticationEntryPoint;
import com.example.oldtown.component.RestfulAccessDeniedHandler;
import com.example.oldtown.dto.SysAdminDetails;
import com.example.oldtown.modules.sys.model.SysAdmin;
import com.example.oldtown.modules.sys.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author ding.yp
 * @name
 * @info
 * @date 2020/8/24
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    SysAdminService sysAdminService;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    // ????????????
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_SUPER > ROLE_ADMIN \n ROLE_ADMIN > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
    // ?????????????????????
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger/**","/swagger-ui.html","/webjars/**","/v2/**",
                "/webjars/springfox-swagger-ui/**","/v2/api-docs/**","/**/swagger-ui.html","/**/webjars/**",
                "/swagger-resources/**","/v2/api-docs-ext/**","/doc.html","/favicon.ico",
                "/**/*.html","/**/*.css", "/**/*.js","/*.html","/","/META-INF/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf() //??????????????????JWT????????????????????????csrf
                .disable()
                .sessionManagement()//token ?????????session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/com/**/get*","/xcx/xcxNotice/get*","/xcx/xcxPropaganda/get*","/xcx/xcxNews/get*",
                        "/trf/trfStaffGps/add","/trf/trfApp/getTheLatest","/trf/trfApp/getAppConfig","/zch/zchShape/*")
                .permitAll()
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll()//??????????????????????????????options??????
                .antMatchers("/sys/test/**","/sys/sysAdmin/login","/xcx/xcxUser/login","/file/**")
                .permitAll()
                .anyRequest()
                .authenticated();
        // ????????????
        httpSecurity.headers().cacheControl().and().frameOptions().disable();
        // ??????JWT filter
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //????????????????????????????????????????????????
        httpSecurity.exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    @Override
    protected void configure (AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        //??????????????????????????????
        return username -> sysAdminService.loadUserByUsername(username);

    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
