package com.nb.authenticate.repository;

import com.nb.authenticate.entity.Usersessionlog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersessionlogRepository extends JpaRepository<Usersessionlog,Integer> {
    Optional<Usersessionlog> findByRefreshtoken(String refreshtoken);
}
