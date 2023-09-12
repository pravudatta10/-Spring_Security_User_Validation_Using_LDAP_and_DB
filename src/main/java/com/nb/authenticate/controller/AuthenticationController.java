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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
