package com.dsousa.minhasfinancas.model.repository;

import com.dsousa.minhasfinancas.model.entity.Lancamento;

import com.dsousa.minhasfinancas.model.enums.StatusLancamento;
import com.dsousa.minhasfinancas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findAll(Example example);

    Optional<Lancamento> findAllById(Long id);

    @Query( value =
             " select sum(l.valor) from Lancamento l join l.usuario u"
          +  " where u.id = :idUsuario and l.tipo =:tipo and l.status = :status group by u")
    BigDecimal obterSaldoPorTipoLancamentoeUsuarioeStatus(
             @Param("idUsuario") Long idUsuario,
             @Param("tipo") TipoLancamento tipo,
             @Param("status") StatusLancamento Status
    );
}
