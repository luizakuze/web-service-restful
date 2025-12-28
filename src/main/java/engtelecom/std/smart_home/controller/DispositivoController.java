package engtelecom.std.smart_home.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import engtelecom.std.smart_home.entities.Dispositivo;
import engtelecom.std.smart_home.exceptions.AmbienteTipoInvalidoException;
import engtelecom.std.smart_home.exceptions.DispositivoNaoEncontradoException;
import engtelecom.std.smart_home.exceptions.TelevisaoEventosExclusivosException;
import engtelecom.std.smart_home.service.DispositivoService;

@RestController
@RequestMapping({ "/dispositivos", "/dispositivos/" })
public class DispositivoController {
    @Autowired
    private DispositivoService dispositivoService;

    /**
     * Obter os dispositivos 
     *
     * @return lista de dispositivos
     */
    @GetMapping
    public ArrayList<Dispositivo> obterDispositivos() {
        return this.dispositivoService.buscarTodos();
    }

    /**
     * Obter um dispositivo pelo id
     * 
     * @param id o ID do dispositivo
     * @return o dispositivo encontrado
     * 
     * @throws DispositivoNaoEncontradoException se o dispositivo não for encontrado
     * 
     */
    @GetMapping("/{dispositivoId}")
    @ResponseStatus(HttpStatus.OK)
    public Dispositivo obterDispositivo(@PathVariable("dispositivoId") long id) {
        Dispositivo d = this.dispositivoService.buscarPorId(id);
        if (d != null) {
            return d;
        }
        throw new DispositivoNaoEncontradoException(id);
    }

    /**
     * Atualizar um dispositivo pelo id
     * 
     * @param dispositivoId o ID do dispositivo
     * @param body os novos dados do dispositivo
     * @return o dispositivo atualizado
     * 
     * @throws DispositivoNaoEncontradoException se o dispositivo não for encontrado
     */
    @PutMapping("/{dispositivoId}")
    @ResponseStatus(HttpStatus.OK)
    public Dispositivo atualizarDispositivo(
            @PathVariable("dispositivoId") Long dispositivoId,
            @RequestBody Map<String, Object> body) {
        return dispositivoService.substituirDispositivo(dispositivoId, body);
    }
}

@ControllerAdvice
class DispositivoExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(DispositivoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String dispositivoNaoEncontrado(DispositivoNaoEncontradoException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler({
        AmbienteTipoInvalidoException.class,
        TelevisaoEventosExclusivosException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String erroDeRegraDeNegocio(RuntimeException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler({
        IllegalArgumentException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String requisicaoInvalida(Exception ex) {
        return "Requisição inválida.";
    }
}
