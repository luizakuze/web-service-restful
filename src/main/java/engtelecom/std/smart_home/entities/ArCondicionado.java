package engtelecom.std.smart_home.entities;

import java.util.Map;

/**
 * Classe para representar um ar-condicionado em uma casa inteligente.
 * Tem atributos para temperatura, velocidade do ventilador,
 * ativar/desativar a auto limpeza e ativar/desativar o modo silencioso.
 */
public class ArCondicionado extends Dispositivo {
    private int temperatura;
    private int velocidade; // Opções: 0 - fraco, 1 - médio, 2 - forte
    private boolean autoLimpeza;
    private boolean modoSilencioso;

    public ArCondicionado() {
        super("ar-condicionado");
        this.temperatura = 24; // padrão
        this.velocidade = 0; // fraco
        this.autoLimpeza = false; // desativado
        this.modoSilencioso = false; // desativado
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getVelocidade() {
        return velocidade;
    }

    /**
     * Define a velocidade do ventilador do ar-condicionado.
     * Caso o valor fornecido esteja fora do intervalo permitido (0-2),
     * ele será ajustado para o valor mais próximo dentro do intervalo.
     * 
     * @param velocidade 0 - fraco, 1 - médio, 2 - forte
     */
    public void setVelocidade(int velocidade) {
        if (velocidade < 0)
            velocidade = 0;
        if (velocidade > 2)
            velocidade = 2;

        this.velocidade = velocidade;
    }

    public boolean isAutoLimpeza() {
        return autoLimpeza;
    }

    public void setAutoLimpeza(boolean autoLimpeza) {
        this.autoLimpeza = autoLimpeza;
    }

    public boolean isModoSilencioso() {
        return modoSilencioso;
    }

    public void setModoSilencioso(boolean modoSilencioso) {
        this.modoSilencioso = modoSilencioso;
    }

    @Override
    protected boolean atualizarPropriedades(Map<String, Object> body) {
        boolean atualizado = false;

        if (body.containsKey("temperatura")) {
            int temp = Integer.parseInt(body.get("temperatura").toString());
            setTemperatura(temp);
            atualizado = true;
        }

        if (body.containsKey("velocidade")) {
            int vel = Integer.parseInt(body.get("velocidade").toString());
            setVelocidade(vel);
            atualizado = true;
        }

        if (body.containsKey("autoLimpeza")) {
            boolean v = Boolean.parseBoolean(body.get("autoLimpeza").toString());
            setAutoLimpeza(v);
            atualizado = true;
        }

        if (body.containsKey("modoSilencioso")) {
            boolean v = Boolean.parseBoolean(body.get("modoSilencioso").toString());
            setModoSilencioso(v);
            atualizado = true;
        }

        return atualizado;
    }

}
