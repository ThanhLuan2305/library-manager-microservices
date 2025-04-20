package com.project.libmanage.user_service.service.impl;

import com.project.libmanage.library_common.client.ActivityLogFeignClient;
import com.project.libmanage.library_common.client.AuthFeignClient;
import com.project.libmanage.library_common.constant.ErrorCode;
import com.project.libmanage.library_common.constant.UserAction;
import com.project.libmanage.library_common.dto.request.*;
import com.project.libmanage.library_common.dto.response.UserResponse;
import com.project.libmanage.library_common.exception.AppException;
import com.project.libmanage.user_service.criteria.UserCriteria;
import com.project.libmanage.user_service.entity.User;
import com.project.libmanage.user_service.repository.UserRepository;
import com.project.libmanage.user_service.service.IUserService;
import com.project.libmanage.user_service.service.mapper.UserMapper;
import com.project.libmanage.user_service.specification.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link IUserService} for managing user-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;              // Repository for user CRUD operations
    private final UserMapper userMapper;                      // Mapper for entity-DTO conversion
    private final PasswordEncoder passwordEncoder;            // Utility for password encryption
    private final UserQueryService userQueryService;          // Service for complex user queries
    private final ActivityLogFeignClient activityLogFeignClient;     // Service for logging actions
    private final AuthFeignClient authFeignClient;

    private static final String ROLE_ADMIN = "ADMIN";

    /**
     * Creates a new user and assigns a default role.
     *
     * @param request the request object containing user creation details:
     *                - email: unique email address of the new user
     *                - password: raw password to be encrypted
     *                - listRole: set of role names to assign
     * @return a {@link UserResponse} containing the created user's details:
     * - id: unique identifier of the user
     * - email: user's email address
     * - roles: set of assigned role names
     * @throws AppException if:
     *                      - email already exists (ErrorCode.USER_EXISTED)
     *                      - any role does not exist (ErrorCode.ROLE_NOT_EXISTED)
     *                      - database error occurs (ErrorCode.UNCATEGORIZED_EXCEPTION)
     *                      - admin is not authenticated (ErrorCode.UNAUTHORIZED)
     * @implNote This method encrypts the user's password and assigns the default
     * user role. The user is marked as not verified.
     */
    @Transactional
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        // Convert request DTO to User entity
        User user = userMapper.toUser(request);

        // Encrypt the raw password from request and set it to the user entity
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Check if the email already exists in the database
        if (userRepository.existsByEmail(request.getEmail())) {
            // Throw exception if email is already taken
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Check role admin, deny create user with role admin
        for (String r : request.getListRole()) {
            if (r.equals(ROLE_ADMIN)) {
                throw new AppException(ErrorCode.ADMIN_ONLY_ONE);
            }
        }

        // Get the authenticated admin performing this action
        User userAction = getAuthenticatedUser();
        authFeignClient.createAccount(request);
        try {
            // Save the new user to the database
            userRepository.save(user);
            // Convert saved user to response DTO
            UserResponse userResponse = userMapper.toUserResponse(user);
            // Log the creation action by admin
            activityLogFeignClient.logAction(LogActionRequest.builder()
                    .userId(userAction.getId())
                    .email(userAction.getEmail())
                    .action(UserAction.ADMIN_CREATE_USER)
                    .details("Admin create new user with email: " + user.getEmail())
                    .beforeChange(null)
                    .afterChange(userResponse)
                    .build()
            );

            // Return the response to the caller
            return userResponse;
        } catch (DataIntegrityViolationException exception) {
            // Handle database-specific errors (e.g., constraint violations)
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return a {@link User} entity representing the authenticated user, typically an admin
     * @throws AppException if:
     *                      - no security context or authentication exists (ErrorCode.UNAUTHORIZED)
     *                      - authenticated user not found (ErrorCode.USER_NOT_EXISTED)
     * @implNote This method extracts the email from the security context and queries
     * the database to return the corresponding user.
     */
    private User getAuthenticatedUser() {
        // Get current security context holding authentication details
        SecurityContext jwtContext = SecurityContextHolder.getContext();
        // Check if context or authentication is invalid or user is not authenticated
        if (jwtContext == null || jwtContext.getAuthentication() == null ||
                !jwtContext.getAuthentication().isAuthenticated()) {
            // Throw exception if authentication is missing or invalid
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        // Log the authenticated user's email for debugging
        log.info("Authentication {}", jwtContext.getAuthentication().getName());

        // Extract email from authentication object
        String email = jwtContext.getAuthentication().getName();
        // Fetch user by email from database
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Throw if not found
    }

    /**
     * Fetches a paginated list of all users.
     *
     * @param pageable the pagination details:
     *                 - page: page number
     *                 - size: number of items per page
     *                 - sort: sorting criteria
     * @return a {@link Page} of {@link UserResponse} containing user details
     * @throws AppException if no users exist (ErrorCode.USER_NOT_EXISTED)
     * @implNote This method fetches all users from the repository and returns them
     * in a paginated format.
     */
    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        // Fetch all users with pagination from repository
        Page<User> pageUser = userRepository.findAll(pageable);
        // Check if the page is empty (no users found)
        if (pageUser.isEmpty()) {
            // Throw exception if no users exist
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        // Convert page of users to page of responses
        return mapUserPageUserResponsePage(pageUser);
    }

    /**
     * Converts a page of users to a page of user responses.
     *
     * @param userPage the {@link Page} of {@link User} entities to convert
     * @return a {@link Page} of {@link UserResponse} containing mapped user details
     * @implNote This method maps the content of the user page to a list of user
     * responses and returns the paginated response.
     */
    @Override
    public Page<UserResponse> mapUserPageUserResponsePage(Page<User> userPage) {
        // Map each user entity to a response DTO
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user -> mapToUserResponseByMapper(user.getId())) // Convert user by ID
                .toList(); // Collect into list

        // Create and return a new paginated response with mapped data
        return new PageImpl<>(userResponses, userPage.getPageable(), userPage.getTotalElements());
    }

    /**
     * Fetches the response of a user by their ID.
     *
     * @param id the unique identifier of the user to map
     * @return a {@link UserResponse} containing the user's details
     * @throws AppException if the user does not exist (ErrorCode.USER_NOT_EXISTED)
     * @implNote This method retrieves a specific user by ID and returns the user
     * response.
     */
    @Override
    public UserResponse mapToUserResponseByMapper(Long id) {
        // Fetch user by ID from repository
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Convert user entity to response DTO
        return userMapper.toUserResponse(user);
    }

    /**
     * Fetches the details of a specific user.
     *
     * @param id the unique identifier of the user to retrieve
     * @return a {@link UserResponse} containing the user's details
     * @throws AppException if the user does not exist (ErrorCode.USER_NOT_EXISTED)
     * @implNote This method retrieves a specific user and returns their response.
     */
    @Override
    public UserResponse getUser(Long id) {
        // Fetch user by ID and map directly to response DTO
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    /**
     * Fetches the details of the currently authenticated user.
     *
     * @return a {@link UserResponse} containing the authenticated user's details
     * @throws AppException if:
     *                      - user is not authenticated (ErrorCode.UNAUTHORIZED)
     *                      - user does not exist (ErrorCode.USER_NOT_EXISTED)
     *                      - unexpected error occurs (ErrorCode.UNCATEGORIZED_EXCEPTION)
     * @implNote This method retrieves the currently logged-in user using the
     * security context.
     */
    @Override
    public UserResponse getMyInfo() {
        try {
            // Get current security context
            SecurityContext jwtContext = SecurityContextHolder.getContext();

            // Validate security context and authentication
            if (jwtContext == null || jwtContext.getAuthentication() == null ||
                    !jwtContext.getAuthentication().isAuthenticated()) {
                // Throw exception if authentication is missing or invalid
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            // Extract email from authentication object
            String email = jwtContext.getAuthentication().getName();

            // Fetch user by email from database
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

            // check null user (already handled by orElseThrow)
            if (user == null) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }

            // Convert user entity to response DTO
            return userMapper.toUserResponse(user);
        } catch (AppException e) {
            log.error("Error getting user info: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getMyInfo: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Updates the information of an existing user.
     *
     * @param id      the unique identifier of the user to update
     * @param request the request object containing updated user details:
     *                - email: new email address (optional)
     *                - password: new password (optional)
     *                - listRole: new set of role names (optional)
     * @return a {@link UserResponse} containing the updated user's details
     * @throws AppException if:
     *                      - user does not exist (ErrorCode.USER_NOT_EXISTED)
     *                      - user is an admin (ErrorCode.CANNOT_UPDATE_ADMIN)
     *                      - any role does not exist (ErrorCode.ROLE_NOT_EXISTED)
     *                      - unexpected error occurs (ErrorCode.UNCATEGORIZED_EXCEPTION)
     * @implNote This method updates the user's information, password.
     */
    @Transactional
    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        // Fetch existing user by ID
        User u = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Store current user state as response DTO to save activity log
        UserResponse oldeUserResponse = userMapper.toUserResponse(u);
        // Check role admin, deny update user with change role admin
        for (String r : request.getListRole()) {
            if (r.equals(ROLE_ADMIN)) {
                throw new AppException(ErrorCode.ADMIN_ONLY_ONE);
            }
        }
        // Get authenticated admin performing the update
        User userAction = getAuthenticatedUser();
        authFeignClient.updateAccount(u.getEmail(), request);
        try {
            // Store current encrypted password
            String oldPassword = u.getPassword();
            // Update user fields from request DTO
            userMapper.updateUser(u, request);

            // Check if new password is provided and not blank
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                // Encrypt and set new password if provided
                u.setPassword(passwordEncoder.encode(request.getPassword()));
            } else {
                // Retain old password if no new one provided
                u.setPassword(oldPassword);
            }

            // Save updated user to database
            User newUser = userRepository.save(u);
            // Convert updated user to response DTO
            UserResponse userResponse = userMapper.toUserResponse(newUser);
            // Log the update action by admin
            activityLogFeignClient.logAction(LogActionRequest.builder()
                    .userId(userAction.getId())
                    .email(userAction.getEmail())
                    .action(UserAction.ADMIN_UPDATE_USER)
                    .details("Admin update user with email: " + u.getEmail())
                    .beforeChange(oldeUserResponse)
                    .afterChange(userResponse)
                    .build()
            );
            // Return updated response
            return userResponse;
        } catch (AppException e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId the unique identifier of the user to delete
     * @throws AppException if:
     *                      - user does not exist (ErrorCode.USER_NOT_EXISTED)
     *                      - user is an admin (ErrorCode.CANNOT_DELETE_ADMIN)
     *                      - user has active borrowings (ErrorCode.USER_CANNOT_BE_DELETED)
     *                      - unexpected error occurs (ErrorCode.UNCATEGORIZED_EXCEPTION)
     * @implNote This method checks if the user has borrowings before deleting. If
     * so, the user is marked as deleted instead of being fully deleted.
     */
    @Transactional
    @Override
    public void deleteUser(Long userId) {
        // Fetch user by ID
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Get authenticated admin performing the deletion
        User userAction = getAuthenticatedUser();
        try {
            // Mark user as deleted (soft delete)
            user.setDeleted(true);
            authFeignClient.deleteAccount(user.getEmail());
            // Save the updated (deleted) user
            userRepository.save(user);
            // Log the deletion action by admin
            activityLogFeignClient.logAction(LogActionRequest.builder()
                    .userId(userAction.getId())
                    .email(userAction.getEmail())
                    .action(UserAction.ADMIN_DELETE_USER)
                    .details("Admin deleted user with email: " + user.getEmail())
                    .beforeChange(userMapper.toUserResponse(userAction))
                    .afterChange(null)
                    .build()
            );
        } catch (Exception e) {
            // Log and wrap unexpected exceptions in custom exception
            log.error("Error deleting user: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Searches for users based on specified criteria.
     *
     * @param criteria the {@link UserCriteria} object containing search parameters
     * @param pageable the pagination details:
     *                 - page: page number
     *                 - size: number of items per page
     *                 - sort: sorting criteria
     * @return a {@link Page} of {@link UserResponse} containing matching users
     * @implNote This method delegates to UserQueryService for criteria-based search
     * and maps results to a paginated response.
     */
    @Override
    public Page<UserResponse> searchUser(UserCriteria criteria, Pageable pageable) {
        // Fetch users based on search criteria with pagination
        Page<User> users = userQueryService.findByCriteria(criteria, pageable);
        // Convert page of users to page of responses
        return mapUserPageUserResponsePage(users);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return a {@link User} entity matching the email
     * @throws AppException if no user exists with the given email (ErrorCode.USER_NOT_EXISTED)
     * @implNote This method queries the database by email and returns the user entity.
     */
    @Override
    public User findByEmail(String email) {
        // Fetch user by email from repository
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public boolean isBannedFromBorrowing(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.isBannedFromBorrowing();
    }

    @Override
    public void updateLateReturn(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setLateReturnCount(user.getLateReturnCount() + 1);
        userRepository.save(user);
    }

    @Override
    public void updateEmail(ChangeMailRequest changeMailRequest) {
        // Check authentication; ensures user is logged in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Validate old email; ensures request matches authenticated user
        String currentEmail = authentication.getName();
        if (!currentEmail.equals(changeMailRequest.getOldEmail())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Check new email availability; prevents duplicates
        if (userRepository.existsByEmail(changeMailRequest.getNewEmail())) {
            throw new AppException(ErrorCode.MAIL_EXISTED);
        }

        // Fetch user; fails if not found
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setEmail(changeMailRequest.getNewEmail());
        userRepository.save(user); // Persist changes
        SecurityContextHolder.clearContext();
    }

    @Override
    public void updatePassword(ChangePasswordRequest changePasswordRequest) {
        // Fetch security context; assumes JWT-based authentication is configured
        SecurityContext jwtContex = SecurityContextHolder.getContext();
        // Extract email from authenticated principal; assumes email is the subject
        String email = jwtContex.getAuthentication().getName();

        // Retrieve user by email; fails fast if user doesn't exist
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Verify old password matches stored hash; uses PasswordEncoder for security
        boolean rs = passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword());
        // Fail if old password doesn't match; reuses UNAUTHENTICATED for simplicity
        if (!rs) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH); // Indicates authentication failure
        }

        // Check if new password is different from old; prevents redundant updates
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_DUPLICATED); // Enforces password change policy
        }

        // Encrypt new password and update user entity; assumes encoder is consistent
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        // Persist updated user; assumes no concurrent modifications
        userRepository.save(user);
    }

    @Override
    public void updatePhone(ChangePhoneRequest changePhoneRequest) {
        User user = getAuthenticatedUser();

        // Validate old phone; ensures request matches current phone
        if (!user.getPhoneNumber().equals(changePhoneRequest.getOldPhoneNumber())) {
            throw new AppException(ErrorCode.OLD_PHONE_INVALID);
        }

        // Update phone number; applies new value
        user.setPhoneNumber(changePhoneRequest.getNewPhoneNumber());
        userRepository.save(user); // Persist changes
    }

    @Override
    public void createUserInternal(UserCreateRequest userCreateRequest) {
        User user = userMapper.toUser(userCreateRequest);

        // Encrypt the raw password from request and set it to the user entity
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        // Check if the email already exists in the database
        if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
            // Throw exception if email is already taken
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        try {
            // Save the new user to the database
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            // Handle database-specific errors (e.g., constraint violations)
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

}