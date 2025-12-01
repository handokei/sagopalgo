package org.example.global.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T>{

    private boolean success;
    private String message;
    private T data;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true,message,data);
    }

    public static <T> ApiResponse<T> fail(String message,T errorDate) {
        return new ApiResponse<>(false,message,errorDate);
    }

}