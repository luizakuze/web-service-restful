package engtelecom.std.smart_home.entities;

import java.util.ArrayList;

/**
 * Classe para representar um ambiente da casa inteligente, como sala, cozinha, quarto, etc.
 * Cada ambiente possui um tipo (sala, cozinha, ...) e uma lista de IDs de dispositivos associados a ele.
 */
public class Ambiente {

    private AmbienteTipo tipo;
    private ArrayList<Long> dispositivos = new ArrayList<>();

    public Ambiente(AmbienteTipo tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return tipo.getNome();  
    }

    public ArrayList<Long> getDispositivos() {
        return dispositivos;
    }

    public void adicionarDispositivo(Long idDispositivo) {
        dispositivos.add(idDispositivo);
    }

    public AmbienteTipo tipo() {
        return tipo;
    }
}
