package com.example.SocialNetwork.security;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SocialNetwork.config.TenantContext;
import com.example.SocialNetwork.exception.TenantHeaderMissingException;
import com.example.SocialNetwork.repository.TenantRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final String secret;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try{
            setTenantContextFromHeader(request);
        }
        catch (TenantHeaderMissingException exception){
            handlerExceptionResolver.resolveException(request, response, null, exception);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String token = getTokenFromHeader(authorizationHeader);
        if (token != null) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);

                String username = decodedJWT.getSubject();
                String tenantIdFromToken = decodedJWT.getClaim("tenantID").asString();

                if (!TenantContext.getCurrentTenant().equals(tenantIdFromToken)) {
                    throw new JWTVerificationException("Tenant id from token doesn't match with header tenant!");
                }

                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                });
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (JWTVerificationException exception) {
                handlerExceptionResolver.resolveException(request, response, null, exception);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String getTokenFromHeader(String header) {
        return StringUtils.hasText(header) ? header.substring("Bearer ".length()) : null;
    }

    private void setTenantContextFromHeader(HttpServletRequest request) {
        String tenantID = request.getHeader("tenant");
        if (tenantID == null) {
            throw new TenantHeaderMissingException("tenant not present in the Request Header");
        }
        if(tenantRepository.findBySchema(tenantID).isEmpty()){
            throw new TenantHeaderMissingException("invalid header");
        }
        
        TenantContext.setCurrentTenant(tenantID);
    }

}
