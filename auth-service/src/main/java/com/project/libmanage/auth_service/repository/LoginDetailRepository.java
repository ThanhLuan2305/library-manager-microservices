package com.project.libmanage.auth_service.repository;

import com.project.libmanage.auth_service.entity.LoginDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginDetailRepository extends JpaRepository<LoginDetail, Long> {
    Optional<LoginDetail> findByJti(String jti);

    Optional<LoginDetail> findByUserId(Long userId);

    @Query("SELECT l FROM LoginDetail l WHERE l.jti = :jti AND l.enabled = true")
    Optional<LoginDetail> findByJtiAndEnabled(@Param("jti") String jti);

    boolean existsByJti(String jti);

    void deleteByUserId(Long userId);
}
