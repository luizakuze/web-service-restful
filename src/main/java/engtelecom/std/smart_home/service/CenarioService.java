package engtelecom.std.smart_home.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
import engtelecom.std.smart_home.entities.Cenario;
import engtelecom.std.smart_home.exceptions.DispositivoNaoEncontradoException;
import engtelecom.std.smart_home.exceptions.RotinaInvalidaException;
import engtelecom.std.smart_home.exceptions.RotinaNaoEncontradaException;

/**
 * Serviço para gerenciar cenários em uma casa inteligente.
 */
@Component
public class CenarioService {

    /**
     * Atributos:
     * - Uma lista para armazenar os cenários cadastrados.
     * - Um contador atômico para gerar IDs únicos para os cenários.
     * - Uma referência ao DispositivoService para aplicar ações nos dispositivos.
     */
    private final ArrayList<Cenario> cenarios = new ArrayList<>();
    private static final AtomicLong contador = new AtomicLong();
    private final DispositivoService dispositivoService;

    public CenarioService(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    /**
     * Cadastra um novo cenário na lista.
     * 
     * @param cenario o cenário a ser cadastrado.
     * @return o cenário cadastrado com o ID atribuído.
     */
    public Cenario cadastrarCenario(Cenario cenario) {
        cenario.setId(contador.incrementAndGet());
        cenarios.add(cenario);
        return cenario;
    }

    /**
     * Busca todos os cenários cadastrados.
     * 
     * @return a lista de cenários.
     */
    public ArrayList<Cenario> buscarTodos() {
        return cenarios;
    }

    /**
     * Busca um cenário pelo seu ID.
     * 
     * @param id o ID do cenário.
     * @return o cenário encontrado ou null se não existir.
     */
    public Cenario buscarPorId(long id) {
        return cenarios.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Remove um cenário pelo seu ID.
     * 
     * @param id o ID do cenário a ser removido.
     * @return true se o cenário foi removido com sucesso, false caso contrário.
     */
    public Cenario removerCenario(long id) {
        Cenario c = buscarPorId(id);
        if (c == null) {
            return null;
        }
        cenarios.remove(c);
        return c;
    }

    /**
     * Aplica um cenário, executando sua rotina de ações.
     * 
     * @param id o ID do cenário a ser aplicado.
     * @return true se a rotina do cenário foi aplicada, false caso o cenário não
     *         exista.
     */
    public List<Map<String, Object>> aplicarCenario(long id) {
        Cenario c = buscarPorId(id);
        if (c == null) {
            return null;
        }

        if (c.getRotina() == null || c.getRotina().isEmpty()) {
            throw new RotinaNaoEncontradaException();
        }

        processarRotina(c.getRotina());

        // retorna exatamente o que foi aplicado
        return c.getRotina();
    }

    /**
     * Atualiza um cenário existente pelo seu ID
     * 
     * @param id                o ID do cenário a ser atualizado.
     * @param cenarioAtualizado os novos dados do cenário.
     * @return o cenário atualizado ou null se não existir.
     */
    public Cenario atualizarCenario(long id, Cenario cenarioAtualizado) {
        Cenario existente = buscarPorId(id);
        if (existente == null) {
            return null;
        }

        existente.setNome(cenarioAtualizado.getNome());
        existente.setRotina(cenarioAtualizado.getRotina());

        return existente;
    }

    /**
     * Processa a rotina de ações de um cenário.
     *
     * @param rotina lista de ações a serem executadas
     * 
     * @throws RotinaNaoEncontradaException se a rotina for nula ou vazia
     * @throws RotinaInvalidaException       se alguma ação na rotina for inválida
     * @throws DispositivoNaoEncontradoException se algum dispositivo na rotina não for encontrado
     * 
     */
    private void processarRotina(List<Map<String, Object>> rotina) {

        if (rotina == null || rotina.isEmpty()) {
            throw new RotinaNaoEncontradaException();
        }

        int numeroAcao = 1;

        for (Map<String, Object> acao : rotina) {

            // Mensagem padrão usada em qualquer erro da ação
            String mensagem = "Rotina inválida na ação número " + numeroAcao + ".";

            if (acao == null) {
                throw new RotinaInvalidaException(mensagem);
            }

            // Toda ação tem um dispositivo alvo
            Object identificador = acao.get("dispositivoId");
            if (identificador == null) {
                throw new RotinaInvalidaException(mensagem);
            }

            long dispositivoId;
            try {
                dispositivoId = Long.parseLong(identificador.toString());
            } catch (NumberFormatException e) {
                throw new RotinaInvalidaException(mensagem);
            }

            if (dispositivoService.buscarPorId(dispositivoId) == null) {
                throw new DispositivoNaoEncontradoException(dispositivoId);
            }

            // Parâmetros da ação (tudo exceto o id do dispositivo)
            Map<String, Object> parametros = new HashMap<>(acao);
            parametros.remove("dispositivoId");

            // deve haver ao menos uma ação válida para aplicar ao dispositivo
            if (parametros.isEmpty()
                    || dispositivoService.atualizarDispositivo(dispositivoId, parametros) == null) {
                throw new RotinaInvalidaException(mensagem);
            }

            numeroAcao++;
        }
    }

}
