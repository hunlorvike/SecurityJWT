package project.vegist.responses;

public class SuccessResponse<T> extends BaseResponse<T> {
    public SuccessResponse(T data) {
        super("success", null, data);
    }
}


