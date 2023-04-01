package mokindang.jubging.project_backend.exception;

import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int INDEX_OF_BASIC_ERROR = 0;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final RuntimeException e) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,BindException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({JwtException.class, ForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleJwtException(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentValidation(final MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(getDefaultErrorMessage(e)));
    }

    private String getDefaultErrorMessage(final MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getAllErrors()
                .get(INDEX_OF_BASIC_ERROR)
                .getDefaultMessage();
    }
}
