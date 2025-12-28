package engtelecom.std.smart_home.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import engtelecom.std.smart_home.entities.ArCondicionado;
import engtelecom.std.smart_home.entities.Dispositivo;
import engtelecom.std.smart_home.entities.Lampada;
import engtelecom.std.smart_home.entities.Televisao;
import engtelecom.std.smart_home.exceptions.DispositivoNaoEncontradoException;

/**
 * Serviço responsável por gerenciar os dispositivos cadastrados no sistema.
 * O cadastro é mantido em memória.
 *
 * Mantém os dispositivos em memória e oferece operações de cadastro, consulta e
 * atualização. 
 */
@Component
public class DispositivoService {

    private final ArrayList<Dispositivo> dispositivos = new ArrayList<>();
    private static final AtomicLong contador = new AtomicLong();

    /**
     * Inicializa o serviço com alguns dispositivos padrão.
     */
    public DispositivoService() {
        cadastrarDispositivo(new Lampada("branca", 50));
        cadastrarDispositivo(new Lampada("branca", 75));
        cadastrarDispositivo(new Lampada("amarela", 75));
        cadastrarDispositivo(new Lampada("amarela", 75));
        cadastrarDispositivo(new Lampada("amarela", 75));
        cadastrarDispositivo(new ArCondicionado());
        cadastrarDispositivo(new ArCondicionado());
        cadastrarDispositivo(new Televisao());
        cadastrarDispositivo(new Televisao());
    }

    /**
     * Cadastra um novo dispositivo no serviço.
     *
     * O identificador do dispositivo é gerado automaticamente e atribuído ao objeto.
     *
     * @param dispositivo dispositivo a ser cadastrado
     * @return o dispositivo cadastrado, já contendo o id gerado
     */
    public Dispositivo cadastrarDispositivo(Dispositivo dispositivo) {
        dispositivo.setId(contador.incrementAndGet());
        dispositivos.add(dispositivo);
        return dispositivo;
    }

    /**
     * Retorna todos os dispositivos cadastrados.
     *
     * @return lista de dispositivos
     */
    public ArrayList<Dispositivo> buscarTodos() {
        return dispositivos;
    }

    /**
     * Busca um dispositivo pelo seu identificador.
     *
     * @param id identificador do dispositivo
     * @return o dispositivo encontrado, ou null se não existir
     */
    public Dispositivo buscarPorId(Long id) {
        return this.dispositivos.stream()
                .filter(d -> Long.valueOf(d.getId()).equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Modifica todos os campos do dispositivo existente.
     * 
     * @param id   identificador do dispositivo (informado na URI)
     * @param body mapa com os dados completos do dispositivo
     * @return o dispositivo após a substituição
     * 
     * @throws DispositivoNaoEncontradoException se o dispositivo não existir
     * @throws IllegalArgumentException se o mapa estiver inválido para substituição completa
     */
    public Dispositivo substituirDispositivo(Long id, Map<String, Object> body) {
        Dispositivo existente = buscarPorId(id);
        if (existente == null) {
            throw new DispositivoNaoEncontradoException(id);
        }

        Map<String, Object> payload = prepararDadosParaSubstituicao(existente, id, body);

        // Valida primeiro
        validarSubstituicaoCompleta(existente, payload);

        // Depois aplica a atualização
        Dispositivo atualizado = atualizarDispositivo(id, payload);

        if (atualizado != null) {
            return atualizado;
        }

        return existente;
    }

    /**
     * Atualiza parcialmente um dispositivo existente.
     * 
     * @param id   identificador do dispositivo
     * @param body mapa com os campos a atualizar
     * @return o dispositivo atualizado, ou null se a atualização não puder ser aplicada
     */
    public Dispositivo atualizarDispositivo(Long id, Map<String, Object> body) {
        Dispositivo d = buscarPorId(id);
        if (d == null) {
            return null;
        }
        if (body == null || body.isEmpty()) {
            return null;
        }

        return d.atualizar(body);
    }

    /**
     * Normaliza o mapa recebido para substituição completa (PUT).
     *
     * Se os campos "ID" e "tipo" estiverem presentes, eles são validados
     * e removidos do mapa retornado.
     *
     * @param existente dispositivo existente (usado para validar o tipo real)
     * @param idUri     identificador informado na URI
     * @param body      mapa original recebido
     * @return mapa normalizado (sem id e tipo)
     * @throws IllegalArgumentException se o mapa for nulo ou se id/tipo forem inválidos
     */
    private Map<String, Object> prepararDadosParaSubstituicao(Dispositivo existente, Long idUri, Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("Mapa de informações incompleto.");
        }

        Map<String, Object> payload = new HashMap<>(body);

        // id opcional
        if (payload.containsKey("id")) {
            Object idObj = payload.get("id");
            if (idObj == null) {
                throw new IllegalArgumentException("Campo 'id' não pode ser null.");
            }
            long idBody;
            try {
                idBody = Long.parseLong(idObj.toString());
            } catch (Exception e) {
                throw new IllegalArgumentException("Campo 'id' deve ser numérico.");
            }
            if (idBody != idUri) {
                throw new IllegalArgumentException("Campo 'id' no mapa deve bater com o id da URI.");
            }
            payload.remove("id");
        }

        // tipo opcional
        if (payload.containsKey("tipo")) {
            Object tipoObj = payload.get("tipo");
            if (tipoObj == null) {
                throw new IllegalArgumentException("Campo 'tipo' não pode ser null.");
            }
            String tipoBody = tipoObj.toString().trim().toLowerCase();
            String tipoReal = existente.getTipo().trim().toLowerCase();
            if (!tipoReal.equals(tipoBody)) {
                throw new IllegalArgumentException("Campo 'tipo' inválido: esperado '" + tipoReal + "'.");
            }
            payload.remove("tipo");
        }

        return payload;
    }

    /**
     * Valida se o mapa atende os requisitos de substituição completa para o tipo real do dispositivo.
     *
     * @param existente dispositivo existente (define qual conjunto de campos é esperado)
     * @param body      mapa normalizado contendo os campos do dispositivo
     * @throws IllegalArgumentException se houver campos ausentes, campos extras ou valores inválidos
     */
    private void validarSubstituicaoCompleta(Dispositivo existente, Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("PUT exige mapa completo.");
        }

        if (existente instanceof Televisao) {
            validarTelevisaoCompleta(body);
            return;
        }

        Set<String> esperados = extrairJson(existente);

        if (!body.keySet().equals(esperados)) {
            Set<String> faltando = new HashSet<>(esperados);
            faltando.removeAll(body.keySet());

            Set<String> extras = new HashSet<>(body.keySet());
            extras.removeAll(esperados);

            if (!faltando.isEmpty() && !extras.isEmpty()) {
                throw new IllegalArgumentException(
                        "Campos ausentes: " + faltando + ". Campos não permitidos: " + extras + ".");
            }
            if (!faltando.isEmpty()) {
                throw new IllegalArgumentException("Campos ausentes: " + faltando + ".");
            }
            throw new IllegalArgumentException("Campos não permitidos: " + extras + ".");
        }

        validarBoolean(body, "ligado");

        if (existente instanceof Lampada) {
            validarInt(body, "intensidade");
            Object cor = body.get("cor");
            if (cor == null || cor.toString().trim().isEmpty()) {
                throw new IllegalArgumentException("Campo 'cor' não pode ser vazio.");
            }
            return;
        }

        if (existente instanceof ArCondicionado) {
            validarInt(body, "temperatura");
            validarInt(body, "velocidade");
            validarBoolean(body, "autoLimpeza");
            validarBoolean(body, "modoSilencioso");
        }
    }

