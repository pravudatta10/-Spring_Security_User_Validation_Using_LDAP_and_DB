package com.nb.authenticate.service;

import com.nb.authenticate.entity.Usersessionlog;
import com.nb.authenticate.repository.UserinfoRepository;
import com.nb.authenticate.repository.UsersessionlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private UserinfoRepository userinfoRepository;
    @Autowired
    private UsersessionlogRepository usersessionlogRepository;

    public Usersessionlog createRefrehsToken(String userid, String accesstoken) {
        HttpServletRequest ipaddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Usersessionlog usersessionlog = Usersessionlog.builder()
                .userinfo(userinfoRepository.findByUserid(userid))
                .accesstoken(accesstoken)
                .refreshtoken(UUID.randomUUID().toString())
                .ipaddress(ipaddress.getRemoteAddr())
                .expiryDate(Instant.now().plusMillis(600000))
                .systeminfo(System.getProperty("os.name"))
                .logintime(String.valueOf(LocalDateTime.now()))
                .logouttime("")
                .isExpired(false)
                .build();
        return usersessionlogRepository.save(usersessionlog);
    }

    public Optional<Usersessionlog> findbyToken(String refreshtoken) {
        return usersessionlogRepository.findByRefreshtoken(refreshtoken);
    }

    public Usersessionlog verifyExpiration(Usersessionlog refreshtoken) {
        if (refreshtoken.getExpiryDate().compareTo(Instant.now()) < 0) {
            usersessionlogRepository.delete(refreshtoken);
            throw new RuntimeException(refreshtoken.getRefreshtoken() + " Refresh token was expired. Please make a new signin request");
        }
        return refreshtoken;
    }
}