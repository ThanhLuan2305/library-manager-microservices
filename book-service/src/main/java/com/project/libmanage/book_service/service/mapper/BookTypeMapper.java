package com.project.libmanage.book_service.service.mapper;


import com.project.libmanage.book_service.entity.BookType;
import com.project.libmanage.library_common.dto.response.BookTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookTypeMapper {
    BookTypeResponse toBookTypeResponse(BookType bookType);
}
