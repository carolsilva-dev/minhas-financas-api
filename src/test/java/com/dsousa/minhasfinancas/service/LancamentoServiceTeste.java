package com.dsousa.minhasfinancas.service;


import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Lancamento;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.enums.StatusLancamento;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepository;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.dsousa.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("Test")
public class LancamentoServiceTeste {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        //cenario
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        service.salvar(lancamentoASalvar);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        //exxecução
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        //verificação
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);

     Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar),RegraNegocioException.class );

     Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizarUmLancamento() {
        //cenario
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        //exxecução
        service.atualizar(lancamentoSalvo);

        //verificação
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualzarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

        Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar),NullPointerException.class );
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveDeletarUmLancamento() {
         Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
         lancamento.setId(1l);
         service.deletar(lancamento);
         Mockito.verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        Assertions.catchThrowableOfType(() -> service.deletar(lancamento),NullPointerException.class );
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        Assertions.assertThat(resultado).isNotEmpty()
                .hasSize(1)
                .contains(lancamento);
    }

    @Test
     public void deveAtualizarOsStatusDeUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamnetoPorID() {

        long id = (1l);

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        Mockito.when(repository.findAllById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {

        long id = (1l);

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        Mockito.when(repository.findAllById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErroAoValidarUmLancamneto() {
         Lancamento lancamento = new Lancamento();

         Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
         Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição valida.");

         lancamento.setDescricao("");

         erro = Assertions.catchThrowable( () -> service.validar(lancamento));
         Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição valida.");
         lancamento.setDescricao("salario");


          erro = Assertions.catchThrowable( () -> service.validar(lancamento));
          Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido.");

          lancamento.setAno(0);

         erro = Assertions.catchThrowable( () -> service.validar(lancamento));
         Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido.");

         lancamento.setAno(13);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido.");

        lancamento.setMes(1);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
         Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");

         lancamento.setAno(202);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");

        lancamento.setAno(2020);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario");

        lancamento.setUsuario(new Usuario());

         erro = Assertions.catchThrowable( () -> service.validar(lancamento));
         Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario");

         lancamento.setUsuario(new Usuario());
         lancamento.getUsuario().setId(1l);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor valido");

        lancamento.setValor(BigDecimal.ZERO);

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor valido");

        lancamento.setValor(BigDecimal.valueOf(2));

        erro = Assertions.catchThrowable( () -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lancamento");

    }
}
