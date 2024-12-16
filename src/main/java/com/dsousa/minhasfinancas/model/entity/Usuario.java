package com.dsousa.minhasfinancas.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table( name = "usuario", schema = "financas")
@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Usuario {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    @JsonIgnore
    private String senha;

 public static void main (String[] args){
     Usuario usuario= new Usuario();
     usuario.setEmail("douglas@email.com");
     usuario.setNome("usuario");
     usuario.setSenha("senha");
 }

}


