package com.dsousa.minhasfinancas.service;


import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.repository.UsuarioRepository;
import com.dsousa.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

   @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;


    @Test(expected = Test.None.class)
   public void deveSalvarUmUsuario() {
       //cenario
       Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
       Usuario usuario = Usuario.builder()
               .id(1L)
               .nome("nome")
               .email("email@email.com")
               .senha("senha").build();

       Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

       //ação
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

       //verificação
       Assertions.assertThat(usuarioSalvo).isNotNull();
       Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
       Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
       Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
       Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");

   }

   @Test(expected = RegraNegocioException.class)
   public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
        //cenario
       String email = "email@email.com";
       Usuario usuario = Usuario.builder().email(email).build();
       Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

       //ação
       service.salvarUsuario(usuario);

       //verificação
       Mockito.verify( repository, Mockito.never() ).save(usuario);
   }

   @Test(expected = Test.None.class)
   public void deveAutenticarUsuarioComSucesso() {
       //cenario
       String email = "email@email.com";
       String senha = "senha";

       Usuario usuario = Usuario.builder().email(email).senha(senha).id(11L).build();
       Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));

       //ação
      Usuario result = service.autenticar(email, senha);

       //Verificação
       Assertions.assertThat(result).isNotNull();
   }


    @Test(expected = Test.None.class)
    public void DeveValidarEmail() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //ação
        service.validarEmail("email@email.com");
    }

    @Test(expected = RegraNegocioException.class)
   public void DeveLancarErroQuandoValidarEmailCadastrado() {
      //cenario
       Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        service.validarEmail("email@email.com");
   }
}
