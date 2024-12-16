package com.dsousa.minhasfinancas.service.impl;

import com.dsousa.minhasfinancas.exception.ErroAutentificacao;
import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.repository.UsuarioRepository;
import com.dsousa.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

      if (!usuario.isPresent()) {
          throw new ErroAutentificacao("Usuario não encontrado.");
    }
      if(!usuario.get().getSenha().equals(senha)) {
          throw new ErroAutentificacao("Senha inválida.");
      }
      return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Ja existe um usuario cadastado com este email");
        }
    }
    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findAllById(id);
    }

}
