package engtelecom.std.smart_home.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RotinaInvalidaException extends RuntimeException {
    public RotinaInvalidaException(String msg) {
        super(msg);
    }
}
