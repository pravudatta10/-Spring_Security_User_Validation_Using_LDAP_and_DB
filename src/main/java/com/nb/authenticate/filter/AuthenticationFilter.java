package com.nb.authenticate.filter;

import com.nb.authenticate.entity.Usersessionlog;
import com.nb.authenticate.repository.UsersessionlogRepository;
import com.nb.authenticate.service.AuthenticationService;
import com.nb.authenticate.service.AuthenticationUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationUtilService authenticationUtilService;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    UsersessionlogRepository usersessionlogRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String userid = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            userid = authenticationUtilService.extractUsername(token);

        }
        boolean checklogoutSession=false;
        Optional<Usersessionlog> stordedToken = usersessionlogRepository.findByaccesstoken(token);
        if(stordedToken.isPresent()){
             checklogoutSession = stordedToken.get().getIsExpired();
        }
        if (userid != null && SecurityContextHolder.getContext().getAuthentication() == null && checklogoutSession==false) {

            UserDetails userDetails = authenticationService.loadUserByUsername(userid);

            if (authenticationUtilService.validateToken(token, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
