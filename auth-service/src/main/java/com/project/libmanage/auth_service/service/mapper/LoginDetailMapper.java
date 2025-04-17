package com.project.libmanage.auth_service.service.mapper;

import com.project.libmanage.auth_service.entity.LoginDetail;
import com.project.libmanage.library_common.dto.request.LoginDetailRequest;
import com.project.libmanage.library_common.dto.response.LoginDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginDetailMapper {
    LoginDetailResponse toLoginDetailResponse(LoginDetail loginDetail);

    LoginDetail toLoginDetail(LoginDetailRequest loginDetailRequest);

    void updateLoginDetail(@MappingTarget LoginDetail loginDetail, LoginDetailRequest loginDetailRequest);
}