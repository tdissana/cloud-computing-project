package lk.watupa.bff.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DownstreamException extends RuntimeException {

    private final HttpStatus status;

    public DownstreamException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
