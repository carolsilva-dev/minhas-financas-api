package com.dsousa.minhasfinancas.api.resource;


import com.dsousa.minhasfinancas.api.dto.UsuarioDto;
import com.dsousa.minhasfinancas.exception.ErroAutentificacao;
import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.service.LancamentoService;
import com.dsousa.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final LancamentoService lancamentoService;

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDto dto) {
       try {
         Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
         return ResponseEntity.ok(usuarioAutenticado);
    } catch(ErroAutentificacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @PostMapping
      public ResponseEntity salvar ( @RequestBody UsuarioDto dto) {
      Usuario usuario = Usuario.builder()
              .nome(dto.getNome())
              .email(dto.getEmail())
              .senha(dto.getSenha()).build();

      try {
          Usuario usuarioSalvo = service.salvarUsuario(usuario);
          ResponseEntity responseEntity = new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
          return responseEntity;
      } catch (RegraNegocioException e) {
          return ResponseEntity.badRequest().body(e.getMessage());
      }
  }

  @GetMapping("{id}/saldo")
  public ResponseEntity obterSaldo( @PathVariable("id") Long id) {
     Optional<Usuario> usuario =  service.obterPorId(id);

     if(!usuario.isPresent()) {
         return new ResponseEntity(HttpStatus.NOT_FOUND);
     }
      BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
     return ResponseEntity.ok(saldo);
  }
}

