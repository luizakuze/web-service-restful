package engtelecom.std.smart_home.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import engtelecom.std.smart_home.entities.Ambiente;
import engtelecom.std.smart_home.entities.AmbienteTipo;
import engtelecom.std.smart_home.entities.Dispositivo;
import engtelecom.std.smart_home.exceptions.AmbienteAtualizacaoInvalidaException;
import engtelecom.std.smart_home.exceptions.DispositivoNaoEncontradoException;

/**
 * Serviço responsável por gerenciar ambientes.
 *
 * Mantém os ambientes em memória e permite consultar, criar, remover e associar
 * dispositivos a cada ambiente.
 */
@Component
public class AmbienteService {

    private final Map<Long, Ambiente> ambientes = new HashMap<>();
    private static final AtomicLong contador = new AtomicLong();
    private final DispositivoService dispositivoService;

    /**
     * Cria o serviço e inicializa ambientes padrão para todos os tipos definidos em
     * {@link AmbienteTipo}.
     *
     * @param dispositivoService serviço usado para validar e consultar dispositivos
     *                           existentes
     */
    public AmbienteService(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;

        // cria um ambiente inicial para cada tipo disponível
        for (AmbienteTipo tipo : AmbienteTipo.values()) {
            long id = contador.incrementAndGet();
            ambientes.put(id, new Ambiente(tipo));
        }
    }

    /**
     * Instala um dispositivo em um ambiente específico.
     */
    public boolean instalarDispositivoNoAmbiente(Long dispositivoId, long ambienteId) {
        Ambiente a = ambientes.get(ambienteId);
        if (a == null) {
            return false;
        }

        a.adicionarDispositivo(dispositivoId);
        return true;
    }

    /**
     * Busca um ambiente pelo seu ID.
     */
    public Ambiente buscarPorId(Long id) {
        return ambientes.get(id);
    }

    /**
     * Busca os dispositivos associados a um determinado tipo de ambiente.
     */
    public ArrayList<Dispositivo> buscarPorAmbiente(AmbienteTipo tipo) {
        ArrayList<Dispositivo> dispositivosNoAmbiente = new ArrayList<>();

        for (Ambiente a : ambientes.values()) {
            if (a.tipo() == tipo) {
                for (Long dispositivoId : a.getDispositivos()) {
                    Dispositivo d = dispositivoService.buscarPorId(dispositivoId);
                    if (d != null) {
                        dispositivosNoAmbiente.add(d);
                    }
                }
            }
        }

        return dispositivosNoAmbiente;
    }

    /**
     * Busca todos os ambientes cadastrados.
     */
    public ArrayList<Ambiente> buscarTodos() {
        return new ArrayList<>(ambientes.values());
    }

    /**
     * Busca todos os ambientes cadastrados com seus IDs.
     */
    public Map<Long, Ambiente> buscarTodosComId() {
        return new HashMap<>(ambientes);
    }

    /**
     * Remove um ambiente pelo seu ID.
     */
    public boolean removerAmbiente(long id) {
        return ambientes.remove(id) != null;
    }

    /**
     * Remove um dispositivo de um ambiente específico.
     */
    public boolean removerDispositivoDoAmbiente(Long dispositivoId, long ambienteId) {
        Ambiente a = ambientes.get(ambienteId);
        if (a == null) {
            return false;
        }
        return a.getDispositivos().remove(dispositivoId);
    }

    /**
     * Cria um novo ambiente de um determinado tipo.
     */
    public long criarAmbientePorTipo(String nomeTipo) {
        AmbienteTipo tipo = AmbienteTipo.getByNome(nomeTipo);

        long id = contador.incrementAndGet();
        ambientes.put(id, new Ambiente(tipo));
        return id;
    }

