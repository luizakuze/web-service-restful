package engtelecom.std.smart_home.entities;

import java.util.Map;

/**
 * Classe para representar um dispositivo genérico em um sistema de casa
 * inteligente.
 */
public abstract class Dispositivo {
    private boolean ligado;
    private long id;
    private String tipo;

    public Dispositivo(String tipo) {
        this.ligado = false;
        this.tipo = tipo;
    }

    public void trocarEstado() {
        this.ligado = !this.ligado;
    }

    public void ligar() {
        this.ligado = true;
    }

    public void desligar() {
        this.ligado = false;
    }

    public boolean getLigado() {
        return this.ligado;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public String getTipo() {
        if (tipo == null) {
            return null;
        }
        return tipo.toLowerCase();
    }

    /**
     * Atualiza o estado do dispositivo a partir dos dados informados no mapa.
     *
     * O método analisa o conteúdo do mapa recebido e aplica apenas os campos
     * reconhecidos pelo dispositivo.
     * Caso pelo menos um campo válido seja identificado, o dispositivo é retornado,
     * mesmo que os valores aplicados
     * sejam iguais aos atuais.
     *
     * A atualização é considerada inválida quando o mapa é nulo, está vazio ou não
     * contém nenhuma chave reconhecida.
     *
     * @param body mapa contendo os dados para atualização do dispositivo
     * @return o próprio dispositivo após a atualização, ou null se os dados
     *         informados forem inválidos
     */
    public Dispositivo atualizar(Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            return null;
        }

        boolean reconheceuAlgumCampo = false;

        // "ligado"
        if (body.containsKey("ligado")) {
            atualizarEstado(body); // processa ações de ligar/desligar
            reconheceuAlgumCampo = true;
        }

        // campos específicos
        if (atualizarPropriedades(body)) {
            reconheceuAlgumCampo = true;
        }

        if (!reconheceuAlgumCampo) {
            return null;
        }

        return this;
    }

    /**
     * Atualiza apenas o estado ligado/desligado, o qual é geral a todos os dispositivos.
     *
     * @param body mapa contendo os dados para atualização do dispositivo
     * @return true se a chave "ligado" estiver presente, false caso contrário
     */
    public boolean atualizarEstado(Map<String, Object> body) {
        // Verifica se há o campo "ligado"
        if (body == null || !body.containsKey("ligado")) {
            return false;
        }

        // Atualiza o estado ligado/desligado 
        boolean novoEstado = Boolean.parseBoolean(body.get("ligado").toString());
        if (novoEstado != this.ligado) {
            if (novoEstado) {
                ligar();
            } else {
                desligar();
            }
        }

        return true;
    }

    /**
     * Atualiza os atributos específicos do dispositivo.
     *
     * @param body mapa contendo os dados para atualização do dispositivo
     * @return true se pelo menos 1 chave específica reconhecida foi processada
     */
    protected abstract boolean atualizarPropriedades(Map<String, Object> body);
}
