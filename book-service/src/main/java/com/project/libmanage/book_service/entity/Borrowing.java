package com.project.libmanage.book_service.entity;

import com.project.libmanage.library_common.entity.AuditTable;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "borrowings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Book book;

    @Column(nullable = false)
    private Instant borrowDate;

    @Column(nullable = false)
    private Instant dueDate;

    @Column
    private Instant returnDate;
}
