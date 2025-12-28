package engtelecom.std.smart_home.entities;

import engtelecom.std.smart_home.exceptions.AmbienteTipoInvalidoException;

/**
 * Enum para representar os tipos de ambientes disponíveis na casa inteligente.
 */
public enum AmbienteTipo {

    SALA("sala"),
    COZINHA("cozinha"),
    QUARTO("quarto"),
    BANHEIRO("banheiro"),
    LAVANDERIA("lavanderia");

    private final String nome;

    AmbienteTipo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        if (nome == null) {
            return null;
        }
        return nome.toLowerCase();
    }

    public static AmbienteTipo getByNome(String nome) {
        for (AmbienteTipo tipo : values()) {
            if (tipo.getNome().equalsIgnoreCase(nome)) {
                return tipo;
            }
        }
        throw new AmbienteTipoInvalidoException(nome);
    }

}
