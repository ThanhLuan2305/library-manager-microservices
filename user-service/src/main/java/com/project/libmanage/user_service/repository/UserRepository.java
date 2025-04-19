package com.project.libmanage.user_service.repository;

import com.project.libmanage.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

//    @Query(value = """
//                SELECT COUNT(DISTINCT u.id)
//                FROM User u
//                JOIN u.roles r
//                WHERE r.name = 'USER'
//                AND u.verificationStatus = 'FULLY_VERIFIED'
//            """)
//    long countVerifiedUsersWithUserRole();
}
