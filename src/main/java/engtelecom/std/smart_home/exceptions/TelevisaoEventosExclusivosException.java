package engtelecom.std.smart_home.exceptions;

/**
 * Exceção lançada quando há tentativa de definir eventos mutuamente exclusivos na Televisão.
 * Exemplo: definir "app" e "canal" ao mesmo tempo.
 */
public class TelevisaoEventosExclusivosException extends RuntimeException {

    public TelevisaoEventosExclusivosException() {
        super("Não é permitido definir 'app' e 'canal' ao mesmo tempo.");
    }
}