package engtelecom.std.smart_home.exceptions;


/**
 * Exceção lançada quando um dispositivo com o ID especificado não é encontrado.
 */
public class DispositivoNaoEncontradoException extends RuntimeException {
    public DispositivoNaoEncontradoException(long id) {
        super("Não foi possível encontrar um dispositivo com o id: " + id);
    }
}