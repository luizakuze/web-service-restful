package engtelecom.std.smart_home.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AmbienteNaoEncontradoException extends RuntimeException {
    public AmbienteNaoEncontradoException(long id) {
        super("Ambiente não encontrado: " + id);
    }
}
