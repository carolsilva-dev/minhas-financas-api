package com.dsousa.minhasfinancas.service.impl;

import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Lancamento;
import com.dsousa.minhasfinancas.model.enums.StatusLancamento;
import com.dsousa.minhasfinancas.model.enums.TipoLancamento;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepository;
import com.dsousa.minhasfinancas.service.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRepository repository;


    @Autowired
    public LancamentoServiceImpl(LancamentoRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        lancamento.setDataCadastro(LocalDate.now());
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentofiltro) {
        Example example = Example.of( lancamentofiltro ,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) ) ;
        return repository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus(status);
        atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {
        if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
            throw new RegraNegocioException("Informe uma descrição valida.");
        }
        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
            throw new RegraNegocioException("Informe um Mês valido.");
        }
        if(lancamento.getAno() == null || lancamento.getAno().toString().length() !=4 )  {
            throw new RegraNegocioException("Informe um Ano valido.");
        }
        if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
            throw new RegraNegocioException("Informe um Usuario");
        }
        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) <1) {
            throw new RegraNegocioException("Informe um Valor valido");
        }
        if(lancamento.getTipo() == null) {
            throw new RegraNegocioException("Informe um tipo de Lancamento");
        }

    }
    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repository.findAllById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {
       BigDecimal receitas =  repository.obterSaldoPorTipoLancamentoeUsuarioeStatus(id, TipoLancamento.RECEITA,StatusLancamento.EFETIVADO);
       BigDecimal despesas =  repository.obterSaldoPorTipoLancamentoeUsuarioeStatus(id, TipoLancamento.DESPESA,StatusLancamento.EFETIVADO);

       if(receitas == null) {
           receitas = BigDecimal.ZERO;
       }
       if (despesas == null) {
           despesas = BigDecimal.ZERO;
       }
       return receitas.subtract(despesas);
    }
}

