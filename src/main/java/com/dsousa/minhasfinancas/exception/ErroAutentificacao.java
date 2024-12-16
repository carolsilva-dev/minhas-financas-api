package com.dsousa.minhasfinancas.exception;

public class ErroAutentificacao extends RegraNegocioException{

   public ErroAutentificacao(String mensagem){
       super(mensagem);
   }
}
