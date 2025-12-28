package engtelecom.std.smart_home.entities;

/**
 * Enum para representar os aplicativos disponíveis em uma televisão inteligente.
 */
public enum TelevisaoApp {

    NETFLIX("netflix"),
    AMAZON_PRIME("amazonprime"),
    DISNEY_PLUS("disneyplus"),
    YOUTUBE("youtube"),
    GLOBOPLAY("globoplay");

    private final String nome;

    TelevisaoApp(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static TelevisaoApp getByNome(String nome) {
        for (TelevisaoApp app : values()) {
            if (app.getNome().equalsIgnoreCase(nome)) {
                return app;
            }
        }
        throw new IllegalArgumentException("Aplicativo inválido: " + nome);
    }
}
