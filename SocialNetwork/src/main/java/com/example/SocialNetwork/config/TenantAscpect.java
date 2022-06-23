package com.example.SocialNetwork.config;

import com.example.SocialNetwork.service.PostService;
import com.example.SocialNetwork.service.UserService;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.hibernate.Session;

@Aspect
@Component
public class TenantAscpect {
    
    @Before("execution(* com.example.SocialNetwork.service.UserService.*(..))&& target(userService) ")
    public void aroundExecutionUserService(JoinPoint pjp, UserService userService) throws Throwable {
        org.hibernate.Filter filter = userService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("tenantId", TenantContext.getCurrentTenant() == null ? "tenant1" : TenantContext.getCurrentTenant());
        filter.validate();
    }

    @Before("execution(* com.example.SocialNetwork.service.PostService.*(..))&& target(postService) ")
    public void aroundExecutionPostService(JoinPoint pjp, PostService postService) throws Throwable {
        org.hibernate.Filter filter = postService.entityManager.unwrap(Session.class).enableFilter("tenantFilter");
        filter.setParameter("tenantId", TenantContext.getCurrentTenant() == null ? "tenant1" : TenantContext.getCurrentTenant());
        filter.validate();
    }
}
