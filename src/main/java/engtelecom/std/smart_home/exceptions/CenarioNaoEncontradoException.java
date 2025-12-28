package engtelecom.std.smart_home.exceptions;

/**
 * Exceção lançada quando um cenário com o ID especificado não é encontrado.
 */
public class CenarioNaoEncontradoException extends RuntimeException {
    public CenarioNaoEncontradoException(long id) {
        super("Não foi possível encontrar um cenário com o id: " + id);
    }
}