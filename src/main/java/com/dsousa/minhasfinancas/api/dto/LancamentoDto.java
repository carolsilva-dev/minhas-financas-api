package com.dsousa.minhasfinancas.api.dto;

import lombok.*;

import java.math.BigDecimal;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDto {

     private Long id;
     private String descricao;
     private Integer mes;
     private Integer ano;
     private BigDecimal valor;
     private Long usuario;
     private String tipo;
     private String status;

}
