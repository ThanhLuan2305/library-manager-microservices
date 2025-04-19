package com.project.libmanage.book_service.repository;

import com.project.libmanage.book_service.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTypeRepository extends JpaRepository<BookType, Long> {

}
