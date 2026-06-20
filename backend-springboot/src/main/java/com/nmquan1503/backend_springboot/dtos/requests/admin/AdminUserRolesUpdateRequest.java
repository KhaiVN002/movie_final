package com.nmquan1503.backend_springboot.dtos.requests.admin;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AdminUserRolesUpdateRequest(
        @NotNull List<Byte> roleIds
) {
}
