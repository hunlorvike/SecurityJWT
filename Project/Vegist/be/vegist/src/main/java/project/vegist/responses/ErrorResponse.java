package project.vegist.responses;

import lombok.NoArgsConstructor;

public class ErrorResponse<T> extends BaseResponse<T> {
    public ErrorResponse(String message) {
        super("failed", message, null);
    }
}


