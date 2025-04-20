package com.project.libmanage.auth_service.service.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.libmanage.auth_service.entity.Role;
import com.project.libmanage.auth_service.entity.User;
import com.project.libmanage.auth_service.repository.RoleRepository;
import com.project.libmanage.auth_service.repository.UserRepository;
import com.project.libmanage.auth_service.security.JwtTokenProvider;
import com.project.libmanage.auth_service.service.IAuthenticationService;
import com.project.libmanage.auth_service.service.ILoginDetailService;
import com.project.libmanage.auth_service.service.IMaintenanceService;
import com.project.libmanage.auth_service.service.mapper.UserMapper;
import com.project.libmanage.library_common.client.ActivityLogFeignClient;
import com.project.libmanage.library_common.constant.ErrorCode;
import com.project.libmanage.library_common.constant.PredefinedRole;
import com.project.libmanage.library_common.constant.TokenType;
import com.project.libmanage.library_common.constant.UserAction;
import com.project.libmanage.library_common.dto.request.AuthenticationRequest;
import com.project.libmanage.library_common.dto.request.LogActionRequest;
import com.project.libmanage.library_common.dto.request.LoginDetailRequest;
import com.project.libmanage.library_common.dto.response.AuthenticationResponse;
import com.project.libmanage.library_common.dto.response.UserResponse;
import com.project.libmanage.library_common.exception.AppException;
import com.project.libmanage.library_common.util.CommonUtil;
import com.project.libmanage.library_common.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of {@link IAuthenticationService} for handling user authentication operations.
 * Manages login, logout, and token refresh processes using JWT-based authentication.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserRepository userRepository;              // Repository for user data access
    private final UserMapper userMapper;                      // Mapper for user-related conversions
    private final IMaintenanceService maintenanceService;     // Service to check maintenance mode
    private final RoleRepository roleRepository;              // Repository for role lookups
    private final JwtTokenProvider jwtTokenProvider;          // Utility for JWT token generation and verification
    private final AuthenticationManagerBuilder authenticationManagerBuilder; // Builds authentication manager
    private final ILoginDetailService loginDetailService;     // Service for managing login details
    private final CommonUtil commonUtil;                      // Utility for common functions (e.g., JTI generation)
    private final ActivityLogFeignClient activityLogFeignClient;     // Service for logging user actions
    private final CookieUtil cookieUtil;                      // Utility for cookie management

    @Value("${jwt.refresh-duration}")
    private long refreshDuration;                             // Duration (in seconds) for refresh token validity

    @Value("${jwt.valid-duration}")
    private long validDuration;                               // Duration (in seconds) for access token validity

    private static final String ACCESS_TOKEN_STR = "accessToken"; // Cookie key for access token
    private static final String REFRESH_TOKEN_STR = "refreshToken"; // Cookie key for refresh token

    /**
     * Authenticates a user with email and password, issuing JWT tokens upon success.
     *
     * @param aRequest the {@link AuthenticationRequest} containing:
     *                 - email: user's email (required)
     *                 - password: user's password (required)
     * @param response the {@link HttpServletResponse} to set authentication cookies
     * @return an {@link AuthenticationResponse} with access and refresh tokens
     * @throws AppException if:
     *                      - authentication fails (ErrorCode.UNAUTHENTICATED)
     *                      - user not found (ErrorCode.USER_NOT_EXISTED)
     *                      - system in maintenance mode for non-admins (ErrorCode.MAINTENACE_MODE)
     *                      - role not found (ErrorCode.ROLE_NOT_EXISTED)
     * @implNote Authenticates via Spring Security, generates tokens, stores refresh token details,
     * sets cookies, and logs the login action.
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest aRequest, HttpServletResponse response) {
        // Create authentication token; email as principal, password as credential
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                aRequest.getEmail(), aRequest.getPassword());
        // Authenticate using Spring Security; throws exception if credentials invalid
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Set authentication in security context; enables downstream access
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Fetch user; assumes email uniqueness
        User userDB = userRepository.findByEmail(aRequest.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Check maintenance mode; restricts non-admin users
        Role role = roleRepository.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        if (maintenanceService.isMaintenanceMode() && userDB.getRoles().contains(role)) {
            throw new AppException(ErrorCode.MAINTENACE_MODE); // Typo: MAINTENANCE_MODE
        }

        // Generate unique JTI and tokens; JTI links access and refresh tokens
        String jti = commonUtil.generateJTI();
        String accessToken = jwtTokenProvider.generateToken(userDB, TokenType.ACCESS, jti);
        String refreshToken = jwtTokenProvider.generateToken(userDB, TokenType.REFRESH, jti);

        // Save refresh token details; tracks session validity
        saveRefreshToken(refreshToken);

        // Clear existing cookies; ensures clean state
        cookieUtil.removeCookie(response, ACCESS_TOKEN_STR);
        cookieUtil.removeCookie(response, REFRESH_TOKEN_STR);
        // Set new cookies; casts durations to int (assumes seconds)
        cookieUtil.addCookie(response, ACCESS_TOKEN_STR, accessToken, (int) validDuration);
        cookieUtil.addCookie(response, REFRESH_TOKEN_STR, refreshToken, (int) refreshDuration);

        // Log successful login; audit trail for user action
        activityLogFeignClient.logAction(LogActionRequest.builder()
                .userId(userDB.getId())
                .email(userDB.getEmail())
                .action(UserAction.LOGIN)
                .details("User login success!!!")
                .beforeChange(null)
                .afterChange(null)
                .build()
        );

        // Return tokens in response DTO
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Saves refresh token details to track session validity.
     *
     * @param token the refresh token to save
     * @throws AppException if token verification or saving fails (ErrorCode.UNAUTHENTICATED)
     * @implNote Verifies token, extracts claims, and stores login details via LoginDetailService.
     */
    private void saveRefreshToken(String token) {
        try {
            // Verify token; ensures integrity and authenticity
            SignedJWT signedJWT = jwtTokenProvider.verifyToken(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            // Save login details; includes email, JTI, and expiration
            loginDetailService.createLoginDetail(LoginDetailRequest
                    .builder()
                    .email(claimsSet.getSubject())
                    .jti(claimsSet.getJWTID())
                    .enabled(true) // Marks session as active
                    .expiredAt(claimsSet.getExpirationTime().toInstant())
                    .build());
        } catch (Exception e) {
            // Log error; provides context for debugging
            log.error("Error saving refresh token: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * Logs out a user by invalidating their tokens and clearing cookies.
     *
     * @param accessToken the access token to invalidate
     * @param response    the {@link HttpServletResponse} to clear authentication cookies
     * @throws AppException if token verification or logout fails (ErrorCode.UNAUTHENTICATED)
     * @implNote Disables login details by JTI, clears cookies, and logs the logout action.
     */
    @Override
    public void logout(String accessToken, HttpServletResponse response) {
        try {
            // Verify access token; ensures it's valid before proceeding
            SignedJWT signedJWT = jwtTokenProvider.verifyToken(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Disable session; uses JTI to invalidate both tokens
            String jwtID = claimsSet.getJWTID();
            loginDetailService.disableLoginDetailById(jwtID);

            // Clear cookies; removes tokens from client
            cookieUtil.removeCookie(response, ACCESS_TOKEN_STR);
            cookieUtil.removeCookie(response, REFRESH_TOKEN_STR);

            // Fetch user; assumes subject is email
            User userDB = userRepository.findByEmail(claimsSet.getSubject()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            // Log logout action; audit trail for user activity
            activityLogFeignClient.logAction(LogActionRequest.builder()
                    .userId(userDB.getId())
                    .email(userDB.getEmail())
                    .action(UserAction.LOGOUT)
                    .details("User logout success!!!")
                    .beforeChange(null)
                    .afterChange(null)
                    .build()
            );
        } catch (Exception e) {
            // Log error with stack trace; aids debugging
            log.error("Error logout token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    @Override
    public UserResponse getAuthenticatedUser() {
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Fetch user by email from database
        return userMapper.toUserResponse(user); // Throw if not found
    }

    /**
     * Refreshes authentication tokens using a valid refresh token.
     *
     * @param refreshToken the refresh token to verify and refresh
     * @param response     the {@link HttpServletResponse} to update authentication cookies
     * @return an {@link AuthenticationResponse} with new access and refresh tokens
     * @throws AppException if:
     *                      - token is invalid or not a refresh token (ErrorCode.JWT_TOKEN_INVALID)
     *                      - user not found (ErrorCode.USER_NOT_EXISTED)
     *                      - parsing fails (ErrorCode.UNAUTHENTICATED)
     * @implNote Verifies refresh token, updates session expiration, generates new tokens,
     * and updates cookies.
     */
    @Override
    public AuthenticationResponse refreshToken(String refreshToken, HttpServletResponse response) {
        JWTClaimsSet claimsSet;
        String typeToken;
        // Verify token; ensures it's valid and unexpired
        SignedJWT signedJWT = jwtTokenProvider.verifyToken(refreshToken);

        try {
            // Extract claims; contains token metadata
            claimsSet = signedJWT.getJWTClaimsSet();
            typeToken = claimsSet.getStringClaim("type");
        } catch (ParseException e) {
            // Log parsing error; indicates malformed token
            log.error("Error parsing token claims", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Validate token type; ensures it's a refresh token
        if (!TokenType.REFRESH.name().equals(typeToken)) {
            log.error("Token is not refresh token");
            throw new AppException(ErrorCode.JWT_TOKEN_INVALID);
        }

        // Extract JTI and email; reuses JTI for continuity
        String jwtID = claimsSet.getJWTID();
        String email = claimsSet.getSubject();

        // Fetch user; fails if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Generate new tokens; keeps same JTI
        String accessTokenGrt = jwtTokenProvider.generateToken(user, TokenType.ACCESS, jwtID);
        String refreshTokenGrt = jwtTokenProvider.renewRefreshToken(user, jwtID);

        // Update session expiration; extends refresh token validity
        loginDetailService.updateLoginDetailIsEnable(jwtID, Instant.now().plus(refreshDuration, ChronoUnit.SECONDS));

        // Update cookies; replaces old tokens
        cookieUtil.removeCookie(response, ACCESS_TOKEN_STR);
        cookieUtil.removeCookie(response, REFRESH_TOKEN_STR);
        cookieUtil.addCookie(response, ACCESS_TOKEN_STR, accessTokenGrt, (int) validDuration);
        cookieUtil.addCookie(response, REFRESH_TOKEN_STR, refreshTokenGrt, (int) refreshDuration);

        // Return new tokens in response DTO
        return AuthenticationResponse.builder()
                .accessToken(accessTokenGrt)
                .refreshToken(refreshTokenGrt)
                .build();
    }
}