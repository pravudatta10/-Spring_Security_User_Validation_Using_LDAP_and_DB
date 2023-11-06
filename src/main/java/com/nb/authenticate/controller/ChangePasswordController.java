package com.nb.authenticate.controller;

import com.nb.authenticate.dto.ChangePasswordRequest;
import com.nb.authenticate.service.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
public class ChangePasswordController {
    @Autowired
    private ChangePasswordService changePasswordService;
    @PostMapping("/ChangePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request ,HttpServletRequest requestHeader)
    {
        changePasswordService.changePassword(request, requestHeader);
        return ResponseEntity.ok().build();
    }
}
