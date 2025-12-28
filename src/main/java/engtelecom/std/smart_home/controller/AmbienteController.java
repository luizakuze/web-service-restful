package engtelecom.std.smart_home.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import engtelecom.std.smart_home.entities.Ambiente;
import engtelecom.std.smart_home.entities.Dispositivo;
import engtelecom.std.smart_home.exceptions.AmbienteNaoEncontradoException;
import engtelecom.std.smart_home.exceptions.DispositivoNaoEncontradoException;
import engtelecom.std.smart_home.service.AmbienteService;
import engtelecom.std.smart_home.service.DispositivoService;

@RestController
@RequestMapping({ "/ambientes", "/ambientes/" })
public class AmbienteController {

    @Autowired
    private AmbienteService ambienteService;

    @Autowired
    private DispositivoService dispositivoService;

    /**
     * Listar todos os ambientes, bem como os dispositivos associados a cada um
     *
     * @return lista de ambientes com seus dispositivos
     * 
     */
    @GetMapping
    public ArrayList<Map<String, Object>> listarAmbientes() {
        // Lista que irá conter a resposta final da API
        ArrayList<Map<String, Object>> resposta = new ArrayList<>();

        // Obtém todos os ambientes cadastrados, associados aos seus respectivos IDs
        Map<Long, Ambiente> ambientes = ambienteService.buscarTodosComId();

        // Para cada ambiente, monta a representação do ambiente e adiciona à resposta
        for (Map.Entry<Long, Ambiente> entry : ambientes.entrySet()) {
            Ambiente ambiente = entry.getValue();
            resposta.add(montarResposta(ambiente));
        }

        return resposta;
    }

    /**
     * Obter um ambiente pelo id
     *
     * @param ambienteId o ID do ambiente
     * @return o ambiente encontrado
     * @throws AmbienteNaoEncontradoException se o ambiente não for encontrado
     */
    @GetMapping("/{ambienteId}")
    public Map<String, Object> obterAmbiente(@PathVariable long ambienteId) {
        Ambiente a = ambienteService.buscarPorId(ambienteId);
        if (a != null) {
            return montarResposta(a);
        }
        throw new AmbienteNaoEncontradoException(ambienteId);
    }

    /**
     * Atualizar um ambiente existente
     * 
     * @param ambienteId ID do ambiente a ser atualizado (informado na URI)
     * @param body       corpo da requisição contendo os novos dados do ambiente
     * @return representação do ambiente atualizado
     * @throws AmbienteNaoEncontradoException se o ambiente informado não existir
     * @throws IllegalArgumentException       se o corpo da requisição for inválido
     */
    @PutMapping("/{ambienteId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> atualizarAmbiente(@PathVariable long ambienteId, @RequestBody Map<String, Object> body) {
        Ambiente ambiente = ambienteService.atualizarAmbiente(ambienteId, body);
        if (ambiente == null) {
            throw new AmbienteNaoEncontradoException(ambienteId);
        }
        return montarResposta(ambiente);
    }

    /**
     * Cria a representação de um ambiente para resposta da API.
     *
     * O ambiente é convertido para uma estrutura contendo apenas o tipo do ambiente 
     * e a lista completa de dispositivos atualmente associados a ele.
     *
     * @param ambiente ambiente de domínio a ser representado
     * @return mapa contendo os dados do ambiente no formato da resposta HTTP
     */
    private Map<String, Object> montarResposta(Ambiente ambiente) {
        Map<String, Object> obj = new HashMap<>();

        // Tipo do ambiente 
        obj.put("tipo", ambiente.tipo().getNome());

        // Lista de dispositivos associados ao ambiente
        ArrayList<Dispositivo> dispositivos = new ArrayList<>();

        for (Long dispositivoId : ambiente.getDispositivos()) {
            Dispositivo d = dispositivoService.buscarPorId(dispositivoId);
            if (d != null) {
                dispositivos.add(d);
            }
        }

        obj.put("dispositivos", dispositivos);
        return obj;
    }

}

@ControllerAdvice
class AmbienteNaoEncontradoAdvice {

    @ResponseBody
    @ExceptionHandler(AmbienteNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String ambienteNaoEncontrado(AmbienteNaoEncontradoException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DispositivoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String dispositivoNaoEncontrado(DispositivoNaoEncontradoException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String internalError(IllegalStateException ex) {
        return ex.getMessage();
    }
}
