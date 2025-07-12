package lsfzk.userservice.common.dto;

public record Result<T>(
        String status,   // e.g., "success", "error"
        String message,  // A human-readable message
        T data           // The actual data payload (can be null for errors)
) {
    // Static factory methods for convenience

    public static <T> Result<T> success(T data) {
        return new Result<>("success", "Request was successful.", data);
    }

    public static Result<Void> error(String message) {
        return new Result<>("error", message, null);
    }
}