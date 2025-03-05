package com.example.user_service.repositories;


import com.example.user_service.models.UserSSO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSSORepository extends JpaRepository<UserSSO, Long> {
    Optional<UserSSO> findByAuthId(String authId);
}
