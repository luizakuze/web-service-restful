package engtelecom.std.smart_home.exceptions;

public class AmbienteAtualizacaoInvalidaException extends RuntimeException {
    public AmbienteAtualizacaoInvalidaException(String msg) {
        super("Atualização inválida do ambiente: " + msg);
    }
}
