package com.nb.authenticate.controller;

import com.nb.authenticate.dto.AuthenticationRequest;
import com.nb.authenticate.dto.AuthenticationResponse;
import com.nb.authenticate.dto.RefreshtokenRequest;
import com.nb.authenticate.entity.Usersessionlog;
import com.nb.authenticate.service.AuthenticationUtilService;
import com.nb.authenticate.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationUtilService authenticationUtilService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public AuthenticationResponse generateToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUserid(), authenticationRequest.getUserpassword()));
            String accessToken = authenticationUtilService.generateToken(authenticationRequest.getUserid());
            Usersessionlog refreshToken = refreshTokenService.createRefrehsToken(authenticationRequest.getUserid(), accessToken);
            return AuthenticationResponse.builder().accesstoken(accessToken).token(refreshToken.getRefreshtoken()).build();
        } catch (AuthenticationException e) {
            throw new RuntimeException("inavalid username/password");
        }
    }
    @PostMapping("/refreshtoken")
    public AuthenticationResponse refreshtoken(@RequestBody RefreshtokenRequest refreshtokenRequest){
        Optional<Usersessionlog> usersessionlog = refreshTokenService.findbyToken(refreshtokenRequest.getRefreshtoken());
        System.out.println(usersessionlog);
        return  refreshTokenService.findbyToken(refreshtokenRequest.getRefreshtoken())
                .map(refreshTokenService::verifyExpiration)
                .map(Usersessionlog::getUserinfo)
                .map(userinfo ->{
                    String accessToken=authenticationUtilService.generateToken(userinfo.getUserid());
                    return AuthenticationResponse.builder().accesstoken(accessToken).token(refreshtokenRequest.getRefreshtoken()).build();
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }

}
