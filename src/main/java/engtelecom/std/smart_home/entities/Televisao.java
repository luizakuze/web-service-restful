package engtelecom.std.smart_home.entities;

import java.util.Map;

import engtelecom.std.smart_home.exceptions.TelevisaoEventosExclusivosException;

/**
 * Classe para representar uma televisão em uma casa inteligente.
 * Possui atributos para o aplicativo atual, canal atual e volume.
 *
 * A televisão pode alternar entre aplicativos e canais, garantindo que
 * apenas um esteja ativo por vez.
 */
public class Televisao extends Dispositivo {
    private TelevisaoApp appAtual; // aplicativo atualmente em uso
    private TelevisaoCanal canalAtual; // canal atualmente em uso
    private int volume; // 0 a 100

    public Televisao() {
        super("televisao");
        this.canalAtual = TelevisaoCanal.GLOBO; // canal padrão
        this.appAtual = null;
        this.volume = 50;
    }

    /**
     * Define o aplicativo atual da televisão.
     * Desliga o canal atual ao definir um aplicativo.
     *
     * @param app o aplicativo a ser definido como atual
     */
    public void setAppAtual(TelevisaoApp app) {
        this.appAtual = app;
        this.canalAtual = null;
    }

    public String getAppAtual() {
        if (appAtual == null) {
            return null;
        }
        return appAtual.getNome().toLowerCase();
    }

    public String getCanalAtual() {
        if (canalAtual == null) {
            return null;
        }
        return canalAtual.getNome().toLowerCase();
    }

    /**
     * Define o canal atual da televisão.
     * Desliga o aplicativo atual ao definir um canal.
     *
     * @param canal o canal a ser definido como atual
     */
    public void setCanalAtual(TelevisaoCanal canal) {
        this.canalAtual = canal;
        this.appAtual = null;
    }

    public int getVolume() {
        return this.volume;
    }

    /**
     * Define o volume da televisão.
     * O volume é limitado entre 0 e 100.
     *
     * @param volume o volume a ser definido
     */
    public void setVolume(int volume) {
        if (volume < 0)
            volume = 0;
        if (volume > 100)
            volume = 100;
        this.volume = volume;
    }

    @Override
    protected boolean atualizarPropriedades(Map<String, Object> body) {
        boolean atualizado = false;

        boolean temApp = body.containsKey("appAtual") && body.get("appAtual") != null;
        boolean temCanal = body.containsKey("canalAtual") && body.get("canalAtual") != null;

        if (temApp && temCanal) {
            throw new TelevisaoEventosExclusivosException();
        }

        if (temApp) {
            String valor = body.get("appAtual").toString().trim();
            if (!valor.isEmpty()) {
                TelevisaoApp appEscolhido = TelevisaoApp.getByNome(valor);
                setAppAtual(appEscolhido);
                atualizado = true;
            }
        }

        if (temCanal) {
            String valor = body.get("canalAtual").toString().trim();
            if (!valor.isEmpty()) {
                TelevisaoCanal canalEscolhido = TelevisaoCanal.getByNome(valor);
                setCanalAtual(canalEscolhido);
                atualizado = true;
            }
        }

        if (body.containsKey("volume") && body.get("volume") != null) {
            int valor = Integer.parseInt(body.get("volume").toString());
            setVolume(valor);
            atualizado = true;
        }

        return atualizado;
    }
}
