package com.dsousa.minhasfinancas.api.resource;


import com.dsousa.minhasfinancas.api.dto.AtualizarStatusDto;
import com.dsousa.minhasfinancas.api.dto.LancamentoDto;
import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Lancamento;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.enums.StatusLancamento;
import com.dsousa.minhasfinancas.model.enums.TipoLancamento;
import com.dsousa.minhasfinancas.service.LancamentoService;
import com.dsousa.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descrição", required = false) String descrição,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
    ) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descrição);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possivel realizar consulta. Usuario não encontrado");
        }else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity obterLancamento( @PathVariable("id") String id) {
       return service.obterPorId( Long.valueOf(id) )
               .map( lancamento -> new ResponseEntity(converter(lancamento),HttpStatus.OK) )
               .orElseGet( () -> new ResponseEntity (HttpStatus.NOT_FOUND) );
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDto dto) {
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage()) ;
        }
    }

     @PutMapping("{id}")
     public ResponseEntity atualizar(@PathVariable("id") String id, @RequestBody LancamentoDto dto ) {
       return  service.obterPorId( Long.valueOf(id) ).map(entity -> {
           try {
               Lancamento lancamento = converter(dto);
               lancamento.setId(entity.getId());
               service.atualizar(lancamento);
               return ResponseEntity.ok(lancamento);
           }catch (RegraNegocioException e) {
               return ResponseEntity.badRequest().body(e.getMessage());
           }
         }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de da dados.", HttpStatus.BAD_REQUEST) );
     }

     @PutMapping("{id}/atualizar-status")
     public ResponseEntity atualizarStatus(@PathVariable("id") String id, @RequestBody AtualizarStatusDto dto) {
         return service.obterPorId( Long.valueOf(id) ).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possivel atualizar o status do lançamento, envie um status valido");
            }
            try {
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
         }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de da dados.", HttpStatus.BAD_REQUEST) );
     }

      @DeleteMapping("{id}")
      public ResponseEntity deletar(@PathVariable("id") String id) {
         return service.obterPorId( Long.valueOf(id) ).map(entidade -> {
             service.deletar(entidade);
             return new ResponseEntity(HttpStatus.NO_CONTENT);
         }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de da dados.", HttpStatus.BAD_REQUEST));
      }

      private LancamentoDto converter (Lancamento lancamento) {
        return LancamentoDto.builder()
                            .id(lancamento.getId())
                            .descricao(lancamento.getDescricao())
                            .valor(lancamento.getValor())
                            .mes(lancamento.getMes())
                            .ano(lancamento.getAno())
                            .status(lancamento.getStatus().name())
                            .tipo(lancamento.getTipo().name())
                            .usuario(lancamento.getUsuario().getId())
                            .build();
      }


     private Lancamento converter(LancamentoDto dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuario não encontrado para o id informado"));

        lancamento.setUsuario(usuario);
            if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
            if(dto.getStatus() != null) {
                lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
            }
          }
         return lancamento;
      }
   }

