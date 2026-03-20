package lk.watupa.bff.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a downstream microservice returns a non-2xx response.
 * Carries the original HTTP status so the BFF can relay it to the client.
 */
@Getter
public class DownstreamException extends RuntimeException {

    private final HttpStatus status;

    public DownstreamException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
