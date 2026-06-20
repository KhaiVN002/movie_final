package com.nmquan1503.backend_springboot.controllers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.BranchOptionResponse;
import com.nmquan1503.backend_springboot.services.theater.BranchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomDetailResponse;
import com.nmquan1503.backend_springboot.services.theater.RoomService;

import java.util.List;

@Controller
@RequestMapping("/branches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchController {

    BranchService branchService;
    RoomService roomService;

    @GetMapping("/options/province/{provinceId}")
    ResponseEntity<ApiResponse<List<BranchOptionResponse>>> getBranchOptionsByProvinceId(
            @PathVariable Short provinceId
    ) {
        ApiResponse<List<BranchOptionResponse>> response = ApiResponse.success(
            branchService.getBranchOptionsByProvinceId(provinceId)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/options")
    ResponseEntity<ApiResponse<List<BranchOptionResponse>>> getAllBranchOptions() {
        ApiResponse<List<BranchOptionResponse>> response = ApiResponse.success(
            branchService.getAllBranchOptions()
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{branchId}/rooms")
    ResponseEntity<ApiResponse<List<RoomDetailResponse>>> getRoomsByBranchId(
            @PathVariable Short branchId
    ) {
        ApiResponse<List<RoomDetailResponse>> response = ApiResponse.success(
                roomService.getRoomsByBranchId(branchId)
        );
        return ResponseEntity.ok().body(response);
    }

}
