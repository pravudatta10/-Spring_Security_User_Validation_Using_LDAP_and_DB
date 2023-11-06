package com.nb.authenticate.service;

import com.nb.authenticate.dto.ChangePasswordRequest;
import com.nb.authenticate.entity.Userinfo;
import com.nb.authenticate.repository.UserinfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserinfoRepository userinfoRepository;
    @Autowired
    private AuthenticationUtilService authenticationUtilService;

    @Autowired
    private AuthenticationService authenticationService;

    public void changePassword(ChangePasswordRequest request, HttpServletRequest requestHeader) {
        String authorizationHeader = requestHeader.getHeader("Authorization");
        String userid = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            userid = authenticationUtilService.extractUsername(token);
        }
        UserDetails userDetails = authenticationService.loadUserByUsername(userid);
        String storedPassword = userDetails.getPassword(); // This is the stored encoded password
        String plainTextPassword = request.getCurrentPassword(); // This is the plain-text password entered by the user

        // Check if the current password is correct
        if (!passwordEncoder.matches(plainTextPassword, storedPassword)) {
            throw new IllegalStateException("Wrong password");
        }
        // Check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        Userinfo byUserid = userinfoRepository.findByUserid(userid);

        // update the password
        byUserid.setUserpassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userinfoRepository.save(byUserid);
    }
}
