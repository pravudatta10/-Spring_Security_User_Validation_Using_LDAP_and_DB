package com.nb.authenticate.repository;

import com.nb.authenticate.entity.Userinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserinfoRepository extends JpaRepository<Userinfo,Integer> {
    @Query("SELECT u FROM  Userinfo u WHERE u.dbuser = 'yes' AND u.userid = :userid")
    Optional <Userinfo> existingDbuser(@Param("userid") String userid);

    Userinfo findByUserid(String userid);
}
