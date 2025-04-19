package com.project.libmanage.book_service.service;

import com.project.libmanage.book_service.criteria.BookCriteria;
import com.project.libmanage.library_common.dto.request.BookCreateRequest;
import com.project.libmanage.library_common.dto.request.BookUpdateRequest;
import com.project.libmanage.library_common.dto.response.BookResponse;
import com.project.libmanage.library_common.dto.response.BorrowingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IBookService {

    BookResponse createBook(BookCreateRequest bookCreateRequest);

    BookResponse updateBook(BookUpdateRequest bookUpdateRequest, Long bookId);

    void deleteBook(Long id);

    Page<BookResponse> getBooks(Pageable pageable);

    Page<BookResponse> getBooksForAdmin(Pageable pageable);

    BookResponse getBook(Long id);

    BookResponse getBookForAdmin(Long id);

    BorrowingResponse borrowBook(Long bookId);

    BorrowingResponse returnBook(Long bookId);

    Page<BorrowingResponse> getBookBorrowByUser(Long userId, Pageable pageable);

    Page<BorrowingResponse> getBookBorrowForUser(Pageable pageable);

    Page<BorrowingResponse> getBookReturnForUser(Pageable pageable);

    void importBooks(MultipartFile file);

    Page<BookResponse> searchBook(BookCriteria criteria, Pageable pageable);
}
