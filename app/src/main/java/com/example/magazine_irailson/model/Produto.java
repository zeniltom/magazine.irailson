package com.example.magazine_irailson.model;


import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Produto implements Serializable {


    private String id;
    private String nome;
    private String sku;
    private Double valorUnitario;
    private Double quantidadeEstoque;
    private boolean Kg;
    private boolean Unidade;

    @Override
    public String toString() {
        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        moeda.setCurrency(Currency.getInstance(new Locale("pt", "BR")));

        return sku.toUpperCase() + "/" + nome + " | " + " qtd: " + quantidadeEstoque + " [ " + moeda.format(valorUnitario) + " ]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.toUpperCase();
    }


    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }


    public Double getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Double quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public boolean isKg() {
        return Kg;
    }

    public void setKg(boolean kg) {
        Kg = kg;
    }

    public boolean isUnidade() {
        return Unidade;
    }

    public void setUnidade(boolean unidade) {
        Unidade = unidade;
    }
}