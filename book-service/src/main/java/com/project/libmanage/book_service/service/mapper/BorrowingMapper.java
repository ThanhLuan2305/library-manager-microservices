package com.project.libmanage.book_service.service.mapper;


import com.project.libmanage.book_service.entity.Borrowing;
import com.project.libmanage.library_common.dto.response.BorrowingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {

    BorrowingResponse toBorrowingResponse(Borrowing borrowing);
}
