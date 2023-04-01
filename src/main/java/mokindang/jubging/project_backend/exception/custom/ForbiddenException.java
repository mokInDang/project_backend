package mokindang.jubging.project_backend.exception.custom;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException() {
    }

    public ForbiddenException(final String message) {
        super(message);
    }
}
