package engtelecom.std.smart_home.entities;

import java.util.ArrayList;
import java.util.Map;

/**
 * Classe para representar um cenário na casa inteligente.
 * Um cenário possui um ID, um nome (exemplo: "Final de Semana", "Acordar", ...)
 * e uma lista de ações a serem executadas.
 * 
 * As ações são representadas uma lista de chaves-valor (Map<String, Object>),
 * onde cada mapa contém os parâmetros para uma ação específica em um dispositivo.
 * 
 */
public class Cenario {

    /**
     * A lista de ações que compõem a rotina do cenário.
     * Exemplo: uma ação pode ser {"dispositivoId": 1, "acao": "ligar"}, outra pode
     * ser {"dispositivoId": 2, "acao": "definirTemperatura", "valor": 10}
     */
    private ArrayList<Map<String, Object>> rotina;
    
    private long id;
    private String nome;

    public Cenario() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Map<String, Object>> getRotina() {
        return rotina;
    }

    public void setRotina(ArrayList<Map<String, Object>> acoes) {
        this.rotina = acoes;
    }
}
