package hotchatmart.exception;

public class ResponseError {

    private String message;

    public ResponseError(final String message, final String... args) {
        this.message = String.format(message, (Object) args);
    }

    public ResponseError(final Exception ex) {
        this.message = ex.getMessage();
    }
}