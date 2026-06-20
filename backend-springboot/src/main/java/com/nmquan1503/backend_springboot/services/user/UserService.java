package com.nmquan1503.backend_springboot.services.user;

import com.nmquan1503.backend_springboot.dtos.requests.user.UserCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.user.UserUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserPreviewResponse;
import com.nmquan1503.backend_springboot.entities.user.User;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.user.UserMapper;
import com.nmquan1503.backend_springboot.repositories.user.UserRepository;
import com.nmquan1503.backend_springboot.services.authentication.AuthenticationService;
import com.nmquan1503.backend_springboot.services.location.WardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    GenderService genderService;
    WardService wardService;
    RoleService roleService;
    PasswordEncoder passwordEncoder;
    UserRoleService userRoleService;

//    @PreAuthorize("hasRole('ADMIN')")
//    public List<UserResponse> getAllUsers() {
//        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
//    }

//    @PreAuthorize("hasRole('ADMIN')")
//    public UserResponse getUser(Long userId) {
//        return userMapper.toUserResponse(fetchUserById(userId));
//    }
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserPreviewResponse> getUserPreviews(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserPreviewResponse);
    }

    @Transactional
    public void createUser(UserCreationRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new GeneralException(ResponseCode.PHONE_USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new GeneralException(ResponseCode.EMAIL_USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        if (user.getSpecificAddress() != null && user.getSpecificAddress().isBlank()) {
            user.setSpecificAddress(null);
        }
        user.setFullName(formatName(user.getFullName()));
        user.setGender(genderService.fetchGenderById(request.getGenderId()));
        user.setWard(wardService.fetchWardById(request.getWardId()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationTime(LocalDateTime.now());
        user = userRepository.save(user);
        userRoleService.saveWithRoleName(user, "USER");
    }


    public UserDetailResponse getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal()==null || !(authentication.getPrincipal() instanceof Number)) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }
        Long userId = (Long) authentication.getPrincipal();
        User user = fetchUserById(userId);
        return userMapper.toUserDetailResponse(user);
    }

    public UserPreviewResponse getMyInfoPreview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal()==null || !(authentication.getPrincipal() instanceof Number)) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }
        Long userId = (Long) authentication.getPrincipal();
        User user = fetchUserById(userId);
        return userMapper.toUserPreviewResponse(user);
    }

    @PreAuthorize("#userId == authentication.principal")
    public UserDetailResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = fetchUserById(userId);
        userMapper.updateUser(user, request);
        if (user.getSpecificAddress() != null && user.getSpecificAddress().isBlank()) {
            user.setSpecificAddress(null);
        }
        user.setFullName(formatName(user.getFullName()));
        if (request.getGenderId() != null) {
            user.setGender(genderService.fetchGenderById(request.getGenderId()));
        }
        if (request.getWardId() != null) {
            user.setWard(wardService.fetchWardById(request.getWardId()));
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userMapper.toUserDetailResponse(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal")
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = fetchUserById(userId);
        try {
            String folder = "uploads/";
            File dir = new File(folder);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir.getAbsoluteFile(), fileName);

            file.transferTo(dest);

            String avatarUrl = "/uploads/" + fileName;
            user.setAvatarURL(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Upload file failed: " + e.getMessage(), e);
        }
    }

    public User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
    }

    public User fetchUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND));
    }

    public User fetchUserByPhoneOrEmail(String identifier) {
        return userRepository.findByPhone(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new GeneralException(ResponseCode.USER_NOT_FOUND)));
    }

    String formatName(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim().replaceAll("\\s+", " ");
        String[] words = name.split(" ");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            formattedName.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return formattedName.toString().trim();
    }

}
