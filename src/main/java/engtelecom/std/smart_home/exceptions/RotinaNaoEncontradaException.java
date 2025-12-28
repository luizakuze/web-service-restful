package engtelecom.std.smart_home.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há tentativa de cadastrar um cenário com rotina em formato inválido.
 * Um cenário deve possuir uma rotina válida e não pode ser cadastrado sem uma rotina definida.
 * Uma rotina é considerada inválida quando está ausente ou em formato incorreto, como,
 * por exemplo, uma lâmpada tentando acionar uma função de "limpeza" que não é suportada por esse tipo de dispositivo.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RotinaNaoEncontradaException extends RotinaInvalidaException {
    public RotinaNaoEncontradaException() {
        super("A rotina do cenário é inválida ou não foi encontrada.");
    }
}
