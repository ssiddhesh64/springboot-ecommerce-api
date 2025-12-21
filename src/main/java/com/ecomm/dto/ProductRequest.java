package com.ecomm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.NonNull;

public record ProductRequest(
        @NotBlank(message = "Product name required") String name,
        @NotBlank(message = "Product name required") String description,
        @NonNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NonNull @Min(0) Integer stock) {}
