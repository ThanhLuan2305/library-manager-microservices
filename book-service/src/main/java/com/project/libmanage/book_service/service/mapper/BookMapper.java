package com.project.libmanage.book_service.service.mapper;


import com.project.libmanage.book_service.entity.Book;
import com.project.libmanage.library_common.dto.request.BookCreateRequest;
import com.project.libmanage.library_common.dto.request.BookUpdateRequest;
import com.project.libmanage.library_common.dto.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    Book toBook(BookCreateRequest bookRequest);

    @Mapping(target = "bookType", source = "type")
    BookResponse toBookResponse(Book book);

    void updateBook(@MappingTarget Book book, BookUpdateRequest bookCreateRequest);

}
