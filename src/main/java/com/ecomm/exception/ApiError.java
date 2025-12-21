package com.ecomm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ApiError(
        int status, String error, String msg, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timeStamp) {}
