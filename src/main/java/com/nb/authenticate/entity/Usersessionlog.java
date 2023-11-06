package com.nb.authenticate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Usersessionlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int  logid ;
    private int  userloginid ;
    private String  accesstoken;
    private String  refreshtoken ;
    private String  ipaddress ;
    private Instant expiryDate;
    private String  systeminfo;
    private String logintime ;
    private String  logouttime;
    private Boolean isExpired;
    @OneToOne
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private Userinfo userinfo;

}