    /**
     * Valida os campos exigidos para substituição completa de uma Televisão.
     *
     * A televisão tem ma característica adicional de os atributos "appAtual" e "canalAtual" serem mutuamente exclusivos.
     * Não posso acessar um aplicativo de streaming e um canal de TV ao mesmo tempo.
     *
     * @param body mapa normalizado da televisão
     * @throws IllegalArgumentException se o mapa estiver inválido
     */
    private void validarTelevisaoCompleta(Map<String, Object> body) {
        Set<String> esperadosTv = Set.of("ligado", "volume", "appAtual", "canalAtual");

        if (!body.keySet().equals(esperadosTv)) {
            throw new IllegalArgumentException("Televisão: envie exatamente {ligado, volume, appAtual, canalAtual}.");
        }

        Object appV = body.get("appAtual");
        Object canalV = body.get("canalAtual");

        boolean temApp = (appV != null && !appV.toString().trim().isEmpty());
        boolean temCanal = (canalV != null && !canalV.toString().trim().isEmpty());

        if (temApp && temCanal) {
            throw new IllegalArgumentException("Televisão: appAtual e canalAtual são mutuamente exclusivos.");
        }

        validarBoolean(body, "ligado");
        validarInt(body, "volume");
    }

    /**
     * Retorna o conjunto de campos esperados no JSON para cada tipo de dispositivo.
     *
     * @param d dispositivo existente (usado para identificar o tipo real)
     * @return conjunto de chaves esperadas
     */
    private Set<String> extrairJson(Dispositivo d) {
        if (d instanceof Lampada) {
            return Set.of("ligado", "cor", "intensidade");
        }
        if (d instanceof ArCondicionado) {
            return Set.of("ligado", "temperatura", "velocidade", "autoLimpeza", "modoSilencioso");
        }
        if (d instanceof Televisao) {
            return Set.of("ligado", "volume", "appAtual", "canalAtual");
        }
        return Set.of("ligado");
    }

    /**
     * Valida se um campo do mapa é um inteiro.
     *
     * @param body  mapa de dados
     * @param campo nome do campo
     * @throws IllegalArgumentException se o campo estiver ausente, nulo ou não for inteiro
     */
    private void validarInt(Map<String, Object> body, String campo) {
        Object v = body.get(campo);
        if (v == null) {
            throw new IllegalArgumentException("Campo '" + campo + "' não pode ser null.");
        }
        try {
            Integer.parseInt(v.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Campo '" + campo + "' deve ser inteiro.");
        }
    }

    /**
     * Valida se um campo do mapa é um boolean no formato {@code true}/{@code false}.
     *
     * @param body  mapa de dados
     * @param campo nome do campo
     * @throws IllegalArgumentException se o campo estiver ausente, nulo ou não for boolean válido
     */
    private void validarBoolean(Map<String, Object> body, String campo) {
        Object v = body.get(campo);
        if (v == null) {
            throw new IllegalArgumentException("Campo '" + campo + "' não pode ser null.");
        }
        String s = v.toString().toLowerCase();
        if (!s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Campo '" + campo + "' deve ser boolean (true/false).");
        }
    }
}