    /**
     * Modifica a lista de dispositivos atual do ambiente.
     * 
     * @param ambienteId ID do ambiente a ser atualizado.
     * @param body       corpo da requisição com os novos dados do ambiente.
     * @return o ambiente atualizado, ou null se o ambiente não existir.
     * @throws AmbienteAtualizacaoInvalidaException se o corpo da requisição for
     * 
     */
    public Ambiente atualizarAmbiente(long ambienteId, Map<String, Object> body) {
        Ambiente ambiente = ambientes.get(ambienteId);
        if (ambiente == null) {
            return null;
        }

        // Valida o corpo da requisição antes de aplicar as mudanças
        if (!verificarAtualizacao(ambienteId, ambiente, body)) {
            throw new AmbienteAtualizacaoInvalidaException("corpo da requisição é inválido");
        }

        List<Long> ids = obterIdentificadoresDeDispositivos(body.get("dispositivos"));
        ambiente.getDispositivos().clear();
        ambiente.getDispositivos().addAll(ids);

        return ambiente;
    }

    /**
     * Verifica se o corpoda requisição tem o formato esperado para atualização
     * completa do
     * ambiente.
     * 
     * @param ambienteId ID do ambiente a ser atualizado.
     * @param ambiente   ambiente atual.
     * @param body       corpo da requisição.
     * 
     * @return true se o corpo for válido para atualização; caso contrário, lança
     *         uma
     *         exceção.
     * 
     * @throws AmbienteAtualizacaoInvalidaException se o corpo da requisção estiver
     *                                              inválido.
     * 
     */
    private boolean verificarAtualizacao(long ambienteId, Ambiente ambiente, Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            throw new AmbienteAtualizacaoInvalidaException("corpo da requisão está ausente ou é vazio");
        }

        // ID opcional
        if (body.containsKey("id")) {
            Long idBody = converterParaLong(body.get("id"));
            if (idBody == null) {
                throw new AmbienteAtualizacaoInvalidaException("campo 'id' não é numérico");
            }
            if (idBody != ambienteId) {
                throw new AmbienteAtualizacaoInvalidaException("campo 'id' não confere com a URI");
            }
        }

        // Campos obrigatórios
        if (!body.containsKey("tipo") || !body.containsKey("dispositivos")) {
            throw new AmbienteAtualizacaoInvalidaException("campos obrigatórios: tipo e dispositivos");
        }

        // Tipo do ambiente não deve mudar
        String tipoBody = body.get("tipo").toString().toLowerCase();
        String tipoAtual = ambiente.tipo().getNome().toLowerCase();
        if (!tipoAtual.equals(tipoBody)) {
            throw new AmbienteAtualizacaoInvalidaException("tipo do ambiente não pode ser alterado");
        }

        // Nnão aceita campos adicionais
        for (String key : body.keySet()) {
            if (!key.equals("id") && !key.equals("tipo") && !key.equals("dispositivos")) {
                throw new AmbienteAtualizacaoInvalidaException("campo não permitido");
            }
        }

        return true;
    }

    /**
     * Extrai e valida os IDs de dispositivos.
     * 
     * @param dispositivosObj objeto genérico representando a lista de dispositivos.
     * @return lista de IDs de dispositivos.
     * @throws AmbienteAtualizacaoInvalidaException se o formato for inválido ou se
     *                                              algum dispositivo não existir.
     * 
     */
    private List<Long> obterIdentificadoresDeDispositivos(Object dispositivosObj) {
        // o campo "dispositivos" já foi validado como lista
        @SuppressWarnings("unchecked")
        List<Object> lista = (List<Object>) dispositivosObj;

        ArrayList<Long> identificadores = new ArrayList<>();

        // percorre cada item da lista enviada no corpo da requisição
        for (Object item : lista) {

            // cada item deve ser um objeto (mapa)
            if (!(item instanceof Map)) {
                throw new AmbienteAtualizacaoInvalidaException("item inválido em dispositivos");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> dispMap = (Map<String, Object>) item;

            // obtém e converte o campo "id"
            Long identificador = converterParaLong(dispMap.get("id"));
            if (identificador == null) {
                throw new AmbienteAtualizacaoInvalidaException("dispositivo sem id válido");
            }

            // verifica se o dispositivo realmente existe
            if (dispositivoService.buscarPorId(identificador) == null) {
                throw new DispositivoNaoEncontradoException(identificador);
            }

            // adiciona o id válido à lista finalbod
            identificadores.add(identificador);
        }

        return identificadores;
    }

    /**
     * Converte um valor genérico para Long.
     * 
     * @param valor valor genérico.
     * @return valor convertido para Long, ou null se a conversão falhar.
     */
    private Long converterParaLong(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return Long.parseLong(valor.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
