package com.project.libmanage.auth_service.service.mapper;


import com.project.libmanage.auth_service.entity.User;
import com.project.libmanage.library_common.dto.request.RegisterRequest;
import com.project.libmanage.library_common.dto.request.UserCreateRequest;
import com.project.libmanage.library_common.dto.request.UserUpdateRequest;
import com.project.libmanage.library_common.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    User fromRegisterRequest(RegisterRequest request);
}
