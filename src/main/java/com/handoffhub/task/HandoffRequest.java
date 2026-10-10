package com.handoffhub.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HandoffRequest(
    @NotBlank @Size(max = 100) String nextAssignee,
    @NotBlank @Size(max = 1000) String note
) {}
