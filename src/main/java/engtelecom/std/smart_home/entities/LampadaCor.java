package engtelecom.std.smart_home.entities;

/**
 * Enum para representar as cores disponíveis para uma lâmpada em uma casa inteligente.
 */
public enum LampadaCor {
    BRANCA("branca"),
    AMARELA("amarela"),
    AZUL("azul"),
    VERMELHA("vermelha"),
    VERDE("verde"),
    LARANJA("laranja"),
    ROSA("rosa"),
    ROXA("roxa");

    private final String nome;

    LampadaCor(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static LampadaCor getByNome(String nome) {
        for (LampadaCor cor : values()) {
            if (cor.getNome().equalsIgnoreCase(nome)) {
                return cor;
            }
        }
        throw new IllegalArgumentException("Cor de lâmpada inválida: " + nome);
    }
}
