package engtelecom.std.smart_home.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando o tipo de ambiente informado é inválido.
 * Tipos válidos: "sala", "cozinha", "quarto", "banheiro", "lavanderia" (definidos em entities/AmbienteTipo.java).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AmbienteTipoInvalidoException extends RuntimeException {
    public AmbienteTipoInvalidoException(String nome) {
        super("Tipo de ambiente inválido: " + nome);
    }
}
