package com.nb.authenticate.service;

import com.nb.authenticate.dto.UserinfoRequest;
import com.nb.authenticate.entity.Userinfo;
import com.nb.authenticate.repository.UserinfoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserinfoService {
    @Autowired
    UserinfoRepository userinfoRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    public String saveUSerinfo(UserinfoRequest userinfoRequest){
        Userinfo userinfo=this.modelMapper.map(userinfoRequest,Userinfo.class);
        userinfo.setUserpassword(passwordEncoder.encode(userinfoRequest.getUserpassword()));
        userinfoRepository.save(userinfo);
        return "User Saved Successfully";
    }
    /*@PostConstruct
    public String addAdmin(){
        Userinfo userinfo =new Userinfo();
        userinfo.setUserid("pk");
        userinfo.setUserpassword(passwordEncoder.encode("pk"));
        userinfo.setUsername("Pravudatta");
        userinfo.setDbuser("yes");
        userinfo.setEmailid("kpds@gmail.com");
        userinfo.setPhone("123456");
        userinfo.setLdapuser("no");
        userinfoRepository.save(userinfo);
        return "admin added";
    }*/

}
