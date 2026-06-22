package com.nmquan1503.backend_springboot.dtos.requests.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportRequestCreationRequest {

    @NotBlank
    @Size(min = 5, max = 1000)
    String message;
}
