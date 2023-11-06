package com.nb.authenticate.service;

import com.nb.authenticate.entity.Usersessionlog;
import com.nb.authenticate.repository.UsersessionlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public class LogoutService implements LogoutHandler {

    @Autowired
   private UsersessionlogRepository usersessionlogRepository;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        Optional<Usersessionlog> sessionlog=usersessionlogRepository.findByaccesstoken(token);
        if(sessionlog.isPresent()){
            sessionlog.get().setLogouttime(""+System.currentTimeMillis());
            sessionlog.get().setIsExpired(true);
            usersessionlogRepository.save(sessionlog.get());
            SecurityContextHolder.clearContext();
        }

    }
}
