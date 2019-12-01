package com.example.magazine_irailson.model;

import java.io.Serializable;


public class Usuario implements Serializable {

    private String id;
    private String email;
    private String nome;
    private String senha;

    public Usuario() {

    }

    @Override
    public String toString() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
