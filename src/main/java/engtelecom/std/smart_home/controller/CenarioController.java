package engtelecom.std.smart_home.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import engtelecom.std.smart_home.entities.Cenario;
import engtelecom.std.smart_home.exceptions.CenarioNaoEncontradoException;
import engtelecom.std.smart_home.exceptions.DispositivoNaoEncontradoException;
import engtelecom.std.smart_home.exceptions.RotinaInvalidaException;
import engtelecom.std.smart_home.exceptions.RotinaNaoEncontradaException;
import engtelecom.std.smart_home.service.CenarioService;

@RestController
@RequestMapping({ "/cenarios", "/cenarios/" })
public class CenarioController {

    @Autowired
    private CenarioService cenarioService;

    /**
     * Listar todos os cenários
     * 
     * @return lista de cenários
     */
    @GetMapping
    public ArrayList<Cenario> obterCenarios() {
        return this.cenarioService.buscarTodos();
    }

    /**
     * Obter um cenário pelo id
     * 
     * @param cenarioId o ID do cenário
     * @return o cenário encontrado
     * @throws CenarioNaoEncontradoException se o cenário não for encontrado
     */
    @GetMapping("/{cenarioId}")
    public Cenario obterCenarioPorId(@PathVariable long cenarioId) {
        Cenario c = this.cenarioService.buscarPorId(cenarioId);
        if (c == null) {
            throw new CenarioNaoEncontradoException(cenarioId);
        }
        return c;
    }

    /**
     * Cadastrar um novo cenário
     *
     * @param c o cenário a ser cadastrado
     * @return o cenário cadastrado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cenario cadastrarCenario(@RequestBody Cenario c) {
        return this.cenarioService.cadastrarCenario(c);
    }

    /**
     * Aplica as ações de uma rotina definida por um cenário
     *
     * @param cenarioId o ID do cenário a ser aplicado
     * @return lista de estados dos dispositivos após a aplicação do cenário
     * 
     * @throws CenarioNaoEncontradoException se o cenário não for encontrado
     */
    @PatchMapping("/{cenarioId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> aplicarCenario(@PathVariable long cenarioId) {
        var rotina = this.cenarioService.aplicarCenario(cenarioId);
        if (rotina == null) {
            throw new CenarioNaoEncontradoException(cenarioId);
        }
        return rotina;
    }

    /**
     * Remove um cenário pelo seu ID 
     * 
     * @param cenarioId o ID do cenário a ser removido
     * @return o cenário removido
     * 
     * @throws CenarioNaoEncontradoException se o cenário não for encontrado
     */
    @DeleteMapping("/{cenarioId}")
    public Cenario removerCenario(@PathVariable long cenarioId) {
        Cenario removido = this.cenarioService.removerCenario(cenarioId);
        if (removido == null) {
            throw new CenarioNaoEncontradoException(cenarioId);
        }
        return removido;
    }

    /**
     * Atualiza um cenário existente.
     * 
     * @param cenarioId o ID do cenário a ser atualizado
     * @param c         os novos dados do cenário
     * @return o cenário atualizado
     * 
     * @throws CenarioNaoEncontradoException se o cenário não for encontrado
     */
    @PutMapping("/{cenarioId}")
    public Cenario atualizarCenario(@PathVariable long cenarioId, @RequestBody Cenario c) {
        Cenario atualizado = this.cenarioService.atualizarCenario(cenarioId, c);
        if (atualizado == null) {
            throw new CenarioNaoEncontradoException(cenarioId);
        }
        return atualizado;
    }

}

@ControllerAdvice
class CenarioNaoEncontradoAdvice {

    @ResponseBody
    @ExceptionHandler(CenarioNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String cenarioNaoEncontrado(CenarioNaoEncontradoException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DispositivoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String dispositivoNaoEncontrado(DispositivoNaoEncontradoException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler({ RotinaNaoEncontradaException.class, RotinaInvalidaException.class,
            IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String rotinaInvalida(Exception ex) {
        return ex.getMessage();
    }
}