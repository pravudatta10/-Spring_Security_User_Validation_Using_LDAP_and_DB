package com.nb.authenticate.controller;

import com.nb.authenticate.dto.UserinfoRequest;
import com.nb.authenticate.repository.UserinfoRepository;
import com.nb.authenticate.service.UserinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserinfoController {
    @Autowired
    private UserinfoService userinfoService;

    @PostMapping("/saveuser")
    public String save(@RequestBody UserinfoRequest userinfoRequest) {
        return userinfoService.saveUSerinfo(userinfoRequest);
    }
}
