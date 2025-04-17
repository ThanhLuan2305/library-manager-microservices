package com.project.libmanage.auth_service.repository;

import com.project.libmanage.auth_service.entity.OtpVerification;
import com.project.libmanage.library_common.constant.OtpType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    @Query("SELECT COUNT(o) > 0 FROM OtpVerification o WHERE " +
            "(:isPhone = TRUE AND o.phoneNumber = :phoneOrEmail AND o.type = :type) OR " +
            "(:isPhone = FALSE AND o.email = :phoneOrEmail AND o.type = :type)")
    boolean existsByPhoneOrEmailAndType(@Param("phoneOrEmail") String phoneOrEmail,
                                        @Param("type") OtpType type,
                                        @Param("isPhone") boolean isPhone);

    @Query("SELECT o FROM OtpVerification o WHERE o.email = :email AND o.type = :type")
    Optional<OtpVerification> findByEmailAndType(@Param("email") String email, @Param("type") OtpType type);

    @Query("SELECT o FROM OtpVerification o WHERE o.phoneNumber = :phoneNumber AND o.type = :type")
    Optional<OtpVerification> findByPhoneNumberAndType(@Param("phoneNumber") String phoneNumber,
                                                       @Param("type") OtpType type);
}
