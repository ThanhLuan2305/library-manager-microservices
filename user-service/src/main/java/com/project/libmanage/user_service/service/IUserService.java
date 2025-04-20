package com.project.libmanage.user_service.service;

import com.project.libmanage.library_common.dto.request.*;
import com.project.libmanage.library_common.dto.response.UserResponse;
import com.project.libmanage.user_service.criteria.UserCriteria;
import com.project.libmanage.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    UserResponse createUser(UserCreateRequest request);

    Page<UserResponse> getUsers(Pageable pageable);

    Page<UserResponse> mapUserPageUserResponsePage(Page<User> userPage);

    UserResponse mapToUserResponseByMapper(Long id);

    UserResponse getUser(Long id);

    UserResponse getMyInfo();

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long userId);

    Page<UserResponse> searchUser(UserCriteria criteria, Pageable pageable);

    User findByEmail(String email);

    boolean isBannedFromBorrowing(String email);

    void updateLateReturn(String email);
    void updateEmail(ChangeMailRequest changeMailRequest);
    void updatePassword(ChangePasswordRequest changePasswordRequest);
    void updatePhone(ChangePhoneRequest changePhoneRequest);
    void createUserInternal(UserCreateRequest userCreateRequest);
}
