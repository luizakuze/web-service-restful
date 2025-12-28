package engtelecom.std.smart_home.entities;

/**
 * Enum para representar os canais disponíveis em uma televisão.
 */
public enum TelevisaoCanal {

    GLOBO(1, "Globo"),
    SBT(2, "SBT"),
    RECORD(3, "Record"),
    BAND(4, "Band"),
    CULTURA(5, "Cultura");

    private final int numero;
    private final String nome;

    TelevisaoCanal(int numero, String nome) {
        this.numero = numero;
        this.nome = nome;
    }

    public int getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public static TelevisaoCanal getByNumero(int numero) {
        for (TelevisaoCanal canal : values()) {
            if (canal.getNumero() == numero) {
                return canal;
            }
        }
        throw new IllegalArgumentException("Canal inválido: " + numero);
    }

    public static TelevisaoCanal getByNome(String nome) {
        for (TelevisaoCanal canal : values()) {
            if (canal.getNome().equalsIgnoreCase(nome)) {
                return canal;
            }
        }
        throw new IllegalArgumentException("Canal inválido: " + nome);
    }
}
