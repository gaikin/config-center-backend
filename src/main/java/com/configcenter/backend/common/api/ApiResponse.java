package com.configcenter.backend.common.api;

public record ApiResponse<T>(String returnCode, String errorMsg, T body) {

    public static <T> ApiResponse<T> success(T body) {
        return new ApiResponse<>("OK", "success", body);
    }

    public static <T> ApiResponse<T> failure(String returnCode, String errorMsg, T body) {
        return new ApiResponse<>(returnCode, errorMsg, body);
    }
}
