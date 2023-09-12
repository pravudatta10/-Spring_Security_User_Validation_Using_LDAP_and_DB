package com.nb.authenticate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserinfoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    @Column(unique=true)
    private String userid;
    private String userpassword;
    private String emailid;
    private String phone;
    private String ldapuser;
    private String dbuser;
}
