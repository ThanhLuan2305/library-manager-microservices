package com.project.libmanage.auth_service.entity;

import com.project.libmanage.library_common.constant.OtpType;
import com.project.libmanage.library_common.entity.AuditTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "otp_verification")
public class OtpVerification extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 255)
    private String email;

    @Column(nullable = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type;

    @Column(nullable = false)
    private Instant expiredAt;
}
