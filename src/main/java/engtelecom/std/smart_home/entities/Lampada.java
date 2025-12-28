package engtelecom.std.smart_home.entities;

import java.util.Map;

/**
 * Classe para representar uma lâmpada em uma casa inteligente.
 * Possui atributos para cor e intensidade da luz.
 *
 * A cor é representada por uma enum LampadaCor.
 *
 * A intensidade varia de 0 a 100, caso um valor fora desse intervalo seja
 * fornecido, ele será ajustado para o valor mais próximo dentro do intervalo.
 */
public class Lampada extends Dispositivo {

    private LampadaCor cor;
    private int intensidade;

    public Lampada(String cor, int intensidade) {
        super("lampada");
        this.cor = LampadaCor.getByNome(cor);
        setIntensidade(intensidade);
    }

    public String getCor() {
        if (cor == null) {
            return null;
        }
        return cor.getNome().toLowerCase();
    }

    public void setCor(String cor) {
        this.cor = LampadaCor.getByNome(cor);
    }

    public int getIntensidade() {
        return this.intensidade;
    }

    public void setIntensidade(int intensidade) {
        if (intensidade < 0)
            intensidade = 0;
        if (intensidade > 100)
            intensidade = 100;
        this.intensidade = intensidade;
    }

    @Override
    protected boolean atualizarPropriedades(Map<String, Object> body) {
        boolean atualizado = false;

        if (body.containsKey("cor")) {
            setCor(body.get("cor").toString());
            atualizado = true;
        }

        if (body.containsKey("intensidade")) {
            int valor = Integer.parseInt(body.get("intensidade").toString());
            setIntensidade(valor);
            atualizado = true;
        }

        return atualizado;
    }
}
