package com.nmquan1503.backend_springboot.controllers.user;

import com.nmquan1503.backend_springboot.dtos.requests.user.UserCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.user.UserUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserPreviewResponse;
import com.nmquan1503.backend_springboot.services.user.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

//    @GetMapping
//    ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
//        ApiResponse<List<UserResponse>> response = ApiResponse.success(userService.getAllUsers());
//        return ResponseEntity.ok().body(response);
//    }

//    @GetMapping("/{userId}")
//    ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
//        ApiResponse<UserResponse> response = ApiResponse.success(userService.getUser(userId));
//        return ResponseEntity.ok().body(response);
//    }

    @PostMapping
    ResponseEntity<ApiResponse<Void>> createUser(@RequestBody @Valid UserCreationRequest request) {
        userService.createUser(request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{userId}")
    ResponseEntity<ApiResponse<UserDetailResponse>> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        ApiResponse<UserDetailResponse> response = ApiResponse.success(userService.updateUser(userId, request));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/myInfo")
    ResponseEntity<ApiResponse<UserDetailResponse>> getMyInfo() {
        ApiResponse<UserDetailResponse> response = ApiResponse.success(userService.getMyInfo());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/myInfo/preview")
    ResponseEntity<ApiResponse<UserPreviewResponse>> getMyInfoPreview() {
        ApiResponse<UserPreviewResponse> response = ApiResponse.success(
                userService.getMyInfoPreview()
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/previews")
    ResponseEntity<ApiResponse<Page<UserPreviewResponse>>> getUserPreviews(
            Pageable pageable
    ) {
        ApiResponse<Page<UserPreviewResponse>> response = ApiResponse.success(
                userService.getUserPreviews(pageable)
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{userId}/avatar")
    @PreAuthorize("#userId == authentication.principal")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        ApiResponse<String> response = ApiResponse.success(userService.uploadAvatar(userId, file));
        return ResponseEntity.ok().body(response);
    }

}
