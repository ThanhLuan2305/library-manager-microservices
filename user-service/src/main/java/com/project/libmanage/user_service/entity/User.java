package com.project.libmanage.user_service.entity;

import com.project.libmanage.library_common.entity.AuditTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(unique = true, nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = true)
    private Instant birthDate;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private int lateReturnCount;

    public static final int MAX_LATE_RETURNS = 3;

    public boolean isBannedFromBorrowing() {
        return lateReturnCount >= MAX_LATE_RETURNS;
    }
}
